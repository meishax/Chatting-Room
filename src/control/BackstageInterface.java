package control;


import util.MessageUdpUtil;
import util.MessageUdpUtil.ClientInfo;

/**
 * 后台接口类
 * @author Mika
 * @since 2021-1-8 10:24:08
 */
public interface BackstageInterface {
	public void returnChoose(int whichWindow);
	/* 登录请求调用方法 */
	public void loginRequest(String nickname);
	public void loadChatWindow();
	/* 扫描服务器调用方法 */
	public boolean scanServer();
	public void sendUdpMessage(String message);
	public void sendMessage(String message);
	public void sendMessagePrivate(String message, String nickname);
	public void udpCallBack(String receiveString);
	public void CallBack(String receiveString, ClientInfo userInfo);
	public String getNickname();
	public void setEchoMessageInterface(WindowInterface wif);
	public void someEnterOrLeave(String nickname, boolean eol);
	public void add2ServerList(ClientInfo clientinfo);
	public void deleteFromServerList(String nickname);
}
