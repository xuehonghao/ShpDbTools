package com.tzx.datasource;

import com.tzx.datasource.inter.IDataSource;

/**
 * 数据源工厂
 * 
 * @author Administrator
 *
 */
public class DataSourceFactory {

	/**
	 * 创建数据源
	 * 
	 * @param dataSrcType
	 * @return
	 */
	public static IDataSource createDataSource(EDataSourceType dataSrcType) {
		IDataSource dataSrc = null;

		switch (dataSrcType) {
		case Shp:
			dataSrc = new ShpDataSource();
			System.out.println("创建了ShpDataSource");
			break;
			
		case MSSQL:
			dataSrc = new MssqlDataSource();
			System.out.println("创建了MssqlDataSource");
			break;
			
		case MYSQL:
			dataSrc = new MysqlDataSource();
			System.out.println("创建了MysqlDataSource");
			break;
			
		case ORACLE:
			dataSrc = new OracleDataSource();
			System.out.println("创建了OracleDataSource");
			break;
			
		case PostGIS:
			dataSrc = new PostGisDataSource();
			System.out.println("创建了PostGisDataSource");
			break;

		default:
			break;
		}

		return dataSrc;
	}
}
