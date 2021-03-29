package model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ��Ϣ����
 * @author Mika
 * @since 2021-1-11 13:09:45
 */

public class MessageQueue extends LinkedBlockingQueue{
	private  static ExecutorService es = Executors.newFixedThreadPool(10);
	private static MessageQueue mq = new MessageQueue();
	private boolean flag = false;
	
	private MessageQueue(){};
	//��ȡ����ʵ��
	public static MessageQueue getInstance(){
		return mq;
	}
	//��������
	public void start(){
		if(!this.flag){
			this.flag = true;
		}else{
			throw new IllegalArgumentException("�����Ѵ�������״̬���������ظ�����");
		}
		new Thread(new Runnable(){
			@Override
			public void run(){
				while(flag){
					
						Object obj;
						try {
							obj = take();//ʹ������ģʽ��ȡ������Ϣ
							es.execute(new PushBlockQueueHandler(obj));//����ȡ����Ϣ�����̳߳ش���
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
				}
			}
		}).start();
	}
	//ֹͣ���м���
	public void stop(){
		this.flag = false;
	}
	
	/**
	 * ���д�����
	 * @author Hlccare
	 *
	 */
	class PushBlockQueueHandler implements Runnable{
		
		private Object obj;
		public PushBlockQueueHandler(Object obj){
			this.obj = obj;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			doBussiness();
		}
		//������Ϣ������
		public void doBussiness() {
			// TODO Auto-generated method stub
			System.out.println(obj);
		}
	}
	//���Դ���
public static void main(String[] args) throws Exception{
		getInstance().start();
		for(;;){
			Thread.sleep(1000);
			getInstance().put("10086");
		}
	}
}
