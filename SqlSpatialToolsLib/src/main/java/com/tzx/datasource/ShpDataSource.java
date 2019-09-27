package com.tzx.datasource;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;

import com.tzx.datasource.argument.DataSourceArgs;
import com.tzx.datasource.argument.OldDbConfigArgs;
import com.tzx.datasource.argument.VectorFileArgs;
import com.tzx.datasource.inter.InputDataSource;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * shp文件数据源
 * 
 * @author Administrator
 *
 */
public class ShpDataSource extends DataSourceBase {

	private class InsertDataArgs {
		public JDBCDataStore jdbcDs;
		public SimpleFeatureType schema;
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
		System.out.println("正在获取shp文件的字段和属性");
		Map<String, Object> fieldToTypeMap = null;
		DataStore ds = buildDataStore();
		ShapefileDataStore shpDs = null;
		if (ds == null) {
			return fieldToTypeMap;
		}

		fieldToTypeMap = new LinkedHashMap<String, Object>();
		try {
			shpDs = (ShapefileDataStore) ds;
//			shpDs.setCharset(Charset.forName("GBK"));
			List<AttributeDescriptor> attrList = shpDs.getFeatureSource().getSchema().getAttributeDescriptors();
			for (AttributeDescriptor attr : attrList) {
//				if(attr.getLocalName().equals("the_geom")) {
//					fieldToTypeMap.put(attr.getLocalName(), Polygon.class);
//					continue;
//				}
				fieldToTypeMap.put(attr.getLocalName(), attr.getType().getBinding());
			}
			System.out.println("shp文件的字段和属性获取成功");
			return fieldToTypeMap;
		} catch (IOException e) {
			System.out.println("shp文件的字段和属性获取失败");
			e.printStackTrace();
		} finally {
			if (shpDs != null) {
				shpDs.dispose();
			}
			ds.dispose();
		}

		return fieldToTypeMap;
	}

	@Override
	public Object getVectorData() {
		// TODO Auto-generated method stub
		ShapefileDataStore shpDataStore = null;
		try {
			VectorFileArgs vectorFileArgs = (VectorFileArgs) dataSrcCfgArgs;
			File fin = new File(vectorFileArgs.getVectorFilePath());
			shpDataStore = new ShapefileDataStore(fin.toURI().toURL());
			shpDataStore.setCharset(Charset.forName(vectorFileArgs.getCharset()));
			String typeName = fin.getName().split(".shp")[0];
			Map<String, Object> fieldToTypeMap = vectorFileArgs.getFieldToTypeMap();
			String[] fields = new String[fieldToTypeMap.size()];
			int index = 0;
			for (Map.Entry<String, Object> entry : fieldToTypeMap.entrySet()) {
				fields[index++] = entry.getKey();
			}
			final Filter filter = Filter.INCLUDE;// ECQL.toFilter("NAME=610116");
			final Query query = new Query(typeName, filter, fields);
			FeatureReader<SimpleFeatureType, SimpleFeature> fr = shpDataStore.getFeatureReader(query,
					Transaction.AUTO_COMMIT);
			return fr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (shpDataStore != null) {
				shpDataStore.dispose();
			}
		}

		return null;
	}

