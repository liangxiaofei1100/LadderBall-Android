package com.zhaoyan.ladderball.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

/*
 * 应用所有的偏好管理类
 */
public class SharedPreferencesManager {
    private static final String SP_FILE_NAME = "ladderball_data";

    //添加一个偏好
    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取一个偏好
     *
     * @param context       context
     * @param key           SharedPreferences key
     * @param defaultObject 默认值，注意：类型必须和该偏好的类型一致，
     * @param <T>           偏好类型
     * @return 偏好的值，如果没有记录过，返回defaultObject。
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Context context, String key, T defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,
                Context.MODE_PRIVATE);
        Object value = defaultObject;
        if (defaultObject == null || defaultObject instanceof String) {
            value = sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            value = sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            value = sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            value = sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            value = sp.getLong(key, (Long) defaultObject);
        }
        T result;
        try {
            result = (T) value;
        } catch (Exception e) {
            result = defaultObject;
        }
        return result;
    }

    //删除一个偏好
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    //兼容类，能应用apply方法就用该方法，因为它是异步的
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                Log.w("NoSuchMethodException: " + e);
            }
            return null;
        }

        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                Log.w("Exception" + e);
            }
            editor.commit();
        }
    }
}
