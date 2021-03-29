package model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息队列
 * @author Mika
 * @since 2021-1-11 13:09:45
 */

public class MessageQueue extends LinkedBlockingQueue{
	private  static ExecutorService es = Executors.newFixedThreadPool(10);
	private static MessageQueue mq = new MessageQueue();
	private boolean flag = false;
	
	private MessageQueue(){};
	//获取队列实例
	public static MessageQueue getInstance(){
		return mq;
	}
	//启动队列
	public void start(){
		if(!this.flag){
			this.flag = true;
		}else{
			throw new IllegalArgumentException("队列已处于启动状态，不允许重复启动");
		}
		new Thread(new Runnable(){
			@Override
			public void run(){
				while(flag){
					
						Object obj;
						try {
							obj = take();//使用阻塞模式获取队列消息
							es.execute(new PushBlockQueueHandler(obj));//将获取的消息交由线程池处理
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
				}
			}
		}).start();
	}
	//停止队列监听
	public void stop(){
		this.flag = false;
	}
	
	/**
	 * 队列处理器
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
		//具体消息处理方法
		public void doBussiness() {
			// TODO Auto-generated method stub
			System.out.println(obj);
		}
	}
	//测试代码
public static void main(String[] args) throws Exception{
		getInstance().start();
		for(;;){
			Thread.sleep(1000);
			getInstance().put("10086");
		}
	}
}
