package com.pengxh.androidx.lite.kit;

public class ByteArrayKit {
    public static String toHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int i = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[i >>> 4];
            hexChars[j * 2 + 1] = hexArray[i & 0x0F];
        }
        return new String(hexChars);
    }

    public static String toAsciiCode(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append((char) aByte);
        }
        return builder.toString();
    }
}
