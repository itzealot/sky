package com.surfilter.mass.tools;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.service.VehicleDataService;
import com.surfilter.mass.tools.util.VehicleDataUtil;

import junit.framework.TestCase;

public class VehicleDataServiceTest extends TestCase {

	public void testExecute() {
		System.setProperty("tools.settings", "fasle");
		VehicleConfig config = new VehicleConfig(new MassConfiguration());
		VehicleDataService service = new VehicleDataService(config);
		service.execute("2017-03-02");
	}

	public void testTimeStr() {
		for (int i = 1; i <= 10; i++)
			System.out.println(VehicleDataUtil.timeStr("2017-03-02", i));
	}

	public void testcatchVehicleDataService() {
		System.setProperty("tools.settings", "fasle");
		VehicleConfig config = new VehicleConfig(new MassConfiguration());
		new VehicleDataService(config).execute("2017-03-02");
	}
}
