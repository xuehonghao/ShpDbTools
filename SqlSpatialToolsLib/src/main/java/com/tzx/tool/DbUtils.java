package com.tzx.tool;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tzx.datasource.EDbType;

/**
 * 数据库连接类
 * 
 * @author Administrator
 *
 */
public class DbUtils {

	private  String driver;// 驱动程序类名
	private String url;// 连接地址
	private String user;// 用户名
	private String pass;// 密码

	private Connection conn = null; // 创建Connection对象
	private Statement sta = null; // 创建Statement对象
	
	public EDbType edbTypb; //数据库类型
	
	public DbUtils() {
	}
	
	public static void main(String[] args) {
		DbUtils d = new DbUtils(EDbType.MSSQL);
		d.setUrl("jdbc:sqlserver://192.168.0.103:1433;databaseName=RES_MODEL");
		d.setUser("sa");
		d.setPass("123");
		Connection conn = d.getConnection();
		
		System.out.println(conn);
		
		 String sql = "select * from watershd";
		    PreparedStatement pstmt;
		    try {
		        pstmt = (PreparedStatement)conn.prepareStatement(sql);
		        ResultSet rs = pstmt.executeQuery();
		        int col = rs.getMetaData().getColumnCount();
		        System.out.println("============================");
		        while (rs.next()) {
		            if(rs.getString("name").equals("hb_25m_RES_R140W140")) {
		            	byte[] a = rs.getBytes("Vertex");
		            	
		            	long value = 0;
		        		for (int i = 0; i < 8; i++) {
		        			value |= ((long) (a[i] & 0xff)) << (8 * i);
		        		}
		            	System.out.println(a);
		            	System.out.println(Double.longBitsToDouble(value));
		            }
		        }
		            System.out.println("============================");
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}

	public DbUtils(EDbType edbTypb) {
		DataBase dataBase = new DataBase(edbTypb);
		driver = dataBase.DRIVER;
		url = dataBase.URL;
		user = dataBase.USER;
		pass = dataBase.PASSWORD;
		this.edbTypb = dataBase.edbTypb;
	}

	/**
	 * 数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		Connection conn = null;// 声明连接对象
		try {
			// 注册(加载)驱动程序
			try {
				System.out.println("正在加载驱动");
				Class.forName(this.driver);
				System.out.println("加载驱动成功");
			} catch (Exception e1) {
				System.out.println("加载驱动失败");
				e1.printStackTrace();
			}

			// 连接数据库
			System.out.println("正在连接数据库");
			conn = DriverManager.getConnection(this.url, this.user, this.pass);// 获取数据库连接
			System.out.println("数据库连接成功");
		} catch (Exception e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}

		return conn;
	}
	
	
	/**
	 * 数据库连接验证
	 * 
	 * @return
	 */
	public boolean testConnection() {
		try {
			// 注册(加载)驱动程序
			try {
				System.out.println("正在加载驱动");
				Class.forName(this.driver);
				System.out.println("加载驱动成功");
			} catch (Exception e1) {
				System.out.println("加载驱动失败");
				return false;
			}

			// 连接数据库
			System.out.println("正在连接数据库");
			conn = DriverManager.getConnection(this.url, this.user, this.pass);// 获取数据库连接
			System.out.println("数据库连接成功");
		} catch (Exception e) {
			System.out.println("数据库连接失败");
			return false;
		}
		return true;
	}

	/**
	 * 创建表、向表中填充数据
	 * 
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql) {
		// 受影响的行数
		int affectedLine = 0;

		try {
			// 获得连接
			conn = getConnection();

			// 调用sql
			sta = conn.createStatement();
			affectedLine = sta.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println("sql语句异常");
			e.printStackTrace();
		} finally {
			try {
				if (sta != null)
					sta.close();
			} catch (SQLException e) {
				System.out.println("Statement释放异常");
				e.printStackTrace();
			}
		}
		return affectedLine;
	}

	/**
	 * 释放数据库连接
	 * 
	 * @param conn
	 */
	public void releaseConnection(Connection conn) {
		try {
			System.out.println("正在释放数据库连接");
			if (conn != null)
				conn.close();
			System.out.println("释放数据库连接成功");
		} catch (Exception e) {
			System.out.println("释放数据库连接失败");
			e.printStackTrace();
		}
	}

	/**
	 * 关闭所有资源
	 */
	@SuppressWarnings("unused")
	private void closeAll() {
		// 关闭Statement对象
		if (sta != null) {
			try {
				sta.close();
				System.out.println("释放Statement对象成功");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}


		// 关闭Connection 对象
		if (conn != null) {
			try {
				conn.close();
				System.out.println("释放Connection对象成功");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public EDbType getEdbTypb() {
		return edbTypb;
	}

	public void setEdbTypb(EDbType edbTypb) {
		this.edbTypb = edbTypb;
	}
	
	
	
	
	

}
