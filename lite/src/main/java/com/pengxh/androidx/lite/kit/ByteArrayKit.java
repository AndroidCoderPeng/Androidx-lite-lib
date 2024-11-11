package com.pengxh.androidx.lite.kit;

public class ByteArrayKit {
    public static String toAsciiCode(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append((char) aByte);
        }
        return builder.toString();
    }
}
