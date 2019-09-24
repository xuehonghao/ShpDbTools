package com.tzx.datasource;

import java.awt.Point;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.Schema;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;

import com.tzx.datasource.argument.DataSourceArgs;
import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.datasource.argument.OldVectorFileConfigArgs;
import com.tzx.datasource.inter.InputDataSource;
import com.tzx.tool.DbUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 数据库源
 * 
 * @author Administrator
 *
 */
public class DbDataSource extends DataSourceBase {
	public static final String SQLSERVER_DBTYPE = "sqlserver";
	public static final String MYSQL_DBTYPE = "mysql";
	public static final String ORACLE_DBTYPE = "oracle";
	public static final String POSTGIS_DBTYPE = "postgis";

	private class InsertDataArgs {
		public JDBCDataStore jdbcDs;
		public SimpleFeatureType schema;
	}

	/**
	 * 数据库工具
	 */
	protected DbUtils dbUtils;

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
		Map<String, Object> fieldToTypeMap = new LinkedHashMap<String, Object>();
		DataStore ds = buildDataStore(true);
		if (ds == null) {
			return fieldToTypeMap;
		}

		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			String tableName = outputDbCfgArgs.getTableName();
			if(outputDbCfgArgs.getDbType().equals(ORACLE_DBTYPE)) {
				tableName = tableName.toUpperCase();
			}
			List<AttributeDescriptor> attrList = ds.getFeatureSource(tableName).getSchema()
					.getAttributeDescriptors();
			for (AttributeDescriptor attr : attrList) {
				fieldToTypeMap.put(attr.getLocalName(), attr.getType().getBinding());
			}

			// String[] fields = new String[1];
			// fields[0] = "the_geom";
//			if(fieldToTypeMap.containsKey("the_geom") || fieldToTypeMap.containsKey("THE_GEOM")) {
				if(fieldToTypeMap.containsKey("geo") || fieldToTypeMap.containsKey("geo")) {
				return fieldToTypeMap;
			}
			
