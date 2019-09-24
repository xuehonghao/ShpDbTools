package com.tzx.datasource.inter;


/**
 * 输出接口
 * @author Administrator
 *
 */
public interface OutputDataSource extends IDataSource{
	
	/**
	 * 导出数据
	 * @param args
	 * @param inputDataSrcType
	 * @param fieldToTypeMap
	 * @return
	 */
	public boolean exportData(InputDataSource inputDataSrouce);
	
	
	
	/**
	 * 导出旧格式数据
	 * @param inputDataSrouce
	 * @param type
	 * @param type
	 * @return
	 */
	public boolean exportOldData(InputDataSource inputDataSrouce);
	
	public void setProgressListener(ProgressListener listener);
	
}
