package co.edu.udistrital.communicationapp.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class JSONUtil {

    public static String parseToJson(Object object) {
        return new Gson().toJson(object);
    }

    public static Object parseToObject(String jsonString, Class<?> objectClass) {
        return new Gson().fromJson(jsonString, objectClass);
    }


    public static List<?> parseToList(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

}


