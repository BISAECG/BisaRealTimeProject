package com.bisa.health.model.support.convert;

import com.bisa.health.model.IEnum;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2018/8/10.
 */

public class MyEnumAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if(rawType.isEnum()){
            Type[] types = rawType.getGenericInterfaces();
            for(Type item:types){
                if(item == IEnum.class){
                    return new EnumTypeAdapter<T>();
                }
            }
        }
        return null;
    }

}
