package message;
import com.alibaba.fastjson.JSONObject;

public class MessageUtil{
    public static <T> T getObjectFromString(String objBody, Class<T> clazz){
        return JSONObject.parseObject(objBody, clazz);
    }

    public static String object2String(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}