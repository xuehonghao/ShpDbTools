package com.tzx.datasource.inter;

import java.util.Map;


/**
 * 输入接口
 * 
 * @author Administrator
 *
 */
public interface InputDataSource extends IDataSource {
	
	/**
	 * 显示属性的名称和类型
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getVectorPropInfo(Object args);
	
	public Object getVectorData();
	
	public long getRecordNum();
	
}
