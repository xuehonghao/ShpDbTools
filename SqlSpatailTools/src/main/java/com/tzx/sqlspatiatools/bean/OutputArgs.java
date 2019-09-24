package com.tzx.sqlspatiatools.bean;

public class OutputArgs {
	

	/**
	 * 生成的数据库类型
	 */
	private String inputDbType;
	
	/**
	 * 生成的数据库地址
	 */
	private String inputDbHost;
	
	/**
	 * 生成的数据库端口
	 */
	private String inputDbPort;
	
	/**
	 * 生成的数据库用户名
	 */
	private String inputDbUser;
	
	/**
	 * 生成的数据库密码
	 */
	private String inputDbPassword;
	
	/**
	 * 生成的数据库名
	 */
	private String inputDbName;
	
	
	
	/**
	 * 生成文件的名字
	 */
	private String inputName;
	
	
	/**
	 * 输出路径
	 */
	private String inputPath;
	
	
	
	/**
	 * 进度
	 */
	private int progress;
	
	/**
	 * 状态
	 */
	private String state;
	
	/**
	 * 编号
	 */
	private String taskNo;
	
	
	/**
	 * 格式
	 */
	private String format;
	
	
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


	public String getInputDbType() {
		return inputDbType;
	}


	public void setInputDbType(String inputDbType) {
		this.inputDbType = inputDbType;
	}


	public String getInputDbHost() {
		return inputDbHost;
	}


	public void setInputDbHost(String inputDbHost) {
		this.inputDbHost = inputDbHost;
	}


	public String getInputDbPort() {
		return inputDbPort;
	}


	public void setInputDbPort(String inputDbPort) {
		this.inputDbPort = inputDbPort;
	}


	public String getInputDbUser() {
		return inputDbUser;
	}


	public void setInputDbUser(String inputDbUser) {
		this.inputDbUser = inputDbUser;
	}


	public String getInputDbPassword() {
		return inputDbPassword;
	}


	public void setInputDbPassword(String inputDbPassword) {
		this.inputDbPassword = inputDbPassword;
	}


	public String getInputDbName() {
		return inputDbName;
	}


	public void setInputDbName(String inputDbName) {
		this.inputDbName = inputDbName;
	}


	public int getProgress() {
		return progress;
	}


	public void setProgress(int progress) {
		this.progress = progress;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getTaskNo() {
		return taskNo;
	}


	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	public String getInputName() {
		return inputName;
	}


	public void setInputName(String inputName) {
		this.inputName = inputName;
	}


	public String getInputPath() {
		return inputPath;
	}


	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}


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


	public String getBinaryDataType() {
		return binaryDataType;
	}


	public void setBinaryDataType(String binaryDataType) {
		this.binaryDataType = binaryDataType;
	}
	
	
	
	
	

}
