
import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import com.tzx.datasource.DataSourceBase;
import com.tzx.datasource.DataSourceFactory;
import com.tzx.datasource.DbDataSource;
import com.tzx.datasource.EDataSourceType;
import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.datasource.argument.OldDbConfigArgs;
import com.tzx.datasource.argument.OldVectorFileConfigArgs;
import com.tzx.datasource.argument.VectorFileArgs;
import com.tzx.datasource.inter.ProgressListener;

public class TestDataSource {

//	@Test
	public void testShpToMssql() {
		// shp to mssql. EDataSourceType
		EDataSourceType dataSourceType = EDataSourceType.Shp;
		DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
		inputDataSrcArgs.setDataSrcType(dataSourceType);
//		inputDataSrcArgs.setVectorFilePath("D:\\磁盘数据2\\数据&图层\\陕西\\陕西政区_region.shp");
		inputDataSrcArgs.setVectorFilePath("C:\\Users\\TZX_XHH\\Desktop\\00013_201907081709\\00013.shp");
		inputDataSrcArgs.setCharset("GBK");
		inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
		try {
			Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
			inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block e.printStackTrace();
			e.printStackTrace();
		}

		dataSourceType = EDataSourceType.MSSQL;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
		outputDataSrcArgs.setDbHost("192.168.0.70\\sql2008");
		outputDataSrcArgs.setDbName("LC_DataBase_YZT_70");
		outputDataSrcArgs.setDbUserName("sa");
		outputDataSrcArgs.setDbPwd("3edc9ijn~");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputDataSrcArgs.setTableName("00013");
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		outputMssqlDataSrc.exportData(inputShpDataSrc);

	}

