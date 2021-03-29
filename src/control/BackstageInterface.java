package control;


import util.MessageUdpUtil;
import util.MessageUdpUtil.ClientInfo;

/**
 * ��̨�ӿ���
 * @author Mika
 * @since 2021-1-8 10:24:08
 */
public interface BackstageInterface {
	public void returnChoose(int whichWindow);
	/* ��¼������÷��� */
	public void loginRequest(String nickname);
	public void loadChatWindow();
	/* ɨ����������÷��� */
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
