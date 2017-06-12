package com.surfilter.mass.tools.db;

import java.util.List;

import com.surfilter.mass.tools.entity.InnList;
import com.surfilter.mass.tools.entity.ZdPerson;
import com.surfilter.mass.tools.entity.ZdPersonInfo;

/**
 * Mysql DAO
 * 
 * @author zealot
 *
 */
public interface MysqlDAO {

	/**
	 * 保存入住信息
	 * 
	 * @param lists
	 */
	void save(List<InnList> lists);

	/**
	 * 保存zd_person且返回对应的id列表
	 * 
	 * @param ps
	 * @return
	 */
	List<Long> saveZdPersons(List<ZdPerson> ps);

	/**
	 * 保存 zd_person_info
	 * 
	 * @param ps
	 */
	void saveZdPersonInfos(List<ZdPersonInfo> ps);

	List<Long> queyZdPersonIds(List<String> certs);
}
