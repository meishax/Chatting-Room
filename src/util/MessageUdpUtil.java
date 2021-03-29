package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import control.BackstageInterface;
import util.MessageConstructor.Msg;
import model.*;

import javax.imageio.IIOException;
import javax.xml.crypto.Data;
import org.apache.commons.codec.*;


/**
 * 不同通信情况的构造
 * @author Mika
 * @since 2021-1-9 13:25:54
 */



public class MessageUdpUtil {
    private static MessageUdpUtil MessageUdpUtil;
    private List<ClientInfo> clientinfolist = new ArrayList<ClientInfo>();
    private BackstageInterface backstageInterface;
    ClientInfo userinfo;
    /**
     * 服务端用的构造器
     */
    private MessageUdpUtil(int port, BackstageInterface bif) {
        backstageInterface = bif;
        try {
            final DatagramSocket ds = new DatagramSocket(port);
            //监听该端口
            for(int i = 0;i<10;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        byte buf[] = new byte[1024];
                        DatagramPacket dp = new DatagramPacket(buf,1024);
                        while(true) {
                            try {
                                ds.receive(dp);//返回连接的info对象
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                dp = null;
                                e.printStackTrace();
                            }
                            if(dp != null) {
                                System.out.println(dp.getPort());
                                ClientInfo newClientInfo = new ClientInfo(dp.getAddress().getHostAddress(),dp.getPort());
                                clientinfolist.add(newClientInfo);
                                System.out.println(newClientInfo.getInfo() + " connected..");
                                System.out.println(clientinfolist.size() + " person(s) in chatroom now.");
                                exec(newClientInfo);
                            }
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 客户端用的构造器
     * @param serverAddress 服务端地址
     * @param port 服务端端口
     */
    private MessageUdpUtil(String serverAddress, int port, BackstageInterface bif) {
        backstageInterface = bif;
        try {
            DatagramSocket ds = new DatagramSocket(port);
            InetAddress inet = InetAddress.getByName(serverAddress);
            new Thread(new Runnable() {
                @Override
                public void run(){

                    String msg = serverAddress + "-" + userinfo.port;
                    byte [] bytes = msg.getBytes();
                    DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,port);

                    try {
                        ds.send(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ds.close();

                }
            }).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //对服务器
    public static MessageUdpUtil getMessageUdpUtilOfServer(int port, BackstageInterface bif) {
        if(MessageUdpUtil == null) {
            return new MessageUdpUtil(port, bif);
        }
        return MessageUdpUtil;
    }

    //对客户端
    public static MessageUdpUtil getMessageUdpUtilOfClient(String serverAddress, int port, BackstageInterface bif) {
        if(MessageUdpUtil == null) {
            return new MessageUdpUtil(serverAddress, port, bif);
        }
        return MessageUdpUtil;
    }
    /* 登录昵称重名判断 */
    public boolean isContain(String nickname) {
        ClientInfo tmpInfo = new ClientInfo(nickname);
        if(clientinfolist.contains(tmpInfo)) {
            return true;
        }
        return false;
    }


    //根据昵称取info信息，以get()方式返回
    public ClientInfo getInfoByNickname(String nickname) {
        ClientInfo tmpInfo = new ClientInfo(nickname);
        int index = clientinfolist.indexOf(tmpInfo);
        if(index != -1) {
            return clientinfolist.get(index);
        }
        return null;
    }


    public void userBroadcast(final ClientInfo ci){
        Msg all = new Msg(0, null);
        for(ClientInfo sx : clientinfolist) {
            if(!sx.equals(ci)) {
                all = MessageConstructor.constructCurrentUserMsg(sx.getNickname(), all);
            }
        }
        final String msg = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.CURRRENT_USER_FEEDBACK, all.getCode() + "-" + all.getMessage());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    String ip = ci.getAddress();
                    InetAddress inet = InetAddress.getByName(ip);
                    byte [] bytes = msg.getBytes();
                    DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,ci.getPort());
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /* 向除去自己外的其他人发送消息 */
    public void sendMessageNoSelf(final String msg, final String nickname){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //PrintWriter writer = null;
                for(ClientInfo ci : clientinfolist) {
                    if(!ci.getNickname().equals(nickname)) {
                        try {
                            String ip = ci.getAddress();
                            InetAddress inet = InetAddress.getByName(ip);
                            byte [] bytes = msg.getBytes();
                            DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,ci.getPort());
                            DatagramSocket ds = new DatagramSocket();
                            ds.send(dp);
                            ds.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    public void sendMessage2(final String msg, final ClientInfo userInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    String ip = userInfo.getAddress();
                    InetAddress inet = InetAddress.getByName(ip);
                    byte [] bytes = msg.getBytes();
                    DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,userInfo.getPort());
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     *
     * @param msg 需要发送的消息
     * @param nickname 接收者昵称，null为全体
     */
    public void sendMessage(final String msg, final String nickname) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(nickname == null) {
                    for(ClientInfo ci : clientinfolist) {
                        try {
                            String ip = ci.getAddress();
                            InetAddress inet = InetAddress.getByName(ip);
                            byte [] bytes = msg.getBytes();
                            DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,ci.getPort());
                            DatagramSocket ds = new DatagramSocket();
                            ds.send(dp);
                            ds.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    ClientInfo clientInfo = getInfoByNickname(nickname);
                    if(clientInfo != null) {
                        try {
                            String ip = clientInfo.getAddress();
                            InetAddress inet = InetAddress.getByName(ip);
                            byte [] bytes = msg.getBytes();
                            DatagramPacket dp = new DatagramPacket(bytes,bytes.length,inet,clientInfo.getPort());
                            DatagramSocket ds = new DatagramSocket();
                            ds.send(dp);
                            ds.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    private void exec(final ClientInfo clientInfo) {//接收信息，回调
        new Thread(new Runnable(){
            @Override
            public void run(){
                byte buf[] = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, 1024);

                while(true){
                    try {
                        DatagramSocket ds = new DatagramSocket(clientInfo.getPort());
                        ds.receive(dp);
                        String msg = new String(buf,0,dp.getLength());
                        backstageInterface.udpCallBack(msg);
                    }catch(IOException e) {
                        System.out.println(clientInfo.getInfo() + " disconnected..");
                        clientinfolist.remove(clientInfo);
                        backstageInterface.someEnterOrLeave(clientInfo.getNickname(), false);
                        backstageInterface.deleteFromServerList(clientInfo.getNickname());
                        System.out.println(clientinfolist.size() + " person(s) in chatroom now.");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static class ClientInfo{
        private String ipAddress;
        public Integer port;
        private String nickname;

        private ClientInfo(String address,int port){
            ipAddress = address;
            this.port = port;
        }
        /* 判断时构造 */
        public ClientInfo(String nickname) {
            this.nickname = nickname;
        }
        /* 连接时构造 */
		/*public ClientInfo(String address,int port) {
			//this(address, port);
			this.ipAddress = address;
			this.port = port;
		}*/
        public String getInfo() {
            return ipAddress + ":" + port;
        }
        public String getAddress() {
            return ipAddress;
        }
        public int getPort() {
            return port;
        }
        public void setNickname(String nk) {
            nickname = nk;
        }
        public String getNickname() {
            return nickname;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(nickname);
            sb.append('(');
            sb.append(ipAddress);
            sb.append(':');
            sb.append(String.valueOf(port));
            sb.append(')');
            return sb.toString();
        }
        @Override
        public boolean equals(Object anObject) {
            if(this == anObject) {
                return true;
            }
            if(anObject instanceof ClientInfo) {
                ClientInfo ci = (ClientInfo)anObject;
                if(this.nickname.equals(ci.nickname)) {
                    return true;
                }
            }
            return false;
        }
    }
}
