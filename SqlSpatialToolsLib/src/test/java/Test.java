

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 读取shp文件的字段名和字段类型
 * @author Administrator
 *
 */ 
public class Test {

	public static void main(String[] args) {
		Test test = new Test();
		test.readSHP();
	}
	/** 
	 * 读取shap格式的文件
	 * 
	 * @param path
	 */
	public void readSHP() {
		ShapefileDataStore shpDataStore = null;
		try {
			File file = JFileDataStoreChooser.showOpenFile("shp", null);
	        if (file == null) {
	            return;
	        }
			shpDataStore = new ShapefileDataStore(file.toURI()
					.toURL());
			shpDataStore.setCharset(Charset.forName("GBK"));
			// 文件名称
			String typeName = shpDataStore.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
			featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore
					.getFeatureSource(typeName);
			FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource
					.getFeatures();
			SimpleFeatureType schema = result.getSchema(); // schema
			List<AttributeDescriptor> columns = schema
					.getAttributeDescriptors();
			FeatureIterator<SimpleFeature> itertor = result.features();
			/*
			 * 或者使用 FeatureReader FeatureReader reader =
			 * DataUtilities.reader(result); while(reader.hasNext()){
			 * SimpleFeature feature = (SimpleFeature) reader.next(); }
			 */
			while (itertor.hasNext()) {
				SimpleFeature feature = itertor.next();
				for (AttributeDescriptor attributeDes : columns) {
					String attributeName = attributeDes.getName().toString();// attribute
					String attributeType = extractMessageByRegular(attributeDes.getType().toString());
					System.out.println(attributeName + "\t" + attributeType);
					System.out.println();
					if (attributeName.equals("the_geom"))
						continue;
					feature.getAttribute(attributeName); // attributeValue
				}
				Geometry g = (Geometry) feature.getDefaultGeometry();// Geometry
				break;
			}
			itertor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 使用正则表达式提取中括号中的内容
	 * @param msg
	 * @return 
	 */
	public static String extractMessageByRegular(String msg){
		String test = "";
		Pattern p = Pattern.compile("<(.*?)>(.*)");
		Matcher m = p.matcher(msg);
		while(m.find()){
			test = m.group(1);
		}
		return test;
	}

}
