package com.pengxh.androidx.lib.util;

import org.json.JSONException;
import org.json.JSONObject;

public class StringHelper {
    public static int separateResponseCode(String value) {
        if (value.isEmpty()) {
            return 404;
        }
        int code = 500;
        try {
            code = new JSONObject(value).getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }
}
