package com.surfilter.mass.tools;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.ReptileConfig;
import com.surfilter.mass.tools.conf.SysConstant;

import junit.framework.TestCase;

public class ReptileConfigTest extends TestCase {

	public void testJdbc() {
		System.setProperty(SysConstant.TOOLS_SETTINGS, "true");
		new ReptileConfig(new MassConfiguration());
	}
}
