import com.google.gson.Gson;

public class GsonTest1 {
	public static void main(String[] args) {
//		String jsonData = "{'name':'John', 'age':20}";
//		Person person = GsonUtil.parseJsonWithGson(jsonData, Person.class);
//		System.out.println(person.getName() + "," + person.getAge());
		Gson gs = new Gson();
		Person person = new Person();
		person.setName("小王");
		person.setAge("小李");
		String objectStr = gs.toJson(person);//把对象转为JSON格式的字符串
        System.out.println("把对象转为JSON格式的字符串///  "+objectStr);
	}
}

/*
 * 封装的GSON解析工具类，提供泛型参数
 */
class GsonUtil {
	// 将Json数据解析成相应的映射对象
	public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
		Gson gson = new Gson();
		T result = gson.fromJson(jsonData, type);
		return result;
	}

}

class Person {
	private String name;
	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}
