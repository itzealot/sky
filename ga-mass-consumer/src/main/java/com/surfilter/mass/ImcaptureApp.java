package com.surfilter.mass;

import com.surfilter.mass.services.ServiceEngine;

public class ImcaptureApp {
	
	public static void main(String[] args) {
		ServiceEngine.getInstance()
					 .provide()
					 .consume()
					 .startEngine();
	}

}
