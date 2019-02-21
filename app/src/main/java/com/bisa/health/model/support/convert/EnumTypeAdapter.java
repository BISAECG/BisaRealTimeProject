package com.bisa.health.model.support.convert;

import android.util.Log;

import com.bisa.health.model.IEnum;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/8/10.
 */

public class EnumTypeAdapter<T> extends TypeAdapter<T> {
    @Override
    public T read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL){

            in.nextNull();
            return null;
        }
        return null;
    }

    public Object read(JsonReader in,TypeToken<T> type) throws IOException {
        boolean isEnum = type.getRawType().isEnum();
        if(isEnum){
            int value = in.nextInt();
            try {
                Method valuesMethod = type.getRawType().getMethod("values", Object.class);
                IEnum[] enumArr = (IEnum[])valuesMethod.invoke(type.getClass(), Object[].class);
                for (IEnum iEnum : enumArr) {
                    if(iEnum.getValue() == value){
                        Log.i("This is a enum ", "value is=====>"+value);
                        return iEnum;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if(value == null){
            out.nullValue();
            return;
        }

        if(value instanceof IEnum){
            out.value(((IEnum)value).getValue());
        }
    }
}
