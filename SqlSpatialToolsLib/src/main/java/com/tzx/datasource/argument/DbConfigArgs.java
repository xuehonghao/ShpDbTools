package com.tzx.datasource.argument;

/**
 * 数据库参数
 * @author Administrator
 *
 */
public class DbConfigArgs extends DataSourceArgs {
	
	/**
	 * 数据库类型
	 */
	protected String dbType;
	
	/**
	 * 数据库ip
	 */
	protected String dbHost;
	
	/**
	 * 端口号
	 */
	protected String dbPort;
	
	/**
	 * 数据库名称
	 */
	protected String dbName;
	
	/**
	 * 用户名
	 */
	protected String dbUserName;
	
	/**
	 * 密码
	 */
	protected String dbPwd;
	
	/**
	 * 数据库表名
	 */
	protected String tableName;
	
	/**
	 * 筛选条件
	 */
	protected String filter;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
		
}