	@Override
	public long getRecordNum() {
		// TODO Auto-generated method stub
		DataStore ds = buildDataStore();
		if (ds == null) {
			return 0;
		}
		VectorFileArgs vectorFileArgs = (VectorFileArgs) dataSrcCfgArgs;
		final Filter filter = Filter.INCLUDE;// ECQL.toFilter("NAME=610116");
		File fin = new File(vectorFileArgs.getVectorFilePath());
		String typeName = fin.getName().split(".shp")[0];
		final Query query = new Query(typeName, filter);
		ShapefileDataStore shpDataStore = (ShapefileDataStore) ds;
		long recordNum = 0;
		try {
			recordNum = shpDataStore.getCount(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recordNum;
	}

	@Override
	public boolean exportData(InputDataSource inputDataSrouce) {
		try {
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

			// 3. 创建shp文件.
			System.out.println("正在生成shp文件");
			VectorFileArgs vectorFileArgs = (VectorFileArgs) dataSrcCfgArgs;
			SimpleFeatureTypeBuilder schemaBuilder = new SimpleFeatureTypeBuilder();

			File fin = new File(vectorFileArgs.getVectorFilePath());
			String typeName = fin.getName().split(".shp")[0];
			schemaBuilder.setName(typeName);
			// add some properties
			for (Map.Entry<String, Object> entry : fieldToTypeMap.entrySet()) {
				if (entry.getKey().equals("the_geom")) {
//					if (entry.getKey().equals("geo")) {
					schemaBuilder.add(entry.getKey(), MultiPoint.class);
					continue;
				}
				schemaBuilder.add(entry.getKey(), (Class<?>) entry.getValue());
			}
			SimpleFeatureType schema = schemaBuilder.buildFeatureType();

			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

			Map<String, Serializable> params = new HashMap<String, Serializable>();

			params.put("url", fin.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			ShapefileDataStore store = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			store.setCharset(Charset.forName("GBK"));
			store.createSchema(schema);
			store.forceSchemaCRS(DefaultGeographicCRS.WGS84);
			System.out.println("shp文件生成成功");

			// 4. 往shp文件里写数据.
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = store.getFeatureWriter(Transaction.AUTO_COMMIT);

			FeatureReader<SimpleFeatureType, SimpleFeature> fr = (FeatureReader<SimpleFeatureType, SimpleFeature>) inputData;
			System.out.println("正在向shp文件中写数据");
			int num = 0;
			while (fr.hasNext()) {
				SimpleFeature feature = fr.next();
				
				if(feature.getAttribute("the_geom") == null) {
//					if(feature.getAttribute("geo") == null) {
					continue;
				}
				writer.hasNext();
				SimpleFeature feature1 = writer.next();
//				feature1.setAttributes(feature.getAttributes());
				for (Map.Entry<String, Object> map : fieldToTypeMap.entrySet()) {
					if (map.getKey().equals("fid") || map.getKey().equals("FID")) {
						continue;
					}
					if (feature.getAttribute(map.getKey()) != null) {
//						if (feature1.getProperty(map.getKey().toLowerCase()) == null) {
//							continue;
//						}
						if(map.getKey().equals("geo")) {
							feature1.setAttribute("the_geom", feature.getAttribute(map.getKey()));
							continue;
						}
						feature1.setAttribute(map.getKey(), feature.getAttribute(map.getKey()));
					}
				}

				writer.write();
				++num;
				if (null != progressListener) {
					progressListener.onProcProgress(dataSrcCfgArgs.getTaskNo(),
							(int) ((float) num / (float) recordNum * 100));
				}
				System.out.println("正在输入第" + num + "条数据");
			}
			System.out.println("数据输入完成，一共有" + num + "条数据");

			writer.close();
		//	fr.close();
//			store.dispose();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean exportOldData(InputDataSource inputDataSrouce) {
		// TODO
		try {
			// 1. 数据源类型检测.
			DataSourceBase inputDataSrcBase = (DataSourceBase) inputDataSrouce;
			OldDbConfigArgs inputDataSrcArgs = (OldDbConfigArgs) inputDataSrcBase.getDataSrcCfgArgs();
			if (inputDataSrcArgs.getDataSrcType() == dataSrcCfgArgs.getDataSrcType()) {
				return false;
			}

			// 2. 获取矢量数据和字段信息.
			Map<String, Object> fieldToTypeMap = inputDataSrcArgs.getFieldToTypeMap();
			Object inputData = inputDataSrouce.getVectorData();
			int recordNum = (int) inputDataSrouce.getRecordNum();

			// 3. 创建shp文件.
			System.out.println("正在生成shp文件");
			VectorFileArgs vectorFileArgs = (VectorFileArgs) dataSrcCfgArgs;
			SimpleFeatureTypeBuilder schemaBuilder = new SimpleFeatureTypeBuilder();

			File fin = new File(vectorFileArgs.getVectorFilePath());

			String typeName = fin.getName().split(".shp")[0];
			schemaBuilder.setName(typeName);

			// 获取要生成的shp类型
			Iterator<Map.Entry<String, Object>> it = fieldToTypeMap.entrySet().iterator();

			switch (inputDataSrcArgs.getType()) {
			case "Point":
				// 设置shp文件的字段名和类型
				schemaBuilder.add("the_geom", Point.class);
				break;
			case "Line":
				schemaBuilder.add("the_geom", MultiLineString.class);

				break;
			case "Polygon":
				// 设置shp文件的字段名和类型
				schemaBuilder.add("the_geom", Polygon.class);

				break;
			}
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				
				  if (entry.getKey().equals("the_geom") ){ 
					   it.remove();//使用迭代器的remove()方法删除元素 
					   continue; 
				  }
				 
				schemaBuilder.add(entry.getKey(), (Class<?>) entry.getValue());
			}

			SimpleFeatureType schema = schemaBuilder.buildFeatureType();

			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

			Map<String, Serializable> params = new HashMap<String, Serializable>();

			params.put("url", fin.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);

			ShapefileDataStore store = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			store.setCharset(Charset.forName("GBK"));
			// store.removeSchema(schema.getTypeName().toLowerCase());
			store.createSchema(schema);
			store.forceSchemaCRS(DefaultGeographicCRS.WGS84);

			System.out.println("shp文件生成成功");

			// 4. 往shp文件里写数据.
			// FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
			// store.getFeatureWriter(Transaction.AUTO_COMMIT);
			String typeName1 = store.getTypeNames()[0];
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = store.getFeatureWriterAppend(typeName1,
					Transaction.AUTO_COMMIT);
			if(inputData == null) {
				return false;
			}
			FeatureReader<SimpleFeatureType, SimpleFeature> fr = (FeatureReader<SimpleFeatureType, SimpleFeature>) inputData;
			
			System.out.println("正在向shp文件中写数据");
			int num = 0;
			String fieldName = "";
			while (fr.hasNext()) {
				SimpleFeature feature = fr.next();
				
				String[] tmfieldNames = inputDataSrcArgs.getFieldName().split(",");
				if(feature.getAttribute(tmfieldNames[0]) == null || feature.getAttribute(tmfieldNames[1]) == null) {
					continue;
				}
				writer.hasNext();
				SimpleFeature feature1 = writer.next();
				switch (inputDataSrcArgs.getType()) {
				case "Point":
					String[] fieldNames = inputDataSrcArgs.getFieldName().split(",");
					
					feature1.setDefaultGeometry(new GeometryFactory()
							.createPoint(new Coordinate(Double.valueOf(String.valueOf(feature.getAttribute(fieldNames[0])) ),
									Double.valueOf(String.valueOf(feature.getAttribute(fieldNames[1]))))));
//					feature1.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate((Double)feature.getAttribute(fieldNames[0]), (Double)feature.getAttribute(fieldNames[1]))));
					break;

				case "Line":
					fieldName = inputDataSrcArgs.getFieldName();
					if (inputDataSrcArgs.getDbType().equals(DbDataSource.ORACLE_DBTYPE)) {
						fieldName = inputDataSrcArgs.getFieldName().toUpperCase();
					}
					byte[] ver = (byte[]) feature.getAttribute(fieldName);
					if (ver == null) {
						break;
					}
					Coordinate cos[] = null;
					LineString ling[] = null;
					int ptNum = 0;
					GeometryFactory gtryFactory = new GeometryFactory();
					if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
						ptNum = ver.length / 4;
						cos = new Coordinate[ptNum / 2];

						float tmpVertex[] = new float[ptNum];
						int index = 0;
						for (int i = 0; i < ver.length; i += 4) {
							int value = 0;
							int j = i;
							value = ver[j + 0];
							value &= 0xff;
							value |= ((long) ver[j + 1] << 8);
							value &= 0xffff;
							value |= ((long) ver[j + 2] << 16);
							value &= 0xffffff;
							value |= ((long) ver[j + 3] << 24);
							tmpVertex[index++] = Float.intBitsToFloat(value);
						}

						index = 0;
						for (int i = 0; i < ptNum; i += 2) {
							cos[index++] = new Coordinate(tmpVertex[i], tmpVertex[i + 1]);
						}

//						ling = new LineString[cos.length];
//						for (int i = 0; i < cos.length; i++) {
//							ling[i] = gtryFactory.createLineString(cos);
//						}
//						MultiLineString multiLineString = gtryFactory.createMultiLineString(ling);
						LineString lineString = gtryFactory.createLineString(cos);
//						feature1.setAttribute("the_geom", multiLineString);
						feature1.setDefaultGeometry(lineString);

					} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
						ptNum = ver.length / 8;
						cos = new Coordinate[ptNum / 2];
						double tmpVertex[] = new double[ptNum];
						int index = 0;
						for (int i = 0; i < ver.length; i += 8) {
							long value = 0;
							for (int j = i; j < i + 8; j++) {
								value |= ((long) (ver[j] & 0xff)) << (8 * j);
							}
							tmpVertex[index++] = Double.longBitsToDouble(value);
						}

						index = 0;
						for (int i = 0; i < ptNum; i += 2) {
							cos[index++] = new Coordinate(tmpVertex[i], tmpVertex[i + 1]);
						}
//						ling = new LineString[cos.length];
//						for (int i = 0; i < cos.length; i++) {
//							ling[i] = gtryFactory.createLineString(cos);
//						}
//						MultiLineString multiLineString = gtryFactory.createMultiLineString(ling);
						LineString lineString = gtryFactory.createLineString(cos);
//						feature1.setAttribute("the_geom", multiLineString);
						feature1.setDefaultGeometry(lineString);
					}
					break;

				case "Polygon":
					fieldName = inputDataSrcArgs.getFieldName();
					if (inputDataSrcArgs.getDbType().equals(DbDataSource.ORACLE_DBTYPE)) {
						fieldName = inputDataSrcArgs.getFieldName().toUpperCase();
					}
					byte[] vertex = (byte[]) feature.getAttribute(fieldName);
					if (vertex == null) {
						break;
					}
					Coordinate coords[] = null;
					int pointNum = 0;
					GeometryFactory geometryFactory = new GeometryFactory();
					if (inputDataSrcArgs.getBinaryDataType().equals("float")) {
						pointNum = vertex.length / 4;
						coords = new Coordinate[pointNum / 2];
						float tmpVertex[] = new float[pointNum];
						int index = 0;
						for (int i = 0; i < vertex.length; i += 4) {
							int value = 0;
							int j = i;
							value = vertex[j + 0];
							value &= 0xff;
							value |= ((long) vertex[j + 1] << 8);
							value &= 0xffff;
							value |= ((long) vertex[j + 2] << 16);
							value &= 0xffffff;
							value |= ((long) vertex[j + 3] << 24);
							tmpVertex[index++] = Float.intBitsToFloat(value);
						}

						index = 0;
						for (int i = 0; i < pointNum; i += 2) {
							coords[index++] = new Coordinate(tmpVertex[i], tmpVertex[i + 1]);
						}
						LinearRing ring = geometryFactory.createLinearRing(coords);
						Polygon polygon = geometryFactory.createPolygon(ring);
//						feature1.setAttribute("the_geom", polygon);
						feature1.setDefaultGeometry(polygon);

					} else if (inputDataSrcArgs.getBinaryDataType().equals("double")) {
						pointNum = vertex.length / 8;
						coords = new Coordinate[pointNum / 2];
						double tmpVertex[] = new double[pointNum];
						int index = 0;
						for (int i = 0; i < vertex.length; i += 8) {
							long value = 0;
							for (int j = i; j < i + 8; j++) {
								value |= ((long) (vertex[j] & 0xff)) << (8 * j);
							}
							tmpVertex[index++] = Double.longBitsToDouble(value);
						}

						index = 0;
						for (int i = 0; i < pointNum; i += 2) {
							coords[index++] = new Coordinate(tmpVertex[i], tmpVertex[i + 1]);
						}
						LinearRing ring = geometryFactory.createLinearRing(coords);
						Polygon polygon = geometryFactory.createPolygon(ring);

						// feature1.setDefaultGeometry(multiPolygon1);

//						feature1.setAttribute("the_geom", polygon);
						feature1.setDefaultGeometry(polygon);
//						System.out.println(feature1.getDefaultGeometry());
						// feature1.setAttribute("the_geom", new
						// GeometryFactory().createPolygon((CoordinateSequence)feature.getAttribute(configArgs.getFieldName())));
					}
					break;
				}
				for (Map.Entry<String, Object> map : fieldToTypeMap.entrySet()) {
//					if(map.getKey().equals("the_geom") || map.getKey().equals("THE_GEOM")) {
//						continue;
//					}
					if (map.getKey().toLowerCase().equals(inputDataSrcArgs.getFieldName().toLowerCase())) {
						continue;
					}

					if (feature.getAttribute(map.getKey()) != null) {
						if (feature1.getProperty(map.getKey()) == null) {
							continue;
						}
						feature1.setAttribute(map.getKey(), feature.getAttribute(map.getKey()));
					}

				}

				writer.write();
				++num;
				if (null != progressListener) {
					progressListener.onProcProgress(dataSrcCfgArgs.getTaskNo(),
							(int) ((float) num / (float) recordNum * 100));
				}
				System.out.println("正在输入第" + (num) + "条数据");
			}
			System.out.println("数据输入完成，一共有" + num + "条数据");

			writer.close();
			fr.close();
			store.dispose();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	protected DataStore buildDataStore() {
		ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
		try {
			VectorFileArgs vectorFileArgs = (VectorFileArgs) dataSrcCfgArgs;
			File vectorFile = new File(vectorFileArgs.getVectorFilePath());
			if (!vectorFile.exists()) {
				System.out.println("文件不存在");
				return null;
			}
			ShapefileDataStore dataStore = (ShapefileDataStore) factory.createDataStore(vectorFile.toURI().toURL());
			if (dataStore != null) {
				dataStore.setCharset(Charset.forName("GBK"));
			}
			return dataStore;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
