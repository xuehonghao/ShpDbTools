import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



/**
 * 用GSON解析Json数组
 */
public class GsonTest {
    public static void main(String[] args) {
        // Json数组最外层要加"[]"
        String jsonData = "[{'name':'John', 'grade':[{'course':'English','score':100},{'course':'Math','score':78}]},{'name':'Tom', 'grade':[{'course':'English','score':86},{'course':'Math','score':90}]}]";

//        List<Student1> students = (List<Student1>)GsonUtil1.parseJsonArrayWithGson(jsonData,
//                Student1.class);
        
        Gson gson = new Gson();
        List<Student1> result = gson.fromJson(jsonData, new TypeToken<List<Student1>>() {
        }.getType());
    }
}

/*
 * 封装的GSON解析工具类，提供泛型参数
 */
class GsonUtil1 {
    // 将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    // 将Json数组解析成相应的映射对象列表
    public static <T> List<T> parseJsonArrayWithGson(String jsonData,
            Class<T> type) {
        Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }
}

class Student1 {
    private String name;
    private List<Grade1> grade; // 因为grade是个数组，所以要定义成List

    public class Grade1 {
        private String course;
        private String score;

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Grade1> getGrade() {
        return grade;
    }

    public void setGrade(List<Grade1> grade) {
        this.grade = grade;
    }
}