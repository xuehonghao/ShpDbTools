package com.tzx.datasource.inter;


/**
 * 数据流接口
 * @author Administrator
 *
 */
public interface IDataSource {
	
	/**
	 * 连接数据库
	 * @return
	 */
	public boolean open();
	
	/**
	 * 关闭数据库
	 */
	public void close();
	
}
