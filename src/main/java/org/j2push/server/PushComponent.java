package org.j2push.server;

import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Message;

public class PushComponent extends AbstractComponent{
	
	public PushComponent() {
	}

	@Override
	public String getDescription() {
		return "用于消息推送服务组件，主要功能就是将消息转发给具体的客户端，实现消息中转的功能";
	}

	@Override
	public String getName() {
		return "pusher";
	}
	
	@Override
	protected void handleMessage(Message message) {
	}

}
