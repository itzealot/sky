package com.surfilter.mass.services;

import org.junit.Test;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureContext;


public class TestConf {

	@Test
	public void testParse(){
		MassConfiguration conf = new MassConfiguration();
		ImcaptureContext ctx = new ImcaptureContext(conf);
		String kafkaZkUrl = ctx.getString("kafka.zk.url");
		System.out.println(kafkaZkUrl);
	}
	
	@Test
	public void testConf(){
		MassConfiguration conf = new MassConfiguration();
		String kafkaZkUrl = conf.getStrings("kafka.zk.url");
		System.out.println(kafkaZkUrl);
	}
}
