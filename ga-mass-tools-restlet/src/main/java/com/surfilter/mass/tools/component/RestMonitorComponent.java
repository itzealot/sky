package com.surfilter.mass.tools.component;

import org.restlet.Component;

import com.surfilter.mass.tools.application.TasksApplication;

/**
 * 组件端，指定 rest 的访问 root URL并注册 Application 的实现类
 * 
 * @author zealot
 *
 */
public class RestMonitorComponent extends Component {

	private static final String REST_ROOT = "/test";

	/**
	 * 重写 createInboundRoot 通过attach方法绑定资源类，并且制定了访问路径
	 * 
	 * 注册 Application 实现类
	 */
	public RestMonitorComponent() {
		getDefaultHost().attach(REST_ROOT, new TasksApplication());
	}
}
