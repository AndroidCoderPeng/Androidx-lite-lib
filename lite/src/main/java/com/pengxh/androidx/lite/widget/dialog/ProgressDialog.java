package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.DialogKit;

import java.util.Locale;

public class ProgressDialog extends Dialog {

    private CircularProgressIndicator progressBar;
    private TextView progressText;

    private ProgressDialog(Context context) {
        super(context, R.style.UserDefinedDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogKit.resetParams(this, 0.5);
        setContentView(R.layout.dialog_progress);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        progressText = findViewById(R.id.progressText);
        progressText.setText("0 %");
    }

    public void setMaxProgress(long maxProgress) {
        progressBar.setMax((int) maxProgress);
    }

    private int getMaxProgress() {
        return progressBar.getMax();
    }

    public void updateProgress(long progress) {
        progressBar.setProgress((int) progress);

        float percent = ((float) progress / getMaxProgress()) * 100;
        progressText.setText(String.format(Locale.getDefault(), "%.2f %%", percent));
    }
}
