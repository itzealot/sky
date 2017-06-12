package com.surfilter.mass.tools.orientdb;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.surfilter.mass.tools.utils.Constants;

/**
 * Created by wellben on 2016/9/26.
 */
public class OrientJdbcUtils {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.OJDBC_URL, Constants.OJDBC_POOL_INFO);
    }

}
