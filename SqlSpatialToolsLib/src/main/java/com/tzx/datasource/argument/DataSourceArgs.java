package com.tzx.datasource.argument;

import java.util.Map;

import com.tzx.datasource.EDataSourceType;

/**
 * 数据源参数
 * 
 * @author Administrator
 *
 */
public class DataSourceArgs {

	/**
	 * 数据源类型
	 */
	protected EDataSourceType dataSrcType;
	protected Map<String, Object> fieldToTypeMap;
	protected String taskNo;

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public EDataSourceType getDataSrcType() {
		return dataSrcType;
	}

	public void setDataSrcType(EDataSourceType dataSrcType) {
		this.dataSrcType = dataSrcType;
	}

	public Map<String, Object> getFieldToTypeMap() {
		return fieldToTypeMap;
	}

	public void setFieldToTypeMap(Map<String, Object> fieldToTypeMap) {
		this.fieldToTypeMap = fieldToTypeMap;
	}

}
