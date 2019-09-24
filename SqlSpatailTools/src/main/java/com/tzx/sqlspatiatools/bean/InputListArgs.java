package com.tzx.sqlspatiatools.bean;

import java.util.List;

/**
 * shp文件参数
 * 
 * @author Administrator
 *
 */
public class InputListArgs {

	/**
	 * 文件路径
	 */
	private String path;

	/**
	 * 生成的表名
	 */
	private String name;

	/**
	 * shp文件的列名和类型
	 */
	private List<InputFieldListArgs> fieldToTypeList;
	
	
	/**
	 * 输出参数
	 */
	private OutputArgs outputArgs;
	
	
	/**
	 * 编码格式   GBK和UTF-8
	 */
	private String charset;
	
	
	
	public InputListArgs() {
		outputArgs = new OutputArgs();
		outputArgs.setProgress(0);
		outputArgs.setState("未完成");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InputFieldListArgs> getFieldToTypeList() {
		return fieldToTypeList;
	}

	public void setFieldToTypeList(List<InputFieldListArgs> fieldToTypeList) {
		this.fieldToTypeList = fieldToTypeList;
	}

	public OutputArgs getOutputArgs() {
		return outputArgs;
	}

	public void setOutputArgs(OutputArgs outputArgs) {
		this.outputArgs = outputArgs;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
