package com.xiao.nettydemo.source;

import com.google.gson.*;
import com.xiao.nettydemo.protocol.Serializer;

import java.lang.reflect.Type;

public class TestGson {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.Algorithm.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }


}
