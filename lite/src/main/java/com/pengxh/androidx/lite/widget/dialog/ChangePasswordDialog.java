package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.utils.StringUtil;
import com.pengxh.androidx.lite.widget.EasyToast;

public class ChangePasswordDialog extends Dialog {

    private final Context context;
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

        public ChangePasswordDialog build() {
            return new ChangePasswordDialog(this);
        }
    }

    private ChangePasswordDialog(Builder builder) {
        super(builder.context, R.style.UserDefinedDialogStyle);
        this.context = builder.context;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogLayoutParam.resetParams(this, 0.85);
        setContentView(R.layout.dialog_change_pwd);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        TextView oldPwdView = findViewById(R.id.oldPwdView);
        TextView newPwdView = findViewById(R.id.newPwdView);
        TextView confirmPwdView = findViewById(R.id.confirmPwdView);
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldPwdView.getText().toString();
                String newPwd = newPwdView.getText().toString();
                String confirmPwd = confirmPwdView.getText().toString();
                if (TextUtils.isEmpty(oldPwd)) {
                    EasyToast.show(context, "请输入原密码");
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    EasyToast.show(context, "请输入新密码");
                    return;
                }
                if (TextUtils.isEmpty(confirmPwd)) {
                    EasyToast.show(context, "请再次确认密码");
                    return;
                }
                if (!StringUtil.isLetterAndDigit(newPwd)) {
                    EasyToast.show(context, "新密码需包含数字和字母");
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    EasyToast.show(context, "新密码和确认密码不一致，请检查");
                    return;
                }
                listener.onConfirmClick(oldPwd, newPwd);
            }
        });
    }

    public interface OnDialogButtonClickListener {
        void onConfirmClick(String oldPwd, String newPwd);
    }
}
