package com.pengxh.androidx.lite.kit;

import com.google.gson.Gson;

public class ObjectKit {
    public static String getJson(Object o) {
        if (o == null) {
            return "";
        }
        return new Gson().toJson(o);
    }
}
