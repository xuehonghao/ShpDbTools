package com.tzx.datasource.argument;


/**
 * 文件参数
 * @author Administrator
 *
 */
public class VectorFileArgs extends DataSourceArgs {
	
	
	/**
	 * 文件路径
	 */
	protected String vectorFilePath;
	
	
	/**
	 * 编码格式
	 */
	protected String charset;

	public String getVectorFilePath() {
		return vectorFilePath;
	}

	public void setVectorFilePath(String vectorFilePath) {
		this.vectorFilePath = vectorFilePath;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	

}
