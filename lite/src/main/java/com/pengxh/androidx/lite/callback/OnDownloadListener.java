package com.pengxh.androidx.lite.callback;

import java.io.File;

public interface OnDownloadListener {
    void onDownloadStart(long totalBytes);

    void onProgressChanged(long currentBytes);

    void onDownloadEnd(File file);
}
