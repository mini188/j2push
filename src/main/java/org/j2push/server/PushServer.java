package org.j2push.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Message.Type;

/**
 * 推送服务
 * @author xiexb
 *
 */
public class PushServer {
	private ComponentManager componentManager;
	//推送服务域名
	private String pushDomain;
	//发送线程池
	private PacketSenderThread readThread;
	//组件
	private Component comp;
	//订阅列表
	private Set<JID> subscriptions;
	//消息列表
	private BlockingQueue<Packet> packetQueue;
	
	
	public PushServer(String pushDomain, final ComponentManager componentManager) {
		this.pushDomain = pushDomain;
		this.componentManager = componentManager;
		comp = new PushComponent();
		packetQueue = new LinkedBlockingQueue<Packet>();
		subscriptions = new HashSet<JID>();
		//启动推送服务，打开线程池，从消息队列中抓取消息
		readThread = new PacketSenderThread(this.componentManager, comp, packetQueue);
		readThread.setDaemon(true);
	}
	
	public synchronized void start() {
		readThread.start();
	}

	public synchronized void stop() {
		readThread.shutdown();
	}
	
	public String getPushDomain() {
		return pushDomain;
	}

	public Component getComp() {
		return comp;
	}
	
	/**
	 * 将数据压入队列
	 * @param packet
	 * @throws InterruptedException
	 */
	public void putPacket(String body) throws InterruptedException {
		//向所有订阅用户发送
		for(JID jid: subscriptions) {
			Message copy = new Message();
			copy.setType(Type.chat);
			copy.setFrom("push@push.bkos");
			copy.setTo(jid);
			copy.setBody(body);
			packetQueue.put(copy);
		}
	}
	
	public synchronized void subscription(JID jid) {
		subscriptions.add(jid);
	}
	
	public synchronized void unsubscription(JID jid) {
		subscriptions.remove(jid);
	}

	private class PacketSenderThread extends Thread {
		private volatile Boolean shutdown = false;
		private BlockingQueue<Packet> queue;
		private Component component;
		private ComponentManager componentManager;

		public PacketSenderThread(ComponentManager componentManager, Component component, BlockingQueue<Packet> queue) {
			this.componentManager = componentManager;
			this.component = component;
			this.queue = queue;
		}
		
		public void run() {
			while (!shutdown) {
				Packet p;
				try {
					p = queue.take();
					componentManager.sendPacket(component, p);
				} catch (InterruptedException e1) {
					System.err.println(e1.getStackTrace());
				} catch (ComponentException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void shutdown() {
			shutdown = true;
			this.interrupt();
		}
		
	}
	
}
