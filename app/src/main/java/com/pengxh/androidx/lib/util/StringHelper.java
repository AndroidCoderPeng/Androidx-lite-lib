package com.pengxh.androidx.lib.util;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class StringHelper {
    private static final Gson gson = new Gson();

    public static Pair<Integer, String> getResponseHeader(String value) {
        if (value.isEmpty()) {
            return new Pair<>(404, "Invalid Response");
        }
        JsonObject jsonObject = gson.fromJson(value, JsonObject.class);
        int code = jsonObject.get("code").getAsInt();
        String message = jsonObject.get("message").getAsString();
        return new Pair<>(code, message);
    }
}
