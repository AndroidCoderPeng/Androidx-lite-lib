package com.pengxh.androidx.lite.kit;

public class ByteArrayKit {
    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            int i = aByte & 0xFF;
            result.append("0123456789ABCDEF".charAt(i >> 4));
            result.append("0123456789ABCDEF".charAt(i & 0x0F));
        }
        return result.toString();
    }

    public static String toAscIICode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            int charValue = b & 0xFF;
            // 只转换可打印的ASCII字符 (32-126)，其他字符忽略
            if (charValue >= 32 && charValue <= 126) {
                builder.append((char) charValue);
            }
        }
        return builder.toString();
    }
}
