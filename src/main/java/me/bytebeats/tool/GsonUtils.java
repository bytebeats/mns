package me.bytebeats.tool;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author <a href="https://github.com/bytebeats">bytebeats</a>
 * @email <happychinapc@gmail.com>
 * @since 2020/8/25 10:58
 */
public class GsonUtils {
    private static Gson gson = new Gson();

    /**
     * 私有化构造方法
     */
    private GsonUtils() {
    }

    /**
     * 获取单例方法
     */
    private static final Gson getInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String toJson(Object o) {
        return getInstance().toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return getInstance().fromJson(json, tClass);
    }

    public static <T> T fromJson(String json, Type type) {
        return getInstance().fromJson(json, type);
    }

}
