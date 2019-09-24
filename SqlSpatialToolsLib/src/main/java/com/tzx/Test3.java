package com.tzx;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.tzx.datasource.EDbType;
import com.tzx.datasource.ShpDataSource;
import com.tzx.tool.DbUtils;
import com.tzx.tool.Tools;

public class Test3 {
	public static void main(String[] args) {
		List<String> key = new ArrayList<String>();
		ShpDataSource shp = new ShpDataSource();
		
		DbUtils dbu = new DbUtils(EDbType.MSSQL);
		
		//连接数据库
		Connection con = dbu.getConnection();
		
		try {
			// 弹出一个对话框，选择shp文件
		    File file = JFileDataStoreChooser.showOpenFile("shp", null);
		    if (file == null) {
		        return;
		    }
		    
		    Map<String, Object> map = shp.getVectorPropInfo(file);
		     
		    String tableName = "eeee";
		    Statement stmt = con.createStatement();
		    
		    System.out.println("正在创建数据表");
		    //创建数据表，拼接sql语句
		    StringBuffer query = new StringBuffer("create table " + tableName + " (");
		    Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator(); 
		    
		    while (entries.hasNext()) { 
		      Map.Entry<String, Object> entry = entries.next(); 
		      System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		      query.append(" " + entry.getKey() + " "+Tools.shq2msSql(entry.getValue().toString())+" ,");//TODO 这里需要写数据库和shp的类型映射
		      key.add(entry.getKey());
		    }	
		    query.append(")");
		    
		    stmt.executeUpdate(query.toString());
		    
		    System.out.println("创建数据表成功");
		    
		    
		    System.out.println("开始插入数据");
		    
		    Map<String, Object> map1 = new LinkedHashMap<String, Object>();  
		    map1.put("url", file.toURI().toURL());  
		  
		    DataStore dataStore = DataStoreFinder.getDataStore(map1);  
		    String typeName = dataStore.getTypeNames()[0];
		    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore  
		               .getFeatureSource(typeName);  

		    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();  
		    FeatureIterator<SimpleFeature> features = collection.features();
		    
		    Statement state=con.createStatement();  
//		    PreparedStatement pstmt = null;
//		    key.remove(0);
		    //插入语句的sql
		    StringBuffer sql ;
		    while (features.hasNext()) {
			    sql = new StringBuffer("insert into " + tableName + " ( ");
			    for(int i = 0 ; i < key.size() ; i++) {
			    	sql.append(" "+ key.get(i));
			    	if(i != key.size() - 1) {
			    		sql.append(" , ");
			    	}
			    }
			    sql.append(") values (");
	            SimpleFeature feature = features.next();  
//	            System.out.println(feature.getAttribute("arcid") + "/t" + feature.getAttribute("grid_code"));
	            for(int i = 0 ; i < key.size() ; i++) {
	            	if(key.get(i).equals("the_geom")) {
	            		sql.append("'");
	            	}
	            	sql.append(feature.getAttribute(key.get(i)));
	            	if(key.get(i).equals("the_geom")) {
	            		sql.append("'");
	            	}
//	            	sql.append(" ? ");
	            	if(i != key.size() - 1) {
			    		sql.append(" , ");
			    	}
			    }
	            sql.append(")");
	            state.executeUpdate(sql.toString());
//	            pstmt = (PreparedStatement) con.prepareStatement(sql.toString());
//	            for (int i = 0; i < key.size(); i++) {
//	            	System.out.println(feature.getAttribute(key.get(i)));
//	            	pstmt.setObject(i+1, feature.getAttribute(key.get(i)));
//				}
//	            pstmt.executeUpdate();
	        }  
		    
		    
		    
		    stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	  //释放数据库连接
	  dbu.releaseConnection(con);
	    
	}
}
