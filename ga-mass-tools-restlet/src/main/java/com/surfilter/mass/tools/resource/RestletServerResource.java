package com.surfilter.mass.tools.resource;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Rest URL 处理的服务端(对POST,PUT,GET请求进行处理)
 * 
 * @author zealot
 *
 */
public class RestletServerResource extends ServerResource {
	private String id;

	/**
	 * 用来获取传递过来的id占位符的值
	 */
	@Override
	protected void doInit() throws ResourceException {
		id = (String) getRequestAttributes().get("taskId");
	}

	/**
	 * 如果加了@Get注解，方法名可以随意取，如果不加，则一定要是get 下面的post和put 一样
	 * 如果为Get方式请求，那么必须结合doInit方法才能把参数拿出来
	 * 
	 * @return
	 */
	@Get
	public Representation getTest() {
		return new StringRepresentation("hello world " + id);
	}

	/**
	 * 处理post请求
	 * 
	 * @param entity
	 * @return
	 */
	@Post
	public Representation postTest(Representation entity) {
		Form form = new Form(entity);
		String taskId = form.getFirstValue("taskId");
		if (taskId == null) {
			return new StringRepresentation("FAIL:null taskId");
		}
		// TODO

		// 向客户端返回处理结果
		return new StringRepresentation("SUCCESS");
	}

	/**
	 * 处理put请求
	 * 
	 * @param entity
	 * @return
	 */
	@Put
	protected Representation putTest(Representation entity) {
		Form form = new Form(entity); // 获取表单值
		String taskId = form.getFirstValue("taskId");
		// 将客户端提交的表单值返回
		return new StringRepresentation("SUCESS:taskId=" + taskId);
	}
}
