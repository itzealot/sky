/**
 * 
 */
package com.surfilter.mass.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.surfilter.mass.services.support.KafkaMsg;
import com.surfilter.mass.services.support.KafkaMsgFactory;
import com.surfilter.mass.services.support.MsgHandler;

/**
 * @author hapuer
 *
 */
public class TestDataProvider {

	@SuppressWarnings("unchecked")
	@Test
	public void testKafkaClient()throws Exception{
		String topic = "wl_h2";
		int partitions = 3;
		
		 // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();

        // The factory for the event
        KafkaMsgFactory factory = new KafkaMsgFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 2048;

        // Construct the Disruptor
        Disruptor<KafkaMsg> disruptor = new Disruptor<>(factory, bufferSize, executor);
        EventHandler<KafkaMsg> handler = new MsgHandler();
        disruptor.handleEventsWith(handler);
        disruptor.start();
        // Connect the handler
		
	/*	DataProvider dataProvider = new KafkaProvider(new ImcaptureContext(new MassConfiguration()));
		try{
			dataProvider.provideData();
		}finally{
			dataProvider.close();
		}*/
	}
	
	@Test
	public void testSimpleClient()throws Exception{
		String topic = "wl_h2";
		int partitions = 3;
		/*DataProvider dataProvider = new KafkaProvider(new ImcaptureContext(new MassConfiguration()));
		dataProvider.provideData();*/
	}
	
}
