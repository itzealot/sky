package com.surfilter.mass.tools;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.service.KaKouInfoCache;

import junit.framework.TestCase;

public class KaKouInfoCacheTest extends TestCase {

	public void testKaKouInfoCache() {
		System.setProperty("tools.settings", "fasle");
		VehicleConfig config = new VehicleConfig(new MassConfiguration());
		KaKouInfoCache.getInstance(config.getKakouPath());
	}
}
