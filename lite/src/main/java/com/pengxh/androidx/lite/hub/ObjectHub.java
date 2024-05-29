package com.pengxh.androidx.lite.hub;

import com.google.gson.Gson;

public class ObjectHub {
    public static String getJson(Object o) {
        if (o == null) {
            return "";
        }
        return new Gson().toJson(o);
    }
}
