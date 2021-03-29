package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import util.HtmlUtil;
import util.MessageConstructor;
import util.MessageConstructor.Msg;
import util.MessageUdpUtil.ClientInfo;
import util.NetworkUtil;
import util.MessageUdpUtil;
import util.UdpUtil;
import view.*;

import javax.imageio.IIOException;



/**
 * 后台类，控制类中最关键的类
 * @author Mika
 * @since 2021-1-8 8:45:10
 */

public class Backstage implements BackstageInterface {
	private boolean isServerMode;
	private static Backstage backstage;
	private WindowInterface window;
	private UdpUtil udpUtil;
	private MessageUdpUtil messageUdpUtil;
	private String serverAddress;
	private int serverPort;
	private String nickname;
	
	public static void main(String[] args) {
		backstage = new Backstage();
		Choose._main(backstage);
	}
	private Backstage() {
		try {
			serverAddress = NetworkUtil.getLocalHostLANAddress().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverPort = 7747;
		/**
		   服务器端口使用固定的7747，如果总是不能scan到server，考虑端口是否占用
		  初始化socket(udp)部分
         */

		udpUtil = new UdpUtil(this);
	}

	/**
	 * Choose 窗口调用
	 * @param whichWindow 选择启用的窗口
     * 选择启用客户端还是服务器端
	 */
	@Override
	public void returnChoose(int whichWindow) {
		// TODO Auto-generated method stub
		if(whichWindow == Choose.CLIENT) {
			System.out.println("CLIENT");
			isServerMode = false;
			LoginWindow._main(this);
		}
		if(whichWindow == Choose.SERVER) {
			System.out.println("SERVER");
			isServerMode = true;
			messageUdpUtil = MessageUdpUtil.getMessageUdpUtilOfServer(serverPort, this);
			ServerWindow._main(this);
		}
	}


	/**
	 * LoginWindow 窗口调用
	 * @param nickname 登录的昵称
	 * @return 是否允许登录。
	 */
	@Override
	public void loginRequest(String nickname) {
		// TODO Auto-generated method stub
		this.nickname = nickname;
		if(messageUdpUtil == null) {
			messageUdpUtil = MessageUdpUtil.getMessageUdpUtilOfClient(serverAddress, serverPort, this);
		}
		sendMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.LOGIN_REQUEST, nickname));
	}


	/**
	 * LoginWindow 窗口调用
	 * @return 返回扫描到的服务器地址:端口。
	 */
	@Override
	public boolean scanServer() {
		// TODO Auto-generated method stub
//		serverAddressAndPort = null;
		serverPort = -1;
		sendUdpMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDP.REQUEST_SERVER_ADDRESS_AND_PORT, ""));
		/**
		 * 超时控制代码，自己写的，网上没找到好用的，循环10次，每次睡眠300毫秒
         * 超时后未找到服务器（服务器不在线，not found）
		 */
		for(int i=0;i<10;i++) {
			try {
				Thread.sleep(300);
				if(serverPort != -1) {
					return true;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	@Override
	public void loadChatWindow() {
		// TODO Auto-generated method stub
		ChatWindow._main(this);
	}
	@Override
	public void udpCallBack(String receiveString) {
		// TODO Auto-generated method stub
		/**
		 * 服务器接收客户端的获取请求和客户端接收服务器的反馈
		 * 附加文本分割和正则表达
		 * */
		Msg msg = MessageConstructor.parseMessage(receiveString);
		System.out.println(receiveString);
		if(isServerMode) {
			if(msg.getCode() == MessageConstructor.Code.UDP.REQUEST_SERVER_ADDRESS_AND_PORT) {
				String newMsg = serverAddress + ":" + serverPort;
				sendUdpMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDP.SERVER_ADDRESS_AND_PORT_FEEDBACK, newMsg));
			}
		}
		else {
			if(msg.getCode() == MessageConstructor.Code.UDP.SERVER_ADDRESS_AND_PORT_FEEDBACK) {
				Msg tmp = MessageConstructor.parseIPAP(msg.getMessage());
				serverAddress = tmp.getMessage();
				serverPort = tmp.getCode();
			}
		}
	}

	@Override
	public void sendUdpMessage(String message) {
		// TODO Auto-generated method stub
		udpUtil.sendUdpPacket(message);
	}
	@Override
	public void sendMessage(String message) {
		// TODO Auto-generated method stub
		messageUdpUtil.sendMessage(message, null);
	}
	@Override
	public void sendMessagePrivate(String message, String nickname) {
		// TODO Auto-generated method stub
		messageUdpUtil.sendMessage(message, nickname);
	}
	private void sendMessagePrivate(String message, ClientInfo userInfo) {
		// TODO Auto-generated method stub
		messageUdpUtil.sendMessage2(message, userInfo);
	}
	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return nickname;
	}
	@Override
	public void setEchoMessageInterface(WindowInterface wif) {
		// TODO Auto-generated method stub
		window = wif;
	}
	@Override
	public void CallBack(String receiveString, ClientInfo userInfo) {
		// TODO Auto-generated method stub
		Msg msg = MessageConstructor.parseMessage(receiveString);
		System.out.println(receiveString);
		if(isServerMode){
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.MESSAGE_FROM_CLIENT_TO_SERVER) {
				String newMessage = HtmlUtil.addUserInfo(userInfo.toString(), msg.getMessage());
				window.echoMessage(newMessage, null);
				sendMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.MESSAGE_FROM_SERVER_TO_CLIENT, newMessage));
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.LOGIN_REQUEST) {
				String nickname = msg.getMessage();
				boolean loginStatus = !messageUdpUtil.isContain(nickname) && !nickname.contains("-") && !nickname.equals("所有人");//ban '-'/"所有人" ,防止影响数据传输
				if(loginStatus) {
					/* 允许登录 */
					userInfo.setNickname(nickname);
					add2ServerList(userInfo);
					someEnterOrLeave(nickname, true);
				}
				sendMessagePrivate(MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.LOGIN_FEEDBACK, String.valueOf(loginStatus)), userInfo);
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.MESSAGE_PRIVATE) {
				String ori = msg.getMessage();
				String[] arr = MessageConstructor.parsePrivateMessage(ori);
				String newMsg = HtmlUtil.addUserInfo(userInfo.toString(), arr[1]);
				String umsg = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.MESSAGE_PRIVATE,
						MessageConstructor.constructPrivateMessage(newMsg, userInfo.getNickname()));
				String umsg2 = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.MESSAGE_PRIVATE,
						MessageConstructor.constructPrivateMessage(newMsg, arr[0]));
				sendMessagePrivate(umsg, arr[0]); /* 投递私聊信息至目的地 */
				sendMessagePrivate(umsg2, userInfo); /* 回显私聊信息至发送端 */
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.CURRENT_USER_REQUEST) {
					messageUdpUtil.userBroadcast(userInfo);
			}
		}
		else {
			ClientInfo fakeBooleanTure = new ClientInfo("");
			ClientInfo fakeBooleanFalse = null;
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.MESSAGE_FROM_SERVER_TO_CLIENT) {
				window.echoMessage(msg.getMessage(), null);
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.LOGIN_FEEDBACK) {
				boolean status = Boolean.parseBoolean(msg.getMessage());
				window.otherFunc(status);
				if(status == true) {
					//获取当前用户请求
					sendMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.CURRENT_USER_REQUEST, ""));
				}
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.MESSAGE_PRIVATE) {
				String ori = msg.getMessage();
				String[] arr = MessageConstructor.parsePrivateMessage(ori);
				window.echoMessage(arr[1], arr[0]);
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.USER_ONLINE) {
				window.addOrDeleteListItem(fakeBooleanTure, msg.getMessage());
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.USER_OFFLINE) {
				window.addOrDeleteListItem(fakeBooleanFalse, msg.getMessage());
			}
			if(msg.getCode() == MessageConstructor.Code.UDPMessage.CURRRENT_USER_FEEDBACK) {
				List<String> nicknameSet = MessageConstructor.parseCurrentUserMsg(msg.getMessage());
				for(String s : nicknameSet) {
					window.addOrDeleteListItem(fakeBooleanTure, s);
				}
			}
		} 
	}
	@Override
	public void someEnterOrLeave(String nickname, boolean eol) {
		// TODO Auto-generated method stub
		if(isServerMode) {
			String s;
			String msg;
			/* 用户进入 */
			if(eol) {
				msg = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.USER_ONLINE, nickname);
				s = HtmlUtil.welcome(nickname);
			}
			/* 用户离开 */
			else {
				msg = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.USER_OFFLINE, nickname);
				s = HtmlUtil.leave(nickname);
			}
			window.echoMessage(s, null);
			messageUdpUtil.sendMessageNoSelf(msg, nickname);
			sendMessage(MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.MESSAGE_FROM_SERVER_TO_CLIENT, s));
		}
		else if(!eol) {
			/* 服务器下线 */
			window.echoMessage(HtmlUtil.serverShutdown(), null);
			window.otherFunc(false);
		}
	}
	@Override
	public void add2ServerList(ClientInfo clientinfo) {
		// TODO Auto-generated method stub
		if(isServerMode) {
			window.addOrDeleteListItem(clientinfo, null);
		}
	}
	@Override
	public void deleteFromServerList(String nickname) {
		// TODO Auto-generated method stub
		if(isServerMode) {
			window.addOrDeleteListItem(null, nickname);
		}
	}
}
