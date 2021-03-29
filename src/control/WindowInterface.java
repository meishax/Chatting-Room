package control;
/**
 * 窗口接口类
 * @author Mika
 * @since 2021-1-8 20:07:11
 */
import util.MessageUdpUtil.ClientInfo;

public interface WindowInterface {
	public void echoMessage(String message, String nickname);
	/* 这个方法有好几个用法，拼接出来减少接口数量 */
	public void otherFunc(boolean b);
	public void addOrDeleteListItem(ClientInfo clientInfo, String nickname);
}
