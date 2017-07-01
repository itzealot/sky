package com.sky.project.share.tool.restlet.client;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * 客户端
 * 
 * @author zealot
 *
 */
public class RestletClient {

	public static void main(String[] args) {
		Form form = new Form();
		form.add("taskId", "01");

		// rest api
		String url = "http://localhost:31218/test/hello";
		ClientResource client = new ClientResource(url);

		// 以post方式提交表单
		Representation representation = client.post(form); // post

		try {
			System.out.println("result:" + representation.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
