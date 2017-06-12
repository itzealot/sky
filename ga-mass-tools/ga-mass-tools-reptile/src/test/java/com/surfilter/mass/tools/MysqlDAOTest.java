package com.surfilter.mass.tools;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import com.surfilter.mass.tools.conf.BaseInfoConfig;
import com.surfilter.mass.tools.conf.JdbcConfig;
import com.surfilter.mass.tools.conf.JdbcUtils;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstant;
import com.surfilter.mass.tools.db.MysqlDAO;
import com.surfilter.mass.tools.db.impl.MysqlDAOImpl;
import com.surfilter.mass.tools.entity.InnList;
import com.surfilter.mass.tools.entity.ZdPerson;
import com.surfilter.mass.tools.entity.ZdPersonInfo;
import com.surfilter.mass.tools.entity.ZtryData;

import junit.framework.TestCase;

public class MysqlDAOTest extends TestCase {

	String driverClassName = JdbcUtils.DRIVER_CLASS_NAME;
	String url = "jdbc:mysql://192.168.0.112:3306/gacenter_gd?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
	String userName = "root";
	String password = "surfilter1218";
	JdbcConfig confg = new JdbcConfig(driverClassName, url, userName, password);
	MysqlDAO dao = new MysqlDAOImpl(confg);

	static {
		System.setProperty(SysConstant.TOOLS_SETTINGS, "true");
	}

	public void testSave() {
		dao.save(Arrays.asList(new InnList("erviceCode", "username", "1021111", "362201198808088888", new Date(0L),
				new Date(0L), "roomNo", "floor", "memo", "orgName", "country", "13888889888", "440300",
				new Timestamp(0L), new Timestamp(0L))));
	}

	ZtryData data = new ZtryData("ztryId", "ztryTId", "name", "nickname", "boy", "2002-10-10", "111111111111111111",
			"汉族", "", "", "162cm", "湖北口音", "IT", "湖北武汉", "", "湖北武汉", "features", "special", "caseNo", "caseType",
			"caseRemark", "2016-10-10", "向北逃窜", "awayType", "lawBook", "2016-10-12", "tjl", "tjlType", "tjlMoney",
			"1004", "deptType", "receiveDept", "2016-10-14", "receiver", "13888888888", "register", "2016-10-15",
			"registAuthor", "blReason", "2016-10-16", "2016-10-17", null, null);

	private MassConfiguration conf = new MassConfiguration();

	public void testSaveZdPersonInfos() {
		ZdPerson zdPerson = new ZdPerson(data, new BaseInfoConfig(conf));
		ZdPersonInfo p1 = new ZdPersonInfo(zdPerson, 1);
		dao.saveZdPersonInfos(Arrays.asList(p1));
	}

	public void testSaveZdPersons() {
		ZdPerson zdPerson = new ZdPerson(data, new BaseInfoConfig(conf));

		List<Long> ids = dao.saveZdPersons(Arrays.asList(zdPerson));

		if (ids != null && ids.size() > 0) {
			ZdPersonInfo p1 = new ZdPersonInfo(zdPerson, ids.get(0));
			dao.saveZdPersonInfos(Arrays.asList(p1));
		}
	}

	public void testQueyZdPersonIds() {
		System.out.println(dao.queyZdPersonIds(Arrays.asList("440301198801225016", "440301198801225017")));
	}
}