			final Filter filter = Filter.INCLUDE;// ECQL.toFilter("NAME=610116");
			// final Query query = new Query(tableName, filter, fields);
			final Query query = new Query(tableName, filter);
			query.setMaxFeatures(1);
			FeatureReader<SimpleFeatureType, SimpleFeature> fr = ds.getFeatureReader(query, Transaction.AUTO_COMMIT);
			while (fr.hasNext()) {
				SimpleFeature feature = fr.next();
				Geometry geo = (Geometry) feature.getDefaultGeometry();
				if (geo != null) {
					String geoType = geo.getGeometryType();
					if (geoType.equals("Point")) {
						fieldToTypeMap.put("the_geom", Point.class);
					} else if (geoType.equals("MultiPoint")) {
						fieldToTypeMap.put("the_geom", MultiPoint.class);
					} else if (geoType.equals("LineString")) {
						fieldToTypeMap.put("the_geom", LineString.class);
					}else if (geoType.equals("MultiLineString")) {
						fieldToTypeMap.put("the_geom", MultiLineString.class);
					} else if (geoType.equals("Polygon")) {
						fieldToTypeMap.put("the_geom", Polygon.class);
					} else if (geoType.equals("MultiPolygon")) {
						fieldToTypeMap.put("the_geom", MultiPolygon.class);
					}
				}
				
				/*if (geo != null) {
					String geoType = geo.getGeometryType();
					if (geoType.equals("Point") || geoType.equals("MultiPoint")) {
						fieldToTypeMap.put("the_geom", Point.class);
					}else if (geoType.equals("LineString") || geoType.equals("MultiLineString")) {
						fieldToTypeMap.put("the_geom", LineString.class);
					}else if (geoType.equals("Polygon") || geoType.equals("MultiPolygon")) {
						fieldToTypeMap.put("the_geom", Polygon.class);
					}
				}*/
			}
			fr.close();
			return fieldToTypeMap;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ds.dispose();
		}

		return fieldToTypeMap;
	}

	@Override
	public Object getVectorData() {
		// TODO Auto-generated method stub
		DataStore ds = buildDataStore(true);
		if (ds == null) {
			return null;
		}

		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			String tableName = outputDbCfgArgs.getTableName();
			if(outputDbCfgArgs.getDbType().equals(ORACLE_DBTYPE)) {
				tableName = tableName.toUpperCase();
			}
			Map<String, Object> fieldToTypeMap = outputDbCfgArgs.getFieldToTypeMap();
			String[] fields = new String[fieldToTypeMap.size()];
			int index = 0;
			for (Map.Entry<String, Object> entry : fieldToTypeMap.entrySet()) {
				fields[index++] = entry.getKey();
			}

			Filter filter = null;
			if (outputDbCfgArgs.getFilter() == null || "".equals(outputDbCfgArgs.getFilter())) {
				filter = Filter.INCLUDE;// ECQL.toFilter("NAME=610116");
			} else {
				filter = ECQL.toFilter(outputDbCfgArgs.getFilter());
			}
			final Query query = new Query(tableName, filter, fields);
			FeatureReader<SimpleFeatureType, SimpleFeature> fr = ds.getFeatureReader(query, Transaction.AUTO_COMMIT);
			return fr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//ds.dispose();
		}

		return null;
	}

	public DbUtils createDbUtils(DataSourceBase args) {
		return null;
	}

	@Override
	public long getRecordNum() {
		DataStore ds = buildDataStore(true);
		if (ds == null) {
			return 0;
		}
		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			String tableName = outputDbCfgArgs.getTableName();
			if(outputDbCfgArgs.getDbType().equals(ORACLE_DBTYPE)) {
				tableName = tableName.toUpperCase();
			}
			SimpleFeatureSource source = ds.getFeatureSource(tableName);

			Filter filter = null;
			if (outputDbCfgArgs.getFilter() == null || "".equals(outputDbCfgArgs.getFilter())) {
				filter = Filter.INCLUDE;// ECQL.toFilter("NAME=610116");
			} else {
				filter = ECQL.toFilter(outputDbCfgArgs.getFilter());
			}
			SimpleFeatureCollection features = source.getFeatures(filter);

			int count = features.size();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

		// DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
		// DbUtils dbu = createDbUtils(this);
		// Connection con = dbu.getConnection();
		// Statement st;
		// ResultSet rs;
		// try {
		// String sql = "select count(1) from " +
		// outputDbCfgArgs.getTableName();
		// if(outputDbCfgArgs.getFilter() != null &&
		// !"".equals(outputDbCfgArgs.getFilter())) {
		// sql = sql + " where " + outputDbCfgArgs.getFilter();
		// }
		// st = con.createStatement();
		// rs = st.executeQuery(sql);
		// rs.next();
		// int row = rs.getInt(1);
		// rs.close();
		// st.close();
		// con.close();
		// return row;
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return 0;

	}

	@Override
	public boolean exportData(InputDataSource inputDataSrouce) {
		// TODO Auto-generated method stub
		// 1. 数据源类型检测.
		DataSourceBase inputDataSrcBase = (DataSourceBase) inputDataSrouce;
		DataSourceArgs inputDataSrcArgs = inputDataSrcBase.getDataSrcCfgArgs();
		if (inputDataSrcArgs.getDataSrcType() == dataSrcCfgArgs.getDataSrcType()) {
			return false;
		}

		// 2. 获取矢量数据和字段信息.
		Map<String, Object> fieldToTypeMap = inputDataSrcArgs.getFieldToTypeMap();
		Object inputData = inputDataSrouce.getVectorData();
		int recordNum = (int) inputDataSrouce.getRecordNum();

		// 3. 创建数据表.
		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			InsertDataArgs insertDataArgs = createTable(outputDbCfgArgs, fieldToTypeMap);

			// 4. 将数据插入数据表.
			insertDatas(outputDbCfgArgs,inputDataSrouce, insertDataArgs, inputData, fieldToTypeMap);

		} catch (Exception e) {
			e.printStackTrace(); // TODO 删除这一行
			return false;
		}

		return true;
	}

	@Override
	public boolean exportOldData(InputDataSource inputDataSrouce) {
		// 1. 数据源类型检测.
		DataSourceBase inputDataSrcBase = (DataSourceBase) inputDataSrouce;
		OldVectorFileConfigArgs inputDataSrcArgs = (OldVectorFileConfigArgs) inputDataSrcBase.getDataSrcCfgArgs();
		if (inputDataSrcArgs.getDataSrcType() == dataSrcCfgArgs.getDataSrcType()) {
			return false;
		}
		int recordNum = (int) inputDataSrouce.getRecordNum();

		// 2. 获取矢量数据和字段信息.
		Map<String, Object> fieldToTypeMap = inputDataSrcArgs.getFieldToTypeMap();
		Object inputData = inputDataSrouce.getVectorData();

		// 3.添加字段信息
		String[] fieldNames = inputDataSrcArgs.getFieldName().split(",");
		if (inputDataSrcArgs.getType().equals("Point")) {
			if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
				fieldToTypeMap.put(fieldNames[0], Float.class);
				fieldToTypeMap.put(fieldNames[1], Float.class);
			} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
				fieldToTypeMap.put(fieldNames[0], Double.class);
				fieldToTypeMap.put(fieldNames[1], Double.class);
			}
		} else {
			// fieldToTypeMap.put(fieldNames[0], new byte[] {}.getClass());
			fieldToTypeMap.put(fieldNames[0], byte[].class);
		}

		// 打印
		System.out.println("*************************************");
		for (Map.Entry<String, Object> m : fieldToTypeMap.entrySet()) {
			System.out.println(m.getKey() + "   " + m.getValue());
		}

		// 4. 创建数据表.
		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			InsertDataArgs insertDataArgs = createTable(outputDbCfgArgs, fieldToTypeMap);
			
			String typeName = insertDataArgs.schema.getTypeName();
			if(outputDbCfgArgs.getDbType().equals("oracle")) {
				typeName = typeName.toUpperCase();
			}

			// 5. 将数据插入数据表.
			// 开始写入数据
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = insertDataArgs.jdbcDs
					.getFeatureWriter(typeName, Transaction.AUTO_COMMIT);

			FeatureReader<SimpleFeatureType, SimpleFeature> fr = (FeatureReader<SimpleFeatureType, SimpleFeature>) inputData;
			System.out.println("开始写入数据");
			int num = 0;
			while (fr.hasNext()) {
				if(num == 340) {
					System.out.println("");
				}
				SimpleFeature feature = fr.next();
				writer.hasNext();
				SimpleFeature feature1 = writer.next();
				Geometry geo = (Geometry) feature.getDefaultGeometry();
				if (geo != null) {
					String geoType = geo.getGeometryType();
					if (geoType.equals("Point") || geoType.equals("MultiPoint")) {// 点
						Coordinate[] c = geo.getCoordinates();
						for (Coordinate coordinate : c) {
							if(outputDbCfgArgs.getDbType().equals("oracle")) {
								feature1.setAttribute(fieldNames[0].toUpperCase(), coordinate.x);
								feature1.setAttribute(fieldNames[1].toUpperCase(), coordinate.y);
							}else {
								feature1.setAttribute(fieldNames[0], coordinate.x);
								feature1.setAttribute(fieldNames[1], coordinate.y);
							}
						}

					} else if (geoType.equals("LineString") || geoType.equals("MultiLineString")) {// 线

						Coordinate[] c = geo.getCoordinates();
						System.out.println("**************");
						byte[] value = null;
						if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
							value = new byte[c.length * 2 * 4];
						} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
							value = new byte[c.length * 2 * 8];
						}

						int valueNum = 0;
						for (Coordinate coordinate : c) {

							// 这是测试
							// System.out.println(coordinate);
							// System.out.println(coordinate.x);
							// System.out.println(coordinate.y);
							// System.out.println(coordinate.z);
							// System.out.println("-----------------");

							if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
								// float转byte[]
								// 把float转换为byte[]
								int fbit = Float.floatToIntBits((float) coordinate.x);
								byte[] b = new byte[4];
								for (int i = 0; i < 4; i++) {
									b[i] = (byte) (fbit >> (24 - i * 8));
								}

								// 翻转数组
								int len = b.length;
								// 建立一个与源数组元素类型相同的数组
								byte[] dest = new byte[len];
								// 为了防止修改源数组，将源数组拷贝一份副本
								System.arraycopy(b, 0, dest, 0, len);
								byte temp;
								// 将顺位第i个与倒数第i个交换
								for (int i = 0; i < len / 2; ++i) {
									temp = dest[i];
									dest[i] = dest[len - i - 1];
									dest[len - i - 1] = temp;
								}

								value[valueNum] = dest[0];
								value[valueNum + 1] = dest[1];
								value[valueNum + 2] = dest[2];
								value[valueNum + 3] = dest[3];

								fbit = Float.floatToIntBits((float) coordinate.y);
								b = new byte[4];
								for (int i = 0; i < 4; i++) {
									b[i] = (byte) (fbit >> (24 - i * 8));
								}
								// 翻转数组
								len = b.length;
								// 建立一个与源数组元素类型相同的数组
								dest = new byte[len];
								// 为了防止修改源数组，将源数组拷贝一份副本
								System.arraycopy(b, 0, dest, 0, len);
								// 将顺位第i个与倒数第i个交换
								for (int i = 0; i < len / 2; ++i) {
									temp = dest[i];
									dest[i] = dest[len - i - 1];
									dest[len - i - 1] = temp;
								}

								value[valueNum + 4] = dest[0];
								value[valueNum + 5] = dest[1];
								value[valueNum + 6] = dest[2];
								value[valueNum + 7] = dest[3];

								valueNum += 8;
							} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
								// 将double转成byte[]
								long fbit = Double.doubleToRawLongBits(coordinate.x);
								byte[] byteRet = new byte[8];
								for (int i = 0; i < 8; i++) {
									byteRet[i] = (byte) ((fbit >> 8 * i) & 0xff);
								}

								value[valueNum] = byteRet[0];
								value[valueNum + 1] = byteRet[1];
								value[valueNum + 2] = byteRet[2];
								value[valueNum + 3] = byteRet[3];
								value[valueNum + 4] = byteRet[4];
								value[valueNum + 5] = byteRet[5];
								value[valueNum + 6] = byteRet[6];
								value[valueNum + 7] = byteRet[7];

								fbit = Double.doubleToRawLongBits(coordinate.y);
								byteRet = new byte[8];
								for (int i = 0; i < 8; i++) {
									byteRet[i] = (byte) ((fbit >> 8 * i) & 0xff);
								}

								value[valueNum + 8] = byteRet[0];
								value[valueNum + 9] = byteRet[1];
								value[valueNum + 10] = byteRet[2];
								value[valueNum + 11] = byteRet[3];
								value[valueNum + 12] = byteRet[4];
								value[valueNum + 13] = byteRet[5];
								value[valueNum + 14] = byteRet[6];
								value[valueNum + 15] = byteRet[7];

								valueNum += 16;

							}
						}
						if(outputDbCfgArgs.getDbType().equals("oracle")) {
							feature1.setAttribute(fieldNames[0].toUpperCase(), value);
						}else {
							feature1.setAttribute(fieldNames[0], value);
						}

					} else if (geoType.equals("Polygon") || geoType.equals("MultiPolygon")) {// 面

						Coordinate[] c = geo.getCoordinates();
						System.out.println("**************");
						byte[] value = null;
						if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
							value = new byte[c.length * 2 * 4];
						} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
							value = new byte[c.length * 2 * 8];
						}

						int valueNum = 0;
						for (Coordinate coordinate : c) {

							// 这是测试
							// System.out.println(coordinate);
							// System.out.println(coordinate.x);
							// System.out.println(coordinate.y);
							// System.out.println(coordinate.z);
							// System.out.println("-----------------");

							if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
								// float转byte[]
								// 把float转换为byte[]
								int fbit = Float.floatToIntBits((float) coordinate.x);
								byte[] b = new byte[4];
								for (int i = 0; i < 4; i++) {
									b[i] = (byte) (fbit >> (24 - i * 8));
								}

								// 翻转数组
								int len = b.length;
								// 建立一个与源数组元素类型相同的数组
								byte[] dest = new byte[len];
								// 为了防止修改源数组，将源数组拷贝一份副本
								System.arraycopy(b, 0, dest, 0, len);
								byte temp;
								// 将顺位第i个与倒数第i个交换
								for (int i = 0; i < len / 2; ++i) {
									temp = dest[i];
									dest[i] = dest[len - i - 1];
									dest[len - i - 1] = temp;
								}

								value[valueNum] = dest[0];
								value[valueNum + 1] = dest[1];
								value[valueNum + 2] = dest[2];
								value[valueNum + 3] = dest[3];

								fbit = Float.floatToIntBits((float) coordinate.y);
								b = new byte[4];
								for (int i = 0; i < 4; i++) {
									b[i] = (byte) (fbit >> (24 - i * 8));
								}
								// 翻转数组
								len = b.length;
								// 建立一个与源数组元素类型相同的数组
								dest = new byte[len];
								// 为了防止修改源数组，将源数组拷贝一份副本
								System.arraycopy(b, 0, dest, 0, len);
								// 将顺位第i个与倒数第i个交换
								for (int i = 0; i < len / 2; ++i) {
									temp = dest[i];
									dest[i] = dest[len - i - 1];
									dest[len - i - 1] = temp;
								}

								value[valueNum + 4] = dest[0];
								value[valueNum + 5] = dest[1];
								value[valueNum + 6] = dest[2];
								value[valueNum + 7] = dest[3];

								valueNum += 8;
							} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
								// 将double转成byte[]
								long fbit = Double.doubleToRawLongBits(coordinate.x);
