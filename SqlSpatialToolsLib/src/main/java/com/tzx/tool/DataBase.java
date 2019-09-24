package com.tzx.tool;

import java.io.FileInputStream;
import java.util.Properties;

import com.tzx.datasource.EDbType;

/**
 * 读取properties文件,获取其中的值,并赋值
 * 
 * @author Administrator
 *
 */
public class DataBase {
	public String DRIVER;
	public String URL;
	public String USER;
	public String PASSWORD;
	public EDbType edbTypb;

	public DataBase(EDbType edbTypb) {
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream("./database.properties");
			prop.load(fis);
			switch (edbTypb) {
			case MSSQL:
				DRIVER = prop.getProperty("driver2mssql");
				break;
			case MYSQL:
				DRIVER = prop.getProperty("driver2mysql");
				break;
			case ORACLE:
				DRIVER = prop.getProperty("driver2oracle");
				break;
			case PostGIS:
				DRIVER = prop.getProperty("driver2postgis");
				break;
			}
			
			URL = prop.getProperty("url");
			USER = prop.getProperty("user");
			PASSWORD = prop.getProperty("password");
			this.edbTypb = edbTypb;
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
