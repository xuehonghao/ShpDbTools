

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
  * 将shp文件打开显示图层
 * @author Administrator
 *
 */ 
public class Quickstart3 {
 
	public static void main(String[] args) throws Exception {
		// 弹出一个对话框，选择shp文件
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("url", file.toURI().toURL());
        params.put("create spatial index", false);
        params.put("memory mapped buffer", false);
        params.put("charset", "ISO-8859-1");
        
        DataStore store = DataStoreFinder.getDataStore(params);
        SimpleFeatureSource featureSource = store.getFeatureSource(store.getTypeNames()[0]);

        // 创建一个映射内容，并将shp文件填入
        MapContent map = new MapContent();
        map.setTitle("Quickstart");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);
        

        // 展示地图
        JMapFrame.showMap(map);
	}

}
