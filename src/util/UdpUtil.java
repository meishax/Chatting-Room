package util;

import control.BackstageInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * ����UDP�㲥������������
 * @author Mika
 * @since 2021-1-10 6:48:29
 */



public class UdpUtil {
	
	private static final String multicastHost="224.0.0.1";
	private static MulticastSocket ds;
	private InetAddress address;
	private BackstageInterface backstageInterface;
	
	/**
	 * ʵ����ʼ��ʱ����ӿ�ʵ��
	 * @param //ThreadCallBack�ӿڵ�ʵ��
	 */
	public UdpUtil(BackstageInterface bi) {
		try {
			address = InetAddress.getByName(multicastHost);  
			ds = new MulticastSocket(8004);
			ds.setTimeToLive(4);
			ds.joinGroup(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		backstageInterface = bi;
		startUdpReceive();
	}
	
	public void breakThread() {
		if (!ds.isClosed()) {
			ds.close();
		}
	}
	
	/**
	 * ��ʼ��������udp�㲥
	 */
	private void startUdpReceive() {
		try {  
            new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					byte buf[] = new byte[1024];  
			        DatagramPacket dp = new DatagramPacket(buf, 1024);  
			        while (true) {  
			            try {  
			            	ds.receive(dp); //�����ͼ���
			            	String msg = new String(buf, 0, dp.getLength());
			            	/* ��Ϣ�ص� */
			            	backstageInterface.udpCallBack(msg);
			            } catch (Exception e) {  
			                e.printStackTrace();  
			            }  
			        }  
				}
			}).start();  
        } catch (Exception e1) {  
            // TODO Auto-generated catch block
            e1.printStackTrace();  
        }
	}
	/**
	 * ����Udp�㲥
	 * @param msg ��Ҫ���͵��ַ�����Ϣ
	 */
	public void sendUdpPacket(String msg) {
		byte[] data = msg.getBytes();
		final DatagramPacket dataPacket = new DatagramPacket(data, data.length, address, 8004);
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ds.send(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
	}

}
