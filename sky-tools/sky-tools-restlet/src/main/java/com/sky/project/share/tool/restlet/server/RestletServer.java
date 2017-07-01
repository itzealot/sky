package com.sky.project.share.tool.restlet.server;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.sky.project.share.tool.restlet.component.RestMonitorComponent;

/**
 * Restlet 服务端(绑定相应的端口)
 * 
 * @author zealot
 *
 */
public class RestletServer {

	public static void main(String[] args) throws Exception {
		Component component = new RestMonitorComponent();
		component.getServers().add(Protocol.HTTP, 31218);
		component.start();
	}
}