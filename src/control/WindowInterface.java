package control;
/**
 * ���ڽӿ���
 * @author Mika
 * @since 2021-1-8 20:07:11
 */
import util.MessageUdpUtil.ClientInfo;

public interface WindowInterface {
	public void echoMessage(String message, String nickname);
	/* ��������кü����÷���ƴ�ӳ������ٽӿ����� */
	public void otherFunc(boolean b);
	public void addOrDeleteListItem(ClientInfo clientInfo, String nickname);
}
