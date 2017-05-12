package com.surfilter.mass.services.support;

import java.util.HashSet;
import java.util.Set;

import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.services.AbstractDataProvider;
import com.surfilter.mass.services.DataProvider;

/**
 * 数据生产者持有类，持有已经注册的生产者
 * 
 * @author hapuer
 *
 */
public class DataProviderHandler extends AbstractDataProvider {

	private final Set<DataProvider> providers = new HashSet<DataProvider>(5);

	public static DataProviderHandler instance = null;

	public static DataProviderHandler getInstance(ImcaptureContext ctx) {
		if (instance == null) {
			synchronized (DataProviderHandler.class) {
				if (instance == null) {
					instance = new DataProviderHandler(ctx);
				}
			}
		}

		return instance;
	}

	private DataProviderHandler(ImcaptureContext ctx) {
		super(ctx);
	}

	@Override
	public void provideData() throws Exception {
		for (DataProvider dataProvider : providers) {
			dataProvider.provideData();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public void register(DataProvider dataProvider) throws Exception {
		if (dataProvider != null) {
			providers.add(dataProvider);
		}
	}

}
