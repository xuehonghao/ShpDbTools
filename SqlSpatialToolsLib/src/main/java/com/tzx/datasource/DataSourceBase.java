package com.tzx.datasource;

import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCDataStore;

import com.tzx.datasource.argument.DataSourceArgs;
import com.tzx.datasource.inter.InputDataSource;
import com.tzx.datasource.inter.OutputDataSource;
import com.tzx.datasource.inter.ProgressListener;

public class DataSourceBase implements InputDataSource, OutputDataSource {
	protected DataSourceArgs dataSrcCfgArgs;
	protected ProgressListener progressListener;

	public DataSourceBase() {
		dataSrcCfgArgs = new DataSourceArgs();
	}

	public DataSourceArgs getDataSrcCfgArgs() {
		return dataSrcCfgArgs;
	}

	public void setDataSrcCfgArgs(DataSourceArgs dataSrcCfgArgs) {
		this.dataSrcCfgArgs = dataSrcCfgArgs;
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getVectorPropInfo(Object args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getVectorData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exportData(InputDataSource inputDataSrouce) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<String> queryDataBase(DataSourceBase args){
		return null;
	}
	
	//获取数据表名
    public List<String> getDataTableName(DataSourceBase args) {
        return null;
    }
    
	
	protected DataStore buildDataStore(boolean bExposePrimaryKey) {
		// Empty.
		return null;
	}
	
	public boolean validConnection(DataSourceBase args) {
		return false;
	}

	@Override
	public boolean exportOldData(InputDataSource inputDataSrouce) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setProgressListener(ProgressListener listener) {
		// TODO Auto-generated method stub
		progressListener = listener;
	}

	@Override
	public long getRecordNum() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public void dialect(JDBCDataStore ds) {
		
	}


}
