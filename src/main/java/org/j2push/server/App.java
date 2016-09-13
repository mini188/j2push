package org.j2push.server;

import org.xmpp.packet.JID;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		PushManager.getInstance().init();
		//推送消息
		PushServer ps = PushManager.getInstance().getPushServer("push");
		ps.start();
		
		JID client = new JID("100069@bkos");
		ps.subscription(client);
		
		JID client1 = new JID("1twja8e8yr@bkos/1twja8e8yr");
		ps.subscription(client1);
		try {
			for (Integer i = 0; i< 200; i++) {
				ps.putPacket("推送消息200:" + i.toString());
				Thread.sleep(1);
			}
            Thread.sleep(5000);
            
//			for (Integer i = 0; i< 500; i++) {
//				ps.putPacket("推送消息500:" + i.toString());
//			}
//            Thread.sleep(5000);    
//            
//			for (Integer i = 0; i< 1000; i++) {
//				ps.putPacket("推送消息1000:" + i.toString());
//			}            
//            Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ps.stop();
        System.out.println("go die");
    }
}
