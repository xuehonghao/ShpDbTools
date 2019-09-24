package test;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
 
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.DataFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
 
 
//使用geotools2.7.2
public class WriteData {
	
    @Test
    public void write2() {  
        try{    
            //定义属性  
            final SimpleFeatureType TYPE = DataUtilities.createType("Location",  
                "geometry:Point:srid=4326," + // <- the geometry attribute: Point type  
                "POIID:String," + // <- a String attribute  
                "MESHID:String," + // a number attribute  
                "OWNER:String"  
            );  
//            SimpleFeatureCollection collection = FeatureCollections.newCollection();  
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = new GeometryFactory();  
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);  
      
            double latitude = Double.parseDouble("116.123456789");  
            double longitude = Double.parseDouble("39.120001");  
            String POIID = "2050003092";  
            String MESHID = "0";  
            String OWNER = "340881";  
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));  
            Object[] obj = {point, POIID, MESHID, OWNER};  
            SimpleFeature feature = featureBuilder.buildFeature(null, obj);  
            collection.add(feature);  
            //feature = featureBuilder.buildFeature(null, obj);  
            //collection.add(feature);  
            File newFile = new File("D:/newPoi.shp");  
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();  
            Map<String, Serializable> params = new HashMap<String, Serializable>();  
            params.put("url", newFile.toURI().toURL());  
            params.put("create spatial index", Boolean.TRUE);  
            ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);  
            newDataStore.createSchema(TYPE);  
            newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);  
      
            Transaction transaction = new DefaultTransaction("create");  
            String typeName = newDataStore.getTypeNames()[0];  
            SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);  
      
            if (featureSource instanceof SimpleFeatureStore) {  
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;  
                featureStore.setTransaction(transaction);  
                try {  
                    featureStore.addFeatures(collection);  
                    transaction.commit();  
                } catch (Exception problem) {  
                    problem.printStackTrace();  
                transaction.rollback();  
                } finally {  
                    transaction.close();  
                }  
            } else {  
                System.out.println(typeName + " does not support read/write access");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    
    public static void writeSHP(String path, Geometry geometry,String desc) throws Exception {
    	 
		// 1.创建shape文件对象
		File file = new File(path);
		ShapefileDataStore ds = new ShapefileDataStore(file.toURI().toURL());
		ds.setCharset(Charset.forName("GBK"));
 
		if (!file.exists()) {
			//如果文件不存在，创建schema，存在的话，就不创建了，防止覆盖
			SimpleFeatureTypeBuilder tBuilder = new SimpleFeatureTypeBuilder();
			// 5.设置
			// WGS84:一个二维地理坐标参考系统，使用WGS84数据
			tBuilder.setCRS(DefaultGeographicCRS.WGS84);
			tBuilder.setName("shapefile");	
			// 6.置该shape文件几何类型
			tBuilder.add( "the_geom", MultiPolygon.class );
			// 7.添加一个id
			tBuilder.add("osm_id", Long.class);
			// 8.添加名称
			tBuilder.add("name", String.class);
			// 9.添加描述
			tBuilder.add("des", String.class);			
			SimpleFeatureType buildFeatureType = tBuilder.buildFeatureType();
			// 10.设置此数据存储的特征类型
			ds.createSchema(buildFeatureType);		
		}
 
		// 11.设置编码
		ds.setCharset(Charset.forName("UTF-8"));
 
		// 12.设置writer
		// 为给定的类型名称创建一个特性写入器
		String typeName = ds.getTypeNames()[0];
		FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriterAppend(typeName,
				Transaction.AUTO_COMMIT);
 
		// Interface SimpleFeature：一个由固定列表值以已知顺序组成的SimpleFeatureType实例。		
		// 13.写一个特征
		SimpleFeature feature = writer.next();
	   
		feature.setAttribute("the_geom", geometry);	
		/**
		 * 下面的属性值，外面可以当做一个实体对象传进来，不写死！
		 */
		feature.setAttribute("osm_id", 1234567890l);
		feature.setAttribute("name", "建筑物");
		feature.setAttribute("des", desc);
 
		// 14.写入
		writer.write();
 
		// 15.关闭
		writer.close();
 
		// 16.释放资源
		ds.dispose();
 
		// 17.读取shapefile文件的图形信息
		ShpFiles shpFiles = new ShpFiles(path);
		/*
		 * ShapefileReader( ShpFiles shapefileFiles, boolean strict,
		 * --是否是严格的、精确的 boolean useMemoryMapped,--是否使用内存映射 GeometryFactory gf,
		 * --几何图形工厂 boolean onlyRandomAccess--是否只随机存取 )
		 */
		ShapefileReader reader = new ShapefileReader(shpFiles, false, true, new GeometryFactory(), false);
		while (reader.hasNext()) {
			System.err.println(reader.nextRecord().shape());
		}
		reader.close();
    }

    
    public static void main(String[] args) throws Exception {
	    System.out.println("===============创建自己的shp文件==============");
		String MPolygonWKT1 = "MULTIPOLYGON(((121.5837313 31.2435225,121.5852142 31.2444795,121.5860999 31.2434539,121.586133 31.2433016,121.5856866 31.243208,121.5846169 31.2425171,121.5837313 31.2435225)))";
		WKTReader reader = new WKTReader( new GeometryFactory() );    
        MultiPolygon multiPolygon1 = (MultiPolygon) reader.read(MPolygonWKT1); 
		//写入一个多多边形 【建筑物】== 信合花园 
		writeSHP("D:/111i.shp", multiPolygon1,"信合花园");		
		
		String MPolygonWKT2 = "MULTIPOLYGON(((121.5869337 31.2479069,121.5874496 31.248256,121.5877683 31.247914,121.5872516 31.2475652,121.5869337 31.2479069)))";
		WKTReader reader2 = new WKTReader( new GeometryFactory() );    
        MultiPolygon multiPolygon2 = (MultiPolygon) reader2.read(MPolygonWKT2); 
		//再追加写入一个多多边形 【建筑物】== 信合花园 
		writeSHP("D:/222i.shp", multiPolygon2,"新金桥大厦");			
		System.out.println("===============打开shp文件==============");
//		openShpFile();
	}

}