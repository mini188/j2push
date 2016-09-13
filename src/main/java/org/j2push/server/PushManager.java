package org.j2push.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;

public class PushManager {
	private static PushManager _instance = new PushManager();
	private Map<String, PushServer> pushServers;
	private ExternalComponentManager manager;
	
	private PushManager() {
		pushServers = new ConcurrentHashMap<String, PushServer>();
		manager = new ExternalComponentManager("192.168.49.204", 5275);
        manager.setSecretKey("push", "test");
        manager.setMultipleAllowed("push", true);		
	}

	public static PushManager getInstance() {
		return _instance;
	}
	
	public void init() {
        try {
    		//初始化PushServer
    		PushServer pushSvr = new PushServer("push", manager);
    		pushServers.put("push", pushSvr);
    		//注册Component到xmpp服务器
    		manager.addComponent(pushSvr.getPushDomain(), pushSvr.getComp());
        } catch (ComponentException e) {
            e.printStackTrace();
        }
	}
	
	public PushServer getPushServer(String pushDomain) {
		return pushServers.get(pushDomain);
	}
}
