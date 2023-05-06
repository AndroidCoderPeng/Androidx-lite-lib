package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.DialogHub;


/**
 * 取消/确定对话框
 */
public class AlertControlDialog extends Dialog {

    private final String title;
    private final String message;
    private final String positiveBtn;
    private final String negativeBtn;
    private final OnDialogButtonClickListener listener;

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveBtn;
        private String negativeBtn;
        private OnDialogButtonClickListener listener;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(String name) {
            this.positiveBtn = name;
            return this;
        }

        public Builder setNegativeButton(String name) {
            this.negativeBtn = name;
            return this;
        }

        public Builder setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public AlertControlDialog build() {
            return new AlertControlDialog(this);
        }
    }

    private AlertControlDialog(Builder builder) {
        super(builder.context, R.style.UserDefinedDialogStyle);
        this.title = builder.title;
        this.message = builder.message;
        this.positiveBtn = builder.positiveBtn;
        this.negativeBtn = builder.negativeBtn;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogHub.resetParams(this, 0.8);
        setContentView(R.layout.dialog_alert);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        TextView dialogTitleView = findViewById(R.id.dialogTitleView);
        TextView dialogMessageView = findViewById(R.id.dialogMessageView);
        Button dialogCancelButton = findViewById(R.id.dialogCancelButton);
        Button dialogConfirmButton = findViewById(R.id.dialogConfirmButton);

        if (!TextUtils.isEmpty(title)) {
            dialogTitleView.setText(title);
        }

        if (!TextUtils.isEmpty(message)) {
            dialogMessageView.setText(message);
        }

        if (!TextUtils.isEmpty(negativeBtn)) {
            dialogCancelButton.setText(negativeBtn);
        }
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCancelClick();
                    dismiss();
                }
            }
        });

        if (!TextUtils.isEmpty(positiveBtn)) {
            dialogConfirmButton.setText(positiveBtn);
        }
        dialogConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onConfirmClick();
                    dismiss();
                }
            }
        });
    }

    public interface OnDialogButtonClickListener {
        void onConfirmClick();

        void onCancelClick();
    }
}
