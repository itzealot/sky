package com.sky.project.share.hessian;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * HessianClient
 * 
 * @author zealot
 *
 */
public class HessianClientTest {

	public static void main(String[] args) throws MalformedURLException {
		// 服务端 url
		String url = "http://localhost:8080/sky-hessian-demo/HelloWorldServlet";

		HessianProxyFactory factory = new HessianProxyFactory();
		HelloWorldService basic = (HelloWorldService) factory.create(HelloWorldService.class, url);

		System.out.println("msg:" + basic.hello());
	}
}
