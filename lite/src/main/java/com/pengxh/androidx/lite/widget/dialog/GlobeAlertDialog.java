package com.pengxh.androidx.lite.widget.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.ContextHub;

public class GlobeAlertDialog extends DialogFragment {

    private final OnDialogButtonClickListener listener;

    public GlobeAlertDialog(OnDialogButtonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    public interface OnDialogButtonClickListener {
        void onConfirmClick();

        void onCancelClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (ContextHub.getScreenWidth(getDialog().getContext()) * 0.8f);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        View view = inflater.inflate(R.layout.dialog_globe_alert, container, false);

        view.findViewById(R.id.dialogCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancelClick();
            }
        });

        view.findViewById(R.id.dialogConfirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirmClick();
            }
        });
        return view;
    }
}
