package com.example.charletdemo.util;

import java.lang.reflect.Field;

public class AutoSizeUtils {
    /**
     * 适配小写c开头的Number类型字段
     * @param target
     */
    public static void autoSize(Object target){

        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (field.getName().charAt(0) != 'c'){
                continue;
            }

            field.setAccessible(true);
            Class<?> type = field.getType();
            try {
                if (type == int.class){
                    field.setInt(target, getScaleWidth(field.getInt(target)));
                }
                if (type == float.class){
                    field.setFloat(target, getScaleWidth((int) field.getFloat(target)));
                }
                if (type == double.class){
                    field.setDouble(target, getScaleWidth((int) field.getDouble(target)));
                }
            }catch (Exception e){
                throw new IllegalStateException("适配出错: " + e.getLocalizedMessage());
            }

        }

    }

    public static int getScaleWidth(int width){
        if(width < 2) return width;
        return (int) (width * DeviceInfo.sAutoScaleX);
    }
}
