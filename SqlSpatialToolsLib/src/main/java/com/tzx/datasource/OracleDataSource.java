package com.tzx.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.oracle.OracleNGDataStoreFactory;

import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.tool.DbUtils;

public class OracleDataSource extends DbDataSource {
 
	
	@Override
	protected Object createDataStoreFactory() {
		// TODO Auto-generated method stub
		OracleNGDataStoreFactory factory = new OracleNGDataStoreFactory();
		return factory;
	}
	
	
	@Override
    public List<String> getDataTableName(DataSourceBase args) {
		DbConfigArgs dbc = (DbConfigArgs)args.getDataSrcCfgArgs();
		DbUtils dbu = createDbUtils(args);
        Connection connection = dbu.getConnection();
        List<String> list = new ArrayList<>();
        try {
        	Statement st = connection.createStatement();
        	String sql = "select TABLE_NAME from user_tables where tablespace_name = 'USERS'";
            ResultSet resultSet = st.executeQuery(sql);
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
					"select NAME from v$tablespace where Name = 'USERS'");
			while (rs.next()) {
				list.add(rs.getString("NAME"));
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
		dbu.setDriver("oracle.jdbc.driver.OracleDriver");
		dbu.setEdbTypb(EDbType.ORACLE);
		dbu.setUrl("jdbc:oracle:thin:@" + dbc.getDbHost() + ":" + (dbc.getDbPort() != null  ? dbc.getDbPort()  : "1521") + ":" + dbc.getDbName());
		dbu.setUser(dbc.getDbUserName());
		dbu.setPass(dbc.getDbPwd());
		return dbu;
	}

}
 