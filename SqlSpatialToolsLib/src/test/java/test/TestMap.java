package test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestMap {

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("1", "111");
		map.put("2", "222");
		map.put("3", "333");
		map.put("4", "444");
		map.put("5", "555");
		map.put("6", "666");
		map.put("7", "777");
		map.put("8", "888");
		/*for (Map.Entry<String, Object> m : map.entrySet()) {
		错误的删除方式	System.out.println(m.getKey() + "   " + m.getValue());
			map.remove("1");
		}*/
		
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Object> entry = it.next();
            if(entry.getKey().equals("2")) {
            	it.remove();//使用迭代器的remove()方法删除元素
            	continue;
            }
            System.out.println(entry.getKey() + "   " + entry.getValue());
        }
        
        
        System.out.println("********************************");
        
        for(Map.Entry<String, Object> ss : map.entrySet()) {
        	System.out.println(ss.getKey() + "     "+ss.getValue());
        }
        

	}

}
