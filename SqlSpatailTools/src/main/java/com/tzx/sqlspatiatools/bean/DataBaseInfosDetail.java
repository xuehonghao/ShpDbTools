package com.tzx.sqlspatiatools.bean;

import java.util.ArrayList;
import java.util.List;

public class DataBaseInfosDetail extends DataBaseInfos {
	
	/**
	 * 数据库名
	 */
	private String databaseName;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 所选字段
	 */
	private List<InputFieldListArgs> inputFieldList = new ArrayList<InputFieldListArgs>();
	
	private OutputArgs outputArgs;
	
	/**
	 * 筛选条件
	 */
	private String filter;
	
	public DataBaseInfosDetail() {
		outputArgs = new OutputArgs();
		outputArgs.setProgress(0);
		outputArgs.setState("未完成");
	}
	

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<InputFieldListArgs> getInputFieldList() {
		return inputFieldList;
	}
	

	public void setInputFieldList(List<InputFieldListArgs> inputFieldList) {
		this.inputFieldList = inputFieldList;
	}



	public OutputArgs getOutputArgs() {
		return outputArgs;
	}


	public void setOutputArgs(OutputArgs outputArgs) {
		this.outputArgs = outputArgs;
	}


	public String getFilter() {
		return filter;
	}


	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	
}
