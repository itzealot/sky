package com.sky.projects.message;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚集分析
 * 
 * @author zealot
 *
 */
public final class ClusterAnalysis {

	/**
	 * 分析聚集团伙
	 * 
	 * @param lists
	 *            集合列表
	 * @param d
	 *            距离接口实现类
	 * @param factory
	 *            工厂方法实现类
	 * @param params
	 *            需要构建对象的参数列表
	 * @param m
	 *            执行更新的实现类
	 * @return
	 */
	public static <T, V> List<V> analysisCluster(List<T> lists, Cluster<T, V> d, Factory<V> factory, Params<T> params,
			Modifiable<T, V> m) {
		List<V> results = new ArrayList<>();

		int i = 0;
		T t = lists.get(i);
		results.add(factory.newInstance(params.getParams(t)));

		i++;
		int size = lists.size();
		while (i < size) {
			T o = lists.get(i); // 获取当前执行的成员

			boolean flag = true; // 标记是否有符合要求的聚集

			for (V v : results) { // 查找当前成员是否包含在聚集中
				if (d.isCluster(o, v)) { // 成员属于目标聚集
					m.update(o, v);
					flag = false;
					break;
				}
			}

			if (flag) { // 未找到合适的聚集，新增到新的聚集中
				results.add(factory.newInstance(params.getParams(o)));
			}

			i++;
		}

		return results;
	}

	private ClusterAnalysis() {
	}
}
