package com.project.backend.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public interface BaseData {

    //把this.class的对象转换为clazz类（视图类）的对象
    default <V> V asViewObject(Class<V> clazz){
        try{
            Field[] declaredFields = clazz.getDeclaredFields();
            Constructor<V> constructor=clazz.getConstructor();
            V v=constructor.newInstance();
            for (Field field:declaredFields)
                convert(field,v);
            return v;
        }catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    default <V> V asViewObject(Class<V> clazz , Consumer<V> consumer){
        V v=this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }
    private void convert(Field field,Object vo){
        try{
            //this.class的同名field，如果存在
            Field source=this.getClass().getDeclaredField(field.getName());
            source.setAccessible(true);
            field.setAccessible(true);
            field.set(vo,source.get(this));
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
    }
}
