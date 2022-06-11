package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.widget.DeleteEditText;

/**
 * 输入对话框
 */
public class AlertInputDialog extends Dialog {

    private final String title;
    private final String hint;
    private final String positiveBtn;
    private final String negativeBtn;
    private final OnDialogButtonClickListener listener;

    public static class Builder {
        private Context context;
        private String title;
        private String hint;
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

        public Builder setHintMessage(String hint) {
            this.hint = hint;
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

        public AlertInputDialog build() {
            return new AlertInputDialog(this);
        }
    }

    private AlertInputDialog(Builder builder) {
        super(builder.context, R.style.UserDefinedDialogStyle);
        this.title = builder.title;
        this.hint = builder.hint;
        this.positiveBtn = builder.positiveBtn;
        this.negativeBtn = builder.negativeBtn;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogLayoutParam.resetParams(this, 0.8);
        setContentView(R.layout.dialog_input);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        TextView dialogTitleView = findViewById(R.id.dialogTitleView);
        DeleteEditText dialogInputView = findViewById(R.id.dialogInputView);
        Button dialogCancelButton = findViewById(R.id.dialogCancelButton);
        Button dialogConfirmButton = findViewById(R.id.dialogConfirmButton);

        if (!TextUtils.isEmpty(title)) {
            dialogTitleView.setText(title);
        }

        if (!TextUtils.isEmpty(hint)) {
            dialogInputView.setHint(hint);
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
                    String inputValue = dialogInputView.getText().toString().trim();
                    if (TextUtils.isEmpty(inputValue)) {
                        //TODO 添加Toast
                        return;
                    }
                    listener.onConfirmClick(inputValue);
                    dismiss();
                }
            }
        });
    }

    public interface OnDialogButtonClickListener {
        void onConfirmClick(String value);

        void onCancelClick();
    }
}
