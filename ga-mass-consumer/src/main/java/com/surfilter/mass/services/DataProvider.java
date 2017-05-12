package com.surfilter.mass.services;

/**
 * 数据提供者,可以有多重提供方式
 * 
 * @author hapuer
 */
public interface DataProvider {

	/**
	 * 开起生产者生产数据
	 * 
	 * @throws Exception
	 */
	void provideData() throws Exception;

	/**
	 * 注册生产者 DataProvider
	 * 
	 * @param dataProvider
	 * @throws Exception
	 */
	void register(DataProvider dataProvider) throws Exception;

	/**
	 * 关闭生产者
	 */
	void close();

}
