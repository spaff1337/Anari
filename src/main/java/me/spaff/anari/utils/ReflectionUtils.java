package me.spaff.anari.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getField(Class<?> clazz, String fieldName, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            return value;
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
    }
}

