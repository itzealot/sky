package com.surfilter.mass.tools.orientdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by wellben on 2016/9/26.
 */
public class OrientDaoSingleton {
	
	private static volatile OrientDaoSingleton instance = null;
	
	private static final Properties OJDBC_POOL_INFO = new Properties() {{
        put("user", "admin");
        put("password", "admin");
        
        put("db.usePool", "true"); // USE THE POOL
        put("db.pool.min", "100"); // MINIMUM POOL SIZE
        put("db.pool.max", "1000"); // MAXIMUM POOL SIZE
    }};
    
    //private static String OJDBC_URL = "jdbc:orient:remote:10.10.10.104/gacenter";
    private static String OJDBC_URL = "odb:jdbc:orient:remote:10.10.10.104/gacenter";
    
	private OrientDaoSingleton() {}
	
	private OrientDaoSingleton(String urlStr) {
		initOdbInfo(urlStr);
	}
	
	public static OrientDaoSingleton getInstance(String urlStr) {
		OrientDaoSingleton inst = instance;
        if(inst == null) {
            synchronized (OrientDaoSingleton.class) {
                //inst = instance;
                if(inst == null) {
                    inst = new OrientDaoSingleton(urlStr);
                    instance = inst;
                }
            }
        }
        return inst;
    }
	
    public void initOdbInfo(String orientdbConnStr) {
    	if(orientdbConnStr.startsWith("odb:") && orientdbConnStr.length() > 4) {
			int split1 = orientdbConnStr.indexOf("|");
			int split2 = orientdbConnStr.lastIndexOf("|");
			
			if(split1 > 0) {
				OJDBC_URL = orientdbConnStr.substring(4, split1);
				
				if(split1 != split2) {
					OJDBC_POOL_INFO.setProperty("user", orientdbConnStr.substring(split1 + 1, split2));
			    	OJDBC_POOL_INFO.setProperty("password", orientdbConnStr.substring(split2 + 1));
				} else {
					OJDBC_POOL_INFO.setProperty("user", orientdbConnStr.substring(split1 + 1));
				}
			} else {
				OJDBC_URL = orientdbConnStr.substring(4);
			}
		}
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OJDBC_URL, OJDBC_POOL_INFO);
    }
    
}
