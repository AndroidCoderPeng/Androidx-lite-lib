package com.pengxh.androidx.lite.kit;

public class ByteArrayKit {
    public static String toAsciiCode(byte[] bytes) {
        //判断是否能被2整除
        if (bytes.length % 2 == 0) {
            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append((char) aByte);
            }
            return builder.toString();
        } else {
            return "Decode Error";
        }
    }
}
