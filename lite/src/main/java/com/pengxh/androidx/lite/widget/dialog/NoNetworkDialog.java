package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.DialogKit;

public class NoNetworkDialog extends Dialog {

    private final OnDialogButtonClickListener listener;

    public static class Builder {
        private Context context;
        private OnDialogButtonClickListener listener;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public NoNetworkDialog build() {
            return new NoNetworkDialog(this);
        }
    }

    private NoNetworkDialog(Builder builder) {
        super(builder.context, R.style.UserDefinedDialogStyle);
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogKit.resetParams(this, Gravity.CENTER, R.style.UserDefinedAnimation, 0.85f);
        setContentView(R.layout.dialog_no_network);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        ImageView dismissView = findViewById(R.id.dismissView);
        dismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button dialogButton = findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClick();
                dismiss();
            }
        });
    }

    public interface OnDialogButtonClickListener {
        void onButtonClick();
    }
}
