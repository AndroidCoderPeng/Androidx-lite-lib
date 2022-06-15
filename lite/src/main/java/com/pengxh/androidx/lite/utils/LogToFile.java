package com.pengxh.androidx.lite.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LogToFile {
    /**
     * @param log 待写入的内容
     */
    public static void write(File file, String log) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(log);
            writer.newLine(); //换行
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(File file) {
        StringBuilder builder;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}