package com.alier.com.commons.net;


import android.os.Handler;
import android.os.Message;

import com.alier.com.commons.biz.MQEventListener;
import com.alier.com.commons.utils.LogMgr;
import com.ibm.micro.client.mqttv3.MqttCallback;
import com.ibm.micro.client.mqttv3.MqttClient;
import com.ibm.micro.client.mqttv3.MqttConnectOptions;
import com.ibm.micro.client.mqttv3.MqttDeliveryToken;
import com.ibm.micro.client.mqttv3.MqttException;
import com.ibm.micro.client.mqttv3.MqttMessage;
import com.ibm.micro.client.mqttv3.MqttTopic;
import com.ibm.micro.client.mqttv3.internal.MemoryPersistence;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 实现与服务端ActiveMQ建立一个订阅关系，
 * 订阅的参数包括服务端的地址和端口、订阅的TOPIC名字
 * 
 * 实现接收消息之后的处理入口
 * @author xuan
 *
 */
public class MQClient {
	private String CONNECTION_STRING = null;
	private boolean CLEAN_START = false;
	private short KEEP_ALIVE = 0; //低耗网络，但是又需要及时获取数据，心跳30s
	private String CLIENT_ID = null;
	private String[] TOPICS =null;
	//QOS取值,与TOPICS对应
	//At Most Once (QoS=0)
	//At Least Once (QoS=1)
	//Exactly Once (QoS=2)
	private int[] QOS_VALUES = {2,2};
	private boolean SHOULD_ONLINE=true;	
	// MQ客户端
	private MqttClient mqttClient = null;
	private Thread reconnect=null; //重连接线程
	private Set<MQEventListener> listeners;// 监听器容器
	private Handler handler;
	public MQClient(String url, short keepAlive, String[] topics, int[] qos, String client, Handler mHandler){
		this.listeners = new HashSet<MQEventListener>();
		this.CONNECTION_STRING = url;
		this.KEEP_ALIVE = keepAlive;
		this.TOPICS = topics;
		this.QOS_VALUES = qos;
		this.CLIENT_ID = client;
		this.handler = mHandler;
		connectServer();
	}
	
	/**
	 * 给事件源注册监听器
	 * @param listener
	 */
	public void addEventListener(MQEventListener listener){
        this.listeners.add(listener);
    }
	
	/**
	 * 当事件发生时,通知注册在该事件源上的所有监听器做出相应的反应（调用回调方法）
	 * @param name
	 */
    protected void notifies(String name){
    	MQEventListener listener = null;
        Iterator<MQEventListener> iterator = this.listeners.iterator();
        while(iterator.hasNext()){
        	listener = iterator.next();
        	listener.receiveMessage(name);
        }
    }
	
    /**
     * 调用线程连接mqtt
     */
	public void connectServer(){
		// 正在尝试重连
		if(reconnect!=null && reconnect.isAlive())
			return;
		reconnect = new Thread(new ConnectMQTTClient());
		reconnect.start();
	}
	
	public void disConnect(){
		SHOULD_ONLINE = false;
		if(null == mqttClient)
			return;
		try {
			// 取消订阅
			// mqttClient.unsubscribe(TOPICS);
			mqttClient.disconnect();
			mqttClient = null;
			LogMgr.info("MQClient", "下线了!");
		} catch (MqttException e) {
			mqttClient = null;
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean isConnected(){
		if(null != mqttClient && mqttClient.isConnected()){
			return true;
		}
		return false;
	}
	
	private class MqttCallbackHandler implements MqttCallback{
		/**
		 * 当客户机和broker意外断开时触发
		 * 可以再次处理重新订阅
		 */
		public void connectionLost(Throwable arg0) {
			if(SHOULD_ONLINE){
		    	connectServer();
		    }
		}

		@Override
		public void deliveryComplete(MqttDeliveryToken arg0) {
			
		}

		@Override
		public void messageArrived(MqttTopic topic, MqttMessage mess)
				throws Exception {
			if (null == mess.getPayload()){
				return;
			}
			String sMsg = new String(mess.getPayload());
			notifies(sMsg);
		}
	}
	
	private class ConnectMQTTClient implements Runnable{
		@Override
		public void run() {
			while(SHOULD_ONLINE && (null==mqttClient || !mqttClient.isConnected())){
				try {
					MemoryPersistence persistence = new MemoryPersistence();
					mqttClient = new MqttClient(CONNECTION_STRING, CLIENT_ID, persistence);
					MqttConnectOptions options = new MqttConnectOptions();
//					options.setUserName("system");
//					options.setPassword("manager".toCharArray());
					options.setKeepAliveInterval(KEEP_ALIVE);
					options.setCleanSession(CLEAN_START);
					// 设置超时时间
					options.setConnectionTimeout(10000);
					MqttCallbackHandler simpleHandler = new MqttCallbackHandler();
				    mqttClient.setCallback(simpleHandler); // 注册接收消息方法
				    mqttClient.connect(options);
				    mqttClient.subscribe(TOPICS, QOS_VALUES);//订阅
					
				    if (mqttClient.isConnected()){
				    	LogMgr.info("MQClient", "上线了!");
				    }
				    if (null != handler){
				    	Message msg = handler.obtainMessage();
				    	msg.what = 0;
				    	msg.arg1 = 0;
				    	msg.sendToTarget();
				    }
				    try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				} catch (MqttException e) {
					if (null != handler){
						Message msg = handler.obtainMessage();
						msg.what = 0;
						// 同一个账号已经有人在登陆
						if (3 == e.getReasonCode()){
							msg.arg1 = 1;
							SHOULD_ONLINE = false;
						}else{
							msg.arg1 = 0;
						}
						msg.sendToTarget();
					}
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
					}
				}
			}
		}
		
	}

}
