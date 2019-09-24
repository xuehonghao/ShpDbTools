package com.tzx.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.tool.DbUtils;

public class MysqlDataSource extends DbDataSource{
 
	@Override
	protected Object createDataStoreFactory() {
		// TODO Auto-generated method stub
		MySQLDataStoreFactory factory = new MySQLDataStoreFactory();
		return factory;
	}
	
	@Override
    public List<String> getDataTableName(DataSourceBase args) {
		DbConfigArgs dbc = (DbConfigArgs)args.getDataSrcCfgArgs();
		DbUtils dbu = createDbUtils(args);
        Connection connection = dbu.getConnection();
        List<String> list = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(connection.getCatalog(),null,null,new String[]{"TABLE"});
            while (resultSet.next()){
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
					"show databases");
			while (rs.next()) {
				list.add(rs.getString("Database"));
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
		DbConfigArgs dbc = (DbConfigArgs)args.getDataSrcCfgArgs();
		DbUtils dbu = new DbUtils();
		dbu.setDriver("com.mysql.jdbc.Driver");
		dbu.setEdbTypb(EDbType.MYSQL);
		dbu.setUrl("jdbc:mysql://" + dbc.getDbHost() + ":" + (dbc.getDbPort() != null  ? dbc.getDbPort()  : "3306") + "/" + dbc.getDbName());
		dbu.setUser(dbc.getDbUserName());
		dbu.setPass(dbc.getDbPwd());
		return dbu;
	}
	
	@Override
	public void dialect(JDBCDataStore ds) {
		CustomMySQLDialect mysql = new CustomMySQLDialect(ds);
		mysql.setStorageEngine("InnoDB");
		ds.setSQLDialect(mysql);
	}


}
 