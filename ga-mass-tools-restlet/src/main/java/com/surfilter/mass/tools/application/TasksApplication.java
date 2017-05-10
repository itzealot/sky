package com.surfilter.mass.tools.application;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.surfilter.mass.tools.resource.RestletServerResource;

/**
 * 服务应用类，根据 REST Url 注册对应的 ServerResource 实现类
 * 
 * @author zealot
 *
 */
public class TasksApplication extends Application {
	private static final String REST_HELLO = "/hello";

	/**
	 * 重写 createInboundRoot 通过 attach 方法绑定资源类, 并且制定了访问路径
	 * 
	 * REST url:${root}/${restUrl}
	 * 
	 * 注册 ServerResource 实现类
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach(REST_HELLO, RestletServerResource.class);
		return router;
	}
}
