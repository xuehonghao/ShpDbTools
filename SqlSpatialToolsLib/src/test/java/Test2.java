

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * 读取shp文件每个对应字段的值
 * @author Administrator 
 *
 */ 
public class Test2 { 
	public static void main(String[] args) throws Exception { 
		// 弹出一个对话框，选择shp文件
	    File file = JFileDataStoreChooser.showOpenFile("shp", null);
	    if (file == null) {
	        return;
	    }
	    Map<String, Object> map = new HashMap<String, Object>();  
	    map.put("url", file.toURI().toURL());  
	  
	    DataStore dataStore = DataStoreFinder.getDataStore(map);  
	    String typeName = dataStore.getTypeNames()[0];
	    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore  
	               .getFeatureSource(typeName);  

	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();  
	    FeatureIterator<SimpleFeature> features = collection.features();
	    int num = 0;
	    while (features.hasNext()) { 
	    	num++;
            SimpleFeature feature = features.next();  
//            System.out.print(feature.getID());  
//            System.out.print(": ");  
//            System.out.println(feature.getDefaultGeometryProperty().getValue());//此行输出的空间信息的wkt表达形式  
            System.out.println(feature.getAttribute("the_geom") + "/t" + feature.getAttribute("grid_code"));
        }  
	    System.out.println("*********" + num);
	}
	
}
