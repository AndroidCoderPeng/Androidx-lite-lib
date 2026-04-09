package com.pengxh.androidx.lite.kit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class UriKit {
    public static String getRealFilePath(Context context, Uri uri) {
        String path = "";
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {
            path = uri.getPath();
        } else if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                throw new IllegalStateException("Cursor is null");
            }

            if (cursor.moveToFirst()) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String displayName;
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex);
                    } else {
                        // 尝试MediaStore方式获取
                        String[] projection = new String[]{MediaStore.Images.Media.DISPLAY_NAME};
                        Cursor mediaCursor = contentResolver.query(uri, projection, null, null, null);
                        if (mediaCursor == null) {
                            throw new IllegalStateException("Media cursor is null");
                        }
                        if (mediaCursor.moveToFirst()) {
                            int idx = mediaCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                            displayName = mediaCursor.getString(idx);
                        } else {
                            displayName = "temp_file_" + System.currentTimeMillis();
                        }
                        mediaCursor.close();
                    }

                    InputStream inputStream = contentResolver.openInputStream(uri);
                    if (inputStream != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            String uniqueFileName = System.currentTimeMillis() + "_" + displayName;
                            File cache = new File(context.getCacheDir(), uniqueFileName);
                            FileOutputStream fos = new FileOutputStream(cache);
                            try {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, bytesRead);
                                }
                            } finally {
                                fos.close();
                                inputStream.close();
                            }
                            path = cache.getAbsolutePath();
                        } else {
                            // 处理 Android 10 以下的情况
                            String[] projection = new String[]{MediaStore.Images.Media.DATA};
                            Cursor dataCursor = contentResolver.query(uri, projection, null, null, null);
                            if (dataCursor == null) {
                                throw new IllegalStateException("Data cursor is null");
                            }
                            int dataIndex = dataCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            if (dataCursor.moveToFirst()) {
                                path = dataCursor.getString(dataIndex);
                            }
                            dataCursor.close();
                        }
                    } else {
                        throw new IllegalStateException("InputStream is null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }
        return path;
    }
}