//								Double.longBitsToDouble(fbit);
								byte[] byteRet = new byte[8];
								for (int i = 0; i < 8; i++) {
									byteRet[i] = (byte) ((fbit >> 8 * i) & 0xff);
								}
								
//								long asas = 0;
//				        		for (int i = 0; i < 8; i++) {
//				        			asas |= ((long) (byteRet[i] & 0xff)) << (8 * i);
//				        		}
//				        		Double.longBitsToDouble(asas);

								value[valueNum] = byteRet[0];
								value[valueNum + 1] = byteRet[1];
								value[valueNum + 2] = byteRet[2];
								value[valueNum + 3] = byteRet[3];
								value[valueNum + 4] = byteRet[4];
								value[valueNum + 5] = byteRet[5];
								value[valueNum + 6] = byteRet[6];
								value[valueNum + 7] = byteRet[7];

								fbit = Double.doubleToRawLongBits(coordinate.y);
								byteRet = new byte[8];
								for (int i = 0; i < 8; i++) {
									byteRet[i] = (byte) ((fbit >> 8 * i) & 0xff);
								}

								value[valueNum + 8] = byteRet[0];
								value[valueNum + 9] = byteRet[1];
								value[valueNum + 10] = byteRet[2];
								value[valueNum + 11] = byteRet[3];
								value[valueNum + 12] = byteRet[4];
								value[valueNum + 13] = byteRet[5];
								value[valueNum + 14] = byteRet[6];
								value[valueNum + 15] = byteRet[7];

								valueNum += 16;

							}
						}
						
						if(outputDbCfgArgs.getDbType().equals("oracle")) {
							feature1.setAttribute(fieldNames[0].toUpperCase(), value);
						}else {
							feature1.setAttribute(fieldNames[0], value);
						}

					}
				}
				for (Map.Entry<String, Object> map : fieldToTypeMap.entrySet()) {
					if(outputDbCfgArgs.getDbType().equals("oracle")) {
						if (inputDataSrcArgs.getFieldName().toUpperCase().indexOf(map.getKey().toUpperCase()) != -1) {
							continue;
						}
						feature1.setAttribute(map.getKey().toUpperCase(), feature.getAttribute(map.getKey()));
					}else {
						if (inputDataSrcArgs.getFieldName().indexOf(map.getKey()) != -1) {
							continue;
						}
						feature1.setAttribute(map.getKey(), feature.getAttribute(map.getKey()));
					}
					
				}
			

				++num;
				if (null != progressListener) {
					progressListener.onProcProgress(dataSrcCfgArgs.getTaskNo(),
							(int) ((float) num / (float) recordNum * 100));
				}
				writer.write();
				System.out.println("正在写入第" + (num) + "条数据");
			}
			System.out.println("写入数据完成，共有" + num + "条数据");

			// fr.close();
			writer.close();
			insertDataArgs.jdbcDs.dispose();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	protected DataStore buildDataStore(boolean bExposePrimaryKey) {
		try {
			DbConfigArgs outputDbCfgArgs = (DbConfigArgs) dataSrcCfgArgs;
			JDBCDataStoreFactory jdbcDataStoreFactory = (JDBCDataStoreFactory) createDataStoreFactory();
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("dbtype", outputDbCfgArgs.getDbType());
			params1.put("host", outputDbCfgArgs.getDbHost());
			params1.put("port", outputDbCfgArgs.getDbPort());
			params1.put("database", outputDbCfgArgs.getDbName());
			params1.put("user", outputDbCfgArgs.getDbUserName());
			params1.put("passwd", outputDbCfgArgs.getDbPwd());
			params1.put("Expose primary keys", bExposePrimaryKey);
//			params1.put("Geometry metadata table", true);
			JDBCDataStore ds = (JDBCDataStore) jdbcDataStoreFactory.createDataStore(params1);
			if(outputDbCfgArgs.getDbType().equals(DbDataSource.MYSQL_DBTYPE)) {
//				CustomMySQLDialect mysql = new CustomMySQLDialect(ds);
//				mysql.setStorageEngine("InnoDB");
//				ds.setSQLDialect(mysql);
				
				MysqlDataSource mysqlDataSoruce = new MysqlDataSource();
				mysqlDataSoruce.dialect(ds);
				
			}else if(outputDbCfgArgs.getDbType().equals(DbDataSource.SQLSERVER_DBTYPE)) {
//				CustomMsSQLDialect mssql = new CustomMsSQLDialect(ds);
//				ds.setSQLDialect(mssql);
				
				MssqlDataSource mssqlDataSource = new MssqlDataSource();
				mssqlDataSource.dialect(ds);
				
			}else if(outputDbCfgArgs.getDbType().equals(DbDataSource.ORACLE_DBTYPE)) {
//				CustomOracleDialect oracle = new CustomOracleDialect(ds);
//				ds.setSQLDialect(oracle);
			}
			return ds;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 创建数据表
	 * 
	 * @param tableName
	 *            表名
	 * @param inputDataSrcType
	 *            要生成的数据库类型
	 * @param fieldToTypeMap
	 *            字段名和字段类型
	 */
	protected InsertDataArgs createTable(DbConfigArgs outputDbCfgArgs, Map<String, Object> fieldToTypeMap)
			throws Exception {
		// 用来存储类型是byte[]的
		Map<String, Object> binaryTypeMap = new HashMap<String, Object>();

		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		// set the table name
		String tableName = outputDbCfgArgs.getTableName();
		b.setName(tableName);
		b.setCRS(DefaultGeographicCRS.WGS84);
		// add some properties
		for (Map.Entry<String, Object> entry : fieldToTypeMap.entrySet()) {
			if (entry.getValue().toString().endsWith("[B")) {
				binaryTypeMap.put(entry.getKey(), (Class<?>) entry.getValue());
			}
				
			b.add(entry.getKey(), (Class<?>) entry.getValue());
		}

		JDBCDataStoreFactory jdbcDataStoreFactory = (JDBCDataStoreFactory) createDataStoreFactory();

		if (outputDbCfgArgs.getDbHost().indexOf("\\") != -1) {
			outputDbCfgArgs.setDbPort(null);
		}

		if (".".equals(outputDbCfgArgs.getDbHost())) {
			outputDbCfgArgs.setDbHost("127.0.0.1");
		}

		// build the type
		final SimpleFeatureType FLAG = b.buildFeatureType();
		SimpleFeatureType schema = FLAG;
		JDBCDataStore ds = null;
		try {
			ds = (JDBCDataStore) buildDataStore(false);
			if (ds != null) {
				System.out.println("系统连接到位于localhost的空间数据库成功！");
			} else {
				throw new Exception("系统连接到位于localhost的空间数据库失败！请检查相关参数！");
			}
			String typeName = schema.getTypeName();
			if (outputDbCfgArgs.getDbType().equals("mysql")) {
				typeName = typeName.toLowerCase();
			}else if(outputDbCfgArgs.getDbType().equals("oracle")) {
				typeName = typeName.toUpperCase();
			}
			// TODO:
			// ContentEntry entry = ds.getEntry(new NameImpl("dbo", typeName));
			// if (entry != null) {
			// ds.removeSchema(typeName);
			// }
			/** 判断表是否存在start */
			Connection connect = ds.getConnection(Transaction.AUTO_COMMIT);
			ResultSet rs = connect.getMetaData().getTables(null, null, typeName, null);
			if (rs.next()) {// 如果存在，则删除表
				ds.removeSchema(typeName);
			}
			/** 判断表是否存在end */

			System.out.println("正在创建数据表");
			ds.createSchema(schema);// 在mysql创建表
			System.out.println("创建数据表成功");

			if (outputDbCfgArgs.getDbType().equals(DbDataSource.SQLSERVER_DBTYPE)) {
				alterTableVarbinary(binaryTypeMap, outputDbCfgArgs.getTableName(),
						ds.getConnection(Transaction.AUTO_COMMIT));
			}

			// Index geometryIndex = new Index();
			// ds.createIndex(index);

			InsertDataArgs insertDataArgs = new InsertDataArgs();
			insertDataArgs.jdbcDs = ds;
			insertDataArgs.schema = schema;
			return insertDataArgs;
		} catch (IOException e) {
			throw e;
		}
	}

	protected Object createDataStoreFactory() {
		// TODO Auto-generated method stub
		// Empty.
		return null;
	}

	protected void alterTableVarbinary(Map<String, Object> binaryTypeMap, String tableName, Connection con) {
	}

	/**
	 * 将数据插入数据表
	 * 
	 * @param tableName
	 */
	protected void insertDatas(DbConfigArgs outputDbCfgArgs, InputDataSource inputDataSrouce, InsertDataArgs insertDataArgs, Object inputData,
			Map<String, Object> fieldToTypeMap) throws Exception {
		int recordNum = (int) inputDataSrouce.getRecordNum();
		// TODO Auto-generated method stub
		try {
			
			String typeName = insertDataArgs.schema.getTypeName();
			if(outputDbCfgArgs.getDbType().equals("oracle")) {
				typeName = typeName.toUpperCase();
			}
			
			// 开始写入数据 schema的TypeName严格区分大小写
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = insertDataArgs.jdbcDs
					.getFeatureWriter(typeName, Transaction.AUTO_COMMIT);

			FeatureReader<SimpleFeatureType, SimpleFeature> fr = (FeatureReader<SimpleFeatureType, SimpleFeature>) inputData;
			System.out.println("开始写入数据");
			int num = 0;
			while (fr.hasNext()) {
				SimpleFeature feature = fr.next();
//				if(num == 4487) {
//					System.out.println("");
//				}
//				if(feature.getAttribute("LGTD").toString().equals("0.0") || feature.getAttribute("LTTD").toString().equals("0.0")) {
//					continue;
//				} 
				
				writer.hasNext();
				SimpleFeature feature1 = writer.next();
				feature1.setAttributes(feature.getAttributes());
//				feature1.setAttribute(0, null);
				writer.write();
				++num;
				if (null != progressListener) {
					progressListener.onProcProgress(dataSrcCfgArgs.getTaskNo(),
							(int) ((float) num / (float) recordNum * 100));
				}
				System.out.println("正在写入第" + (num) + "条数据");
			}
			System.out.println("写入数据完成，共有" + num + "条数据");

			// FeatureIterator<SimpleFeature> featuresIt =
			// (FeatureIterator<SimpleFeature>) inputData;
			// while (featuresIt.hasNext()) {
			// SimpleFeature feature = featuresIt.next();
			// writer.hasNext();
			// SimpleFeature feature1 = writer.next();
			// feature1.setAttributes(feature.getAttributes());
			// writer.write();
			// }
			writer.close();
			insertDataArgs.jdbcDs.dispose();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<String> queryDataBase(DataSourceBase args) {
		return null;
	}

	@Override
	public List<String> getDataTableName(DataSourceBase args) {
		return null;
	}

	@Override
	public boolean validConnection(DataSourceBase args) {
		return false;
	}

	public DbUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DbUtils dbUtils) {
		this.dbUtils = dbUtils;
	}

}
