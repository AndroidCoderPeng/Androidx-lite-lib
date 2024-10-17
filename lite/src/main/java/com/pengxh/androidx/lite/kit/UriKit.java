package com.pengxh.androidx.lite.kit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class UriKit {
    public static String getRealFilePath(Context context, Uri uri) {
        String path = "";
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {
            path = new File(Objects.requireNonNull(uri.getPath())).getAbsolutePath();
        } else if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                return path;
            }
            if (cursor.moveToFirst()) {
                try {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String displayName = cursor.getString(columnIndex);
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    if (inputStream != null) {
                        //Android 10需要转移到沙盒
                        File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
                        FileOutputStream fos = new FileOutputStream(cache);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            FileUtils.copy(inputStream, fos);
                            path = cache.getAbsolutePath();
                            fos.close();
                            inputStream.close();
                        }
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
