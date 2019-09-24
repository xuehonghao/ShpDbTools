package com.tzx.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotools.data.sqlserver.SQLServerDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.tool.DbUtils;


public class MssqlDataSource extends DbDataSource {

	@Override
	protected Object createDataStoreFactory() {
		// TODO Auto-generated method stub
		SQLServerDataStoreFactory factory = new SQLServerDataStoreFactory();
		return factory;
	}

	@Override
	public List<String> getDataTableName(DataSourceBase args) {
		DbUtils dbu = createDbUtils(args);
		Connection connection = dbu.getConnection();
		List<String> list = new ArrayList<>();
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(connection.getCatalog(), null, null,
					new String[] { "TABLE" });
			while (resultSet.next()) {
				list.add(resultSet.getString("TABLE_NAME"));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<String> queryDataBase(DataSourceBase args) {
		List<String> list = new ArrayList<String>();
		DbUtils dbu = createDbUtils(args);
		try {
			Connection connection = dbu.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT name FROM  master..sysdatabases WHERE name NOT IN ( 'master', 'model', 'msdb', 'tempdb', 'northwind','pubs' )");
			while (rs.next()) {
				list.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean validConnection(DataSourceBase args) {
		DbUtils dbu = createDbUtils(args);
		boolean connection = false;
		try {
			connection = dbu.testConnection();
		} catch (Exception e) {
			connection = false;
		}
		return connection;
	}

	/**
	 * 创建DbUtils对象
	 * 
	 * @param args
	 * @return
	 */
	@Override
	public DbUtils createDbUtils(DataSourceBase args) {
		DbConfigArgs dbc = (DbConfigArgs) args.getDataSrcCfgArgs();
		DbUtils dbu = new DbUtils();
		dbu.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dbu.setEdbTypb(EDbType.MSSQL);
		
		if(".".equals(dbc.getDbHost())) {
			dbc.setDbHost("127.0.0.1");
		}
		
		dbu.setUrl("jdbc:sqlserver://" + dbc.getDbHost() + (dbc.getDbHost().indexOf("\\") != -1 ? "" : ":" + dbc.getDbPort())
				+ ";DatabaseName=" + dbc.getDbName());
		
		dbu.setUser(dbc.getDbUserName());
		dbu.setPass(dbc.getDbPwd());
		return dbu;
	}

	@Override
	protected void alterTableVarbinary(Map<String, Object> binaryTypeMap, String tableName, Connection con) {
		try {
			PreparedStatement pstmt = null;
			for (Map.Entry<String, Object> binaryType : binaryTypeMap.entrySet()) {
				String sql = "ALTER TABLE " + tableName + " ALTER COLUMN " + binaryType.getKey() + " varbinary(MAX)";
				pstmt = (PreparedStatement) con.prepareStatement(sql);
				int i = pstmt.executeUpdate();
				System.out.println("resutl: " + i);
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	
	@Override
	public void dialect(JDBCDataStore ds) {
		CustomMsSQLDialect mssql = new CustomMsSQLDialect(ds);
		ds.setSQLDialect(mssql);
	}

}