	//@Test
	public void testShp2Mysql() {
		// shp to mysql. EDataSourceType
		EDataSourceType dataSourceType = EDataSourceType.Shp;
		DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setVectorFilePath("F:/数据&图层/陕西/陕西政区_region.shp");
		inputDataSrcArgs.setCharset("GBK");
		inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
		try {
			Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
			inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block e.printStackTrace();
			e.printStackTrace();
		}

		dataSourceType = EDataSourceType.MYSQL;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.MYSQL_DBTYPE);
		outputDataSrcArgs.setDbHost("192.168.0.70");
		outputDataSrcArgs.setDbName("test");
		outputDataSrcArgs.setDbUserName("root");
		outputDataSrcArgs.setDbPwd("root");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputDataSrcArgs.setTableName("test1");
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		outputMssqlDataSrc.exportData(inputShpDataSrc);
	}

	//@Test
	public void testMssqlToShp() {
		EDataSourceType dataSourceType = EDataSourceType.MSSQL;
		DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs inputDataSrcArgs = new DbConfigArgs();
		inputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
		inputDataSrcArgs.setDbHost("192.168.0.70\\sql2008");
//		inputDataSrcArgs.setDbHost("127.0.0.1");
//		inputDataSrcArgs.setDbName("HENAN_RES_MODEL_ZXSK");
		inputDataSrcArgs.setDbName("SD_RiverManage");
		inputDataSrcArgs.setDbUserName("sa");
		inputDataSrcArgs.setDbPwd("3edc9ijn~");
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setTableName("NET_RIVER_SECTION");
		inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
		try {
			Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
			Map<String, Object> tmpMap = new HashMap<String,Object>();
			tmpMap.put("hdbm", fieldToTypeMap.get("hdbm"));
			tmpMap.put("hdmc", fieldToTypeMap.get("hdmc"));
			tmpMap.put("rvcd", fieldToTypeMap.get("rvcd"));
			tmpMap.put("rvnm", fieldToTypeMap.get("rvnm"));
			tmpMap.put("startx", fieldToTypeMap.get("startx"));
			tmpMap.put("starty", fieldToTypeMap.get("starty"));
			tmpMap.put("endx", fieldToTypeMap.get("endx"));
			tmpMap.put("endy", fieldToTypeMap.get("endy"));
			tmpMap.put("xzqh", fieldToTypeMap.get("xzqh"));
			tmpMap.put("the_geom", fieldToTypeMap.get("the_geom"));
			inputDataSrcArgs.setFieldToTypeMap(tmpMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataSourceType = EDataSourceType.Shp;
		DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		outputShpDataSrc.setProgressListener(new ProgressListener() {
			
			@Override
			public void onProcProgress(String taskNo, int progress) {
				// TODO Auto-generated method stub
				System.out.println(taskNo + "   " +progress);
			}
		});
		VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
		outputDataSrcArgs.setTaskNo("1");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputDataSrcArgs.setVectorFilePath("C:\\Users\\TZX_XHH\\Desktop\\NET_RIVER_SECTION.shp");
		outputDataSrcArgs.setCharset("GBK");
		outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		outputShpDataSrc.exportData(inputMssqlDataSrc);
	}
	
	
	// @Test
	public void write2() {
		try {
			// 定义属性
			final SimpleFeatureType TYPE = DataUtilities.createType("Location", "location:Point," + // <- the geometry
																									// attribute: Point
																									// type
					"POIID:String," + // <- a String attribute
					"MESHID:String," + // a number attribute
					"OWNER:String");
			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
			Map<String, Serializable> params = new LinkedHashMap<String, Serializable>();
			File newFile = new File("D:/newPoi.shp");
			params.put("url", newFile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			newDataStore.createSchema(TYPE);
			newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void queryDataBase() {
		// 查找sqlserver的库名
		EDataSourceType dataSourceType = EDataSourceType.MSSQL;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
		outputDataSrcArgs.setDbHost("127.0.0.1");
		outputDataSrcArgs.setDbName("master");
		outputDataSrcArgs.setDbUserName("sa");
		outputDataSrcArgs.setDbPwd("123");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
//		outputDataSrcArgs.setTableName("test3");
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		List<String> list = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
		for (String string : list) {
			System.out.println(string);
		}
	}

//	@Test
	public void queryDataBaseTables() { // 根据数据库名称查该库下的所有数据表名称

		// 测试sqlserver
		/*
		 * EDataSourceType dataSourceType = EDataSourceType.MSSQL; DataSourceBase
		 * outputMssqlDataSrc = (DataSourceBase)
		 * DataSourceFactory.createDataSource(dataSourceType); DbConfigArgs
		 * outputDataSrcArgs = new DbConfigArgs();
		 * outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
		 * outputDataSrcArgs.setDbHost("127.0.0.1");
		 * outputDataSrcArgs.setDbName("tmpRWDBNew");
		 * outputDataSrcArgs.setDbUserName("sa"); outputDataSrcArgs.setDbPwd("123");
		 * outputDataSrcArgs.setDataSrcType(dataSourceType);
		 * outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs); List<String> list =
		 * outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc); for (String string :
		 * list) { System.out.println(string); }
		 */

		// 测试mysql
		/*EDataSourceType dataSourceType = EDataSourceType.MYSQL;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.MYSQL_DBTYPE);
		outputDataSrcArgs.setDbHost("192.168.0.70");
		outputDataSrcArgs.setDbName("test");
		outputDataSrcArgs.setDbUserName("root");
		outputDataSrcArgs.setDbPwd("root");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
		List<String> list = outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc);
		for (String string : list) {
			System.out.println(string);
		}*/
		
		
		/*EDataSourceType dataSourceType = EDataSourceType.PostGIS;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
		outputDataSrcArgs.setDbHost("192.168.0.112");
		outputDataSrcArgs.setDbName("postgres");
		outputDataSrcArgs.setDbUserName("postgres");
		outputDataSrcArgs.setDbPwd("3edc9ijn");
		outputDataSrcArgs.setDbPort("5432");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
		List<String> list = outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc);
		for (String string : list) {
			System.out.println(string);
		}*/
		
		
		EDataSourceType dataSourceType = EDataSourceType.ORACLE;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
		outputDataSrcArgs.setDbHost("192.168.0.70");
		outputDataSrcArgs.setDbName("dyslpc");
		outputDataSrcArgs.setDbUserName("fda");
		outputDataSrcArgs.setDbPwd("fda");
		outputDataSrcArgs.setDbPort("1521");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
		List<String> list = outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc);
		for (String string : list) {
			System.out.println(string);
		}

	}

//	@Test
	public void testOldMssqlToShp() {
		EDataSourceType dataSourceType = EDataSourceType.MSSQL;
		DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		OldDbConfigArgs inputDataSrcArgs = new OldDbConfigArgs();
		inputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
//		inputDataSrcArgs.setDbHost("192.168.0.210");
//		inputDataSrcArgs.setDbHost("192.168.0.70\\sql2008");
		inputDataSrcArgs.setDbHost("192.168.0.108");
		inputDataSrcArgs.setDbName("SD_RiverManage");
		inputDataSrcArgs.setDbUserName("sa");
		inputDataSrcArgs.setDbPwd("123");
//		inputDataSrcArgs.setDbPwd("3edc9ijn~");
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setTableName("NET_RIVER_SECTION");
//		inputDataSrcArgs.setFilter("DataType=1040603");
//		inputDataSrcArgs.setTableName("test5");
//		inputDataSrcArgs.setTableName("watershd");
//		inputDataSrcArgs.setFieldName("vertex");
//		inputDataSrcArgs.setFieldName("startx,starty");
		inputDataSrcArgs.setFieldName("endx,endy");
		inputDataSrcArgs.setType("Point");
//		inputDataSrcArgs.setFieldName("vertex");
//		inputDataSrcArgs.setType("Line");
//		inputDataSrcArgs.setBinaryDataType("float");
		inputDataSrcArgs.setBinaryDataType("double");
		inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);

		try {
			Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
//			for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//				System.out.println(m.getKey() + "   " + m.getValue());
//
//			}
			
//			Map<String, Object> fieldMap  = new HashMap<String, Object>();
//			fieldMap.put("vertex", fieldToTypeMap.get("vertex"));
//			fieldMap.put("PID", fieldToTypeMap.get("PID"));

//			Map<String, Object> fieldTo = new LinkedHashMap<String, Object>();
//			fieldTo.put("STCD", fieldToTypeMap.get("STCD"));
			//fieldTo.put("vertex", fieldToTypeMap.get("vertex"));
//			fieldTo.put("LGTD", fieldToTypeMap.get("LGTD"));
//			fieldTo.put("LTTD", fieldToTypeMap.get("LTTD"));
//			fieldTo.put("ValidTM", fieldToTypeMap.get("ValidTM"));
			Map<String, Object> tmpMap = new HashMap<String,Object>();
			tmpMap.put("hdbm", fieldToTypeMap.get("hdbm"));
			tmpMap.put("hdmc", fieldToTypeMap.get("hdmc"));
			tmpMap.put("rvcd", fieldToTypeMap.get("rvcd"));
			tmpMap.put("rvnm", fieldToTypeMap.get("rvnm"));
//			tmpMap.put("startx", fieldToTypeMap.get("startx"));
//			tmpMap.put("starty", fieldToTypeMap.get("starty"));
			tmpMap.put("endx", fieldToTypeMap.get("endx"));
			tmpMap.put("endy", fieldToTypeMap.get("endy"));
			tmpMap.put("xzqh", fieldToTypeMap.get("xzqh"));
//			tmpMap.put("the_geom", fieldToTypeMap.get("the_geom"));
			inputDataSrcArgs.setFieldToTypeMap(tmpMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataSourceType = EDataSourceType.Shp;
		DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputDataSrcArgs.setVectorFilePath("C:\\Users\\TZX_XHH\\Desktop\\000HD_201903221146\\NET_RIVER_SECTION_END.shp");
		outputDataSrcArgs.setCharset("GBK");
		outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		outputShpDataSrc.exportOldData(inputMssqlDataSrc);
	}

	@Test
	public void testOldShpToMSSQL() {
		EDataSourceType dataSourceType = EDataSourceType.Shp;
		DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setVectorFilePath("C:\\Users\\TZX_XHH\\Desktop\\111\\watershed.shp");
		inputDataSrcArgs.setCharset("GBK");
		inputDataSrcArgs.setFieldName("vertex");
		inputDataSrcArgs.setBinaryDataType("double");
		inputDataSrcArgs.setType("Polygon");
		inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
		try {
			Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

//			Map<String, Object> fieldMap  = new HashMap<String, Object>();
//			fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
//			fieldMap.put("Remark", fieldToTypeMap.get("Remark"));
			
			//打印
//			for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//				System.out.println(m.getKey() + "   " + m.getValue());
//
//			}
			
			inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dataSourceType = EDataSourceType.MSSQL;
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
		outputDataSrcArgs.setDbHost("127.0.0.1");
		outputDataSrcArgs.setDbName("MyDatabase");
		outputDataSrcArgs.setDbUserName("sa");
		outputDataSrcArgs.setDbPwd("123");
		outputDataSrcArgs.setTableName("watershed2");
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		outputMssqlDataSrc.exportOldData(inputShpDataSrc);
	}
	
	
//	 @Test
		public void queryDataBase2MySQL() {
			// 查找sqlserver的库名
			EDataSourceType dataSourceType = EDataSourceType.MYSQL;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbName("mysql");
			outputDataSrcArgs.setDbPort("3306");
			outputDataSrcArgs.setDbUserName("root");
			outputDataSrcArgs.setDbPwd("root");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
//			outputDataSrcArgs.setTableName("test3");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			List<String> list = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
			for (String string : list) {
				System.out.println(string);
			}
		}
	 
	 
//		@Test
		public void queryDataBaseTables2MySql() { // 根据数据库名称查该库下的所有数据表名称
			// 测试mysql
			EDataSourceType dataSourceType = EDataSourceType.MYSQL;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.MYSQL_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbName("test");
			outputDataSrcArgs.setDbUserName("root");
			outputDataSrcArgs.setDbPwd("root");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
			List<String> list = outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc);
			for (String string : list) {
				System.out.println(string);
			}

		}
		
//		@Test
		public void testOldShpToMySQL() {
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath("F:/luyu.shp");
			inputDataSrcArgs.setCharset("GBK");
			inputDataSrcArgs.setFieldName("vertex");
			inputDataSrcArgs.setBinaryDataType("double");
			inputDataSrcArgs.setType("Polygon");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
				
				
				Map<String, Object> fieldMap  = new HashMap<String, Object>();
				fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
				fieldMap.put("Remark", fieldToTypeMap.get("Remark"));
				//打印
//				for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//					System.out.println(m.getKey() + "   " + m.getValue());
//
//				}
				
				inputDataSrcArgs.setFieldToTypeMap(fieldMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.MYSQL;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.MYSQL_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbPort("3306");
			outputDataSrcArgs.setDbName("gopush");
			outputDataSrcArgs.setDbUserName("root");
			outputDataSrcArgs.setDbPwd("root");
			outputDataSrcArgs.setTableName("test1");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportOldData(inputShpDataSrc);
		}
		
		
//		@Test
		public void queryOracleSpaceName() {
			// 查找sqlserver的库名
			EDataSourceType dataSourceType = EDataSourceType.ORACLE;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbName("dyslpc");
			outputDataSrcArgs.setDbUserName("fda");
			outputDataSrcArgs.setDbPwd("fda");
			outputDataSrcArgs.setDbPort("1521");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
//			outputDataSrcArgs.setTableName("test3");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			List<String> list = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
			for (String string : list) {
				System.out.println(string);
			}
		}
		
		
		
//		 @Test
		public void queryDataBasePostGis() {
			// 查找sqlserver的库名
			EDataSourceType dataSourceType = EDataSourceType.PostGIS;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.112");
			outputDataSrcArgs.setDbName("postgres");
			outputDataSrcArgs.setDbUserName("postgres");
			outputDataSrcArgs.setDbPwd("3edc9ijn");
			outputDataSrcArgs.setDbPort("5432");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
//			outputDataSrcArgs.setTableName("test3");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			List<String> list = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
			for (String string : list) {
				System.out.println(string);
			}
		}
		
		
		
//		@Test
		public void testShp2PostGis() {
			// shp to mysql. EDataSourceType
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath("G:/xhh/全国矢量地图大全 shp格式/1/bou1_4l.shp");
			inputDataSrcArgs.setCharset("GBK");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
				
				for (Map.Entry<String, Object> it : fieldToTypeMap.entrySet()) {
					System.out.println(it.getKey() + "   " + it.getValue());
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block e.printStackTrace();
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.PostGIS;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
			outputDataSrcArgs.setDbHost("127.0.0.1");
			outputDataSrcArgs.setDbName("postgis_24_sample");
			outputDataSrcArgs.setDbUserName("postgres");
			outputDataSrcArgs.setDbPwd("123");
			outputDataSrcArgs.setDbPort("5432");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setTableName("test");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportData(inputShpDataSrc);
		}
		
		
		
//		@Test
		public void testPostGISToShp() {
			EDataSourceType dataSourceType = EDataSourceType.PostGIS;
			DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs inputDataSrcArgs = new DbConfigArgs();
			inputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
			inputDataSrcArgs.setDbHost("127.0.0.1");
			inputDataSrcArgs.setDbName("postgis_24_sample");
			inputDataSrcArgs.setDbUserName("postgres");
			inputDataSrcArgs.setDbPwd("123");
			inputDataSrcArgs.setDbPort("5432");
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setTableName("test");
			inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.Shp;
			DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			outputShpDataSrc.setProgressListener(new ProgressListener() {
				
				@Override
				public void onProcProgress(String taskNo, int progress) {
					// TODO Auto-generated method stub
					System.out.println(taskNo + "   " +progress);
				}
			});
			VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
			outputDataSrcArgs.setTaskNo("1");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setVectorFilePath("G:/111.shp");
			outputDataSrcArgs.setCharset("GBK");
			outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputShpDataSrc.exportData(inputMssqlDataSrc);
		}
		
		
		
//		@Test
		public void testOldShpToPostGis() {
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath("G:/xhh/全国矢量地图大全 shp格式/1/bou1_4l.shp");
			inputDataSrcArgs.setCharset("GBK");
			inputDataSrcArgs.setFieldName("vertex");
			inputDataSrcArgs.setBinaryDataType("double");
			inputDataSrcArgs.setType("Line");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

//				Map<String, Object> fieldMap  = new HashMap<String, Object>();
//				fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
//				fieldMap.put("Remark", fieldToTypeMap.get("Remark"));
				
				//打印
//				for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//					System.out.println(m.getKey() + "   " + m.getValue());
	//
//				}
				
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.PostGIS;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
			outputDataSrcArgs.setDbHost("127.0.0.1");
			outputDataSrcArgs.setDbName("postgis_24_sample");
			outputDataSrcArgs.setDbUserName("postgres");
			outputDataSrcArgs.setDbPwd("123");
			outputDataSrcArgs.setDbPort("5432");
			outputDataSrcArgs.setTableName("test2");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportOldData(inputShpDataSrc);
		}
		
		
//		@Test
		public void testOldPostGisToShp() {
			EDataSourceType dataSourceType = EDataSourceType.PostGIS;
			DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldDbConfigArgs inputDataSrcArgs = new OldDbConfigArgs();
			inputDataSrcArgs.setDbType(DbDataSource.POSTGIS_DBTYPE);
			inputDataSrcArgs.setDbHost("127.0.0.1");
			inputDataSrcArgs.setDbName("postgis_24_sample");
			inputDataSrcArgs.setDbUserName("postgres");
			inputDataSrcArgs.setDbPwd("123");
			inputDataSrcArgs.setDbPort("5432");
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setTableName("test2");
			inputDataSrcArgs.setFieldName("vertex");
			inputDataSrcArgs.setType("Line");
			inputDataSrcArgs.setBinaryDataType("double");
			inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);

			try {
				Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
				
//				Map<String, Object> fieldMap  = new HashMap<String, Object>();
//				fieldMap.put("vertex", fieldToTypeMap.get("vertex"));
//				fieldMap.put("PID", fieldToTypeMap.get("PID"));

				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.Shp;
			DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setVectorFilePath("G:/111Line.shp");
			outputDataSrcArgs.setCharset("GBK");
			outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputShpDataSrc.exportOldData(inputMssqlDataSrc);
		}
		
		
		
//		@Test
		public void testShp2Oracle() {
			// shp to mysql. EDataSourceType
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
//			inputDataSrcArgs.setVectorFilePath("D:\\磁盘数据2\\数据&图层\\陕西\\陕西政区_region.shp");
			inputDataSrcArgs.setVectorFilePath("D:\\磁盘数据2\\xhh\\TZX_OBJ_VECTOR.shp");
//			inputDataSrcArgs.setVectorFilePath("D:\\szjd.shp");
//			inputDataSrcArgs.setVectorFilePath("D:\\磁盘数据2\\数据&图层\\陕西\\Fixed_layer.shp");
			inputDataSrcArgs.setCharset("GBK");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
				
				Map<String, Object> fieldMap  = new HashMap<String, Object>();
				fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
				
				inputDataSrcArgs.setFieldToTypeMap(fieldMap);
				
				for (Map.Entry<String, Object> it : fieldToTypeMap.entrySet()) {
					System.out.println(it.getKey() + "   " + it.getValue());
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block e.printStackTrace();
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.ORACLE;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbName("dyslpc");
			outputDataSrcArgs.setDbUserName("fda");
			outputDataSrcArgs.setDbPwd("fda");
			outputDataSrcArgs.setDbPort("1521");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setTableName("test2");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportData(inputShpDataSrc);
		}
		
		
//		 @Test
		public void testOracleToShp() {
			EDataSourceType dataSourceType = EDataSourceType.ORACLE;
			DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs inputDataSrcArgs = new DbConfigArgs();
			inputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
			inputDataSrcArgs.setDbHost("192.168.0.70");
			inputDataSrcArgs.setDbName("dyslpc");
			inputDataSrcArgs.setDbUserName("fda");
			inputDataSrcArgs.setDbPwd("fda");
			inputDataSrcArgs.setDbPort("1521");
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setTableName("TEST2");
			inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.Shp;
			DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			outputShpDataSrc.setProgressListener(new ProgressListener() {
				
				@Override
				public void onProcProgress(String taskNo, int progress) {
					// TODO Auto-generated method stub
					System.out.println(taskNo + "   " +progress);
				}
			});
			VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
			outputDataSrcArgs.setTaskNo("1");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setVectorFilePath("D:/222.shp");
			outputDataSrcArgs.setCharset("GBK");
			outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputShpDataSrc.exportData(inputMssqlDataSrc);
		}
		
		
		
//		@Test
		public void testOldShpToOracle() {
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
//			inputDataSrcArgs.setVectorFilePath("D:/磁盘数据2/xhh/TZX_OBJ_VECTOR.shp");//面
//			inputDataSrcArgs.setVectorFilePath("D:/szjd.shp");//点
			inputDataSrcArgs.setVectorFilePath("D:\\磁盘数据2\\xhh\\recive\\新宾县shp\\新宾满族自治县.shp");//线
			inputDataSrcArgs.setCharset("GBK");
			inputDataSrcArgs.setFieldName("vertex");
//			inputDataSrcArgs.setFieldName("LGTDC,LTTDC");
			inputDataSrcArgs.setBinaryDataType("double");
//			inputDataSrcArgs.setType("Point");
			inputDataSrcArgs.setType("LineString");
//			inputDataSrcArgs.setType("Polygon");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

//				Map<String, Object> fieldMap  = new HashMap<String, Object>();
//				fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
//				fieldMap.put("Remark", fieldToTypeMap.get("Remark"));
				
				//打印
//				for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//					System.out.println(m.getKey() + "   " + m.getValue());
	//
//				}
				
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.ORACLE;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70");
			outputDataSrcArgs.setDbName("dyslpc");
			outputDataSrcArgs.setDbUserName("fda");
			outputDataSrcArgs.setDbPwd("fda");
			outputDataSrcArgs.setDbPort("1521");
			outputDataSrcArgs.setTableName("test8");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportOldData(inputShpDataSrc);
		}
		
		
//		@Test
		public void testOldOracleToShp() {
			EDataSourceType dataSourceType = EDataSourceType.ORACLE;
			DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldDbConfigArgs inputDataSrcArgs = new OldDbConfigArgs();
			inputDataSrcArgs.setDbType(DbDataSource.ORACLE_DBTYPE);
			inputDataSrcArgs.setDbHost("192.168.0.70");
			inputDataSrcArgs.setDbName("dyslpc");
			inputDataSrcArgs.setDbUserName("fda");
			inputDataSrcArgs.setDbPwd("fda");
			inputDataSrcArgs.setDbPort("1521");
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setTableName("test8");
			inputDataSrcArgs.setFieldName("vertex");
			inputDataSrcArgs.setType("Line");
			inputDataSrcArgs.setBinaryDataType("double");
			inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);

			try {
				Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
				
//				Map<String, Object> fieldMap  = new HashMap<String, Object>();
//				fieldMap.put("vertex", fieldToTypeMap.get("vertex"));
//				fieldMap.put("PID", fieldToTypeMap.get("PID"));

				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.Shp;
			DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setVectorFilePath("D:/888.shp");
			outputDataSrcArgs.setCharset("GBK");
			outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputShpDataSrc.exportOldData(inputMssqlDataSrc);
		}
		
		
		
		
	//	@Test
		public void testShpOneValueToMssql() {
			// shp to mssql. EDataSourceType
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath("D:\\xhh\\recice\\000ME\\省line.shp");
			inputDataSrcArgs.setCharset("GBK");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block e.printStackTrace();
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.MSSQL;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70\\sql2008");
			outputDataSrcArgs.setDbName("HENAN_RES_MODEL_NEW");
			outputDataSrcArgs.setDbUserName("sa");
			outputDataSrcArgs.setDbPwd("3edc9ijn~");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputDataSrcArgs.setTableName("test");
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportData(inputShpDataSrc);

		}
		
		
		//@Test
		public void testOldShpOneValueToMSSQL() {
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath("D:\\xhh\\recice\\000ME\\000ME\\省line.shp");
			inputDataSrcArgs.setCharset("UTF-8");
			inputDataSrcArgs.setFieldName("vertex");
			inputDataSrcArgs.setBinaryDataType("float");
//			inputDataSrcArgs.setBinaryDataType("double");
			inputDataSrcArgs.setType("LineString");
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

//				Map<String, Object> fieldMap  = new HashMap<String, Object>();
//				fieldMap.put("the_geom", fieldToTypeMap.get("the_geom"));
//				fieldMap.put("Remark", fieldToTypeMap.get("Remark"));
				
				//打印
//				for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
//					System.out.println(m.getKey() + "   " + m.getValue());
	//
//				}
				
				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dataSourceType = EDataSourceType.MSSQL;
			DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setDbType(DbDataSource.SQLSERVER_DBTYPE);
			outputDataSrcArgs.setDbHost("192.168.0.70\\sql2008");
			outputDataSrcArgs.setDbName("HENAN_RES_MODEL_NEW");
			outputDataSrcArgs.setDbUserName("sa");
			outputDataSrcArgs.setDbPwd("3edc9ijn~");
			outputDataSrcArgs.setTableName("test");
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			outputMssqlDataSrc.exportOldData(inputShpDataSrc);
		}


}
