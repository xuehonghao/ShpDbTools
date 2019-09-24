package com.tzx.sqlspatiatools.bean;

/**
 * shp文件的字段名、类型以及是否选中标识
 * 
 * @author Administrator
 *
 */
public class InputFieldListArgs {

	/**
	 * 字段名
	 */
	private String name;

	/**
	 * 数据类型
	 */
	private Class<?> type;

	/**
	 * 是否选中标识 true:选中 false:未选中
	 */
	private boolean isChecked;
	
	
	/**
	 * 空间字段   true:选中  false:未选中
	 */
	private boolean geoChecked;
	
	/**
	 * id列   true:选中    false:未选中
	 */
	private boolean idChecked;
	
	public InputFieldListArgs() {
		isChecked = true;
		geoChecked = false;
		idChecked = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean flag) {
		this.isChecked = flag;
	}

	public boolean isGeoChecked() {
		return geoChecked;
	}

	public void setGeoChecked(boolean geoChecked) {
		this.geoChecked = geoChecked;
	}

	public boolean isIdChecked() {
		return idChecked;
	}

	public void setIdChecked(boolean idChecked) {
		this.idChecked = idChecked;
	}
	
	

}
