package com.tzx.datasource.argument;

/**
 * 旧类型配置
 * 
 * @author Administrator
 *
 */
public class OldDbConfigArgs extends DbConfigArgs {

	/**
	 * 类型分为三种 1. 点：Point 2. 线：Line 3. 面：Polygon
	 */
	private String type;

	/**
	 * 字段名 如果是点，则有两个字段名，用英文逗号隔开；如果是线面，则只有一个字段名
	 */
	private String fieldName;

	/**
	 * 类型float 和 double
	 */
	private String binaryDataType;

	public String getBinaryDataType() {
		return binaryDataType;
	}

	public void setBinaryDataType(String binaryDataType) {
		this.binaryDataType = binaryDataType;
	}

	public String getExcuteSql() {
		return excuteSql;
	}

	public void setExcuteSql(String excuteSql) {
		this.excuteSql = excuteSql;
	}

	private String excuteSql;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
