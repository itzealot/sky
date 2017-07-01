/*
 * Copyright 2015-2016 Dark Phoenixs (Open-Source Organization).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.project.share.common.pool.jdbc;

/**
 * JdbcConfig
 * 
 * @author zealot
 *
 */
public interface JdbcConfig {

	public static final String MYSQL_DEFAULT_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	public static final String MYSQL_DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/test";
	public static final String MYSQL_DEFAULT_JDBC_USERNAME = "root";
	public static final String MYSQL_DEFAULT_JDBC_PASSWORD = "root";

	public static final String DRIVER_CLASS_PROPERTY = "driverClass";
	public static final String JDBC_URL_PROPERTY = "jdbcUrl";
	public static final String JDBC_USERNAME_PROPERTY = "username";
	public static final String JDBC_PASSWORD_PROPERTY = "password";
}
