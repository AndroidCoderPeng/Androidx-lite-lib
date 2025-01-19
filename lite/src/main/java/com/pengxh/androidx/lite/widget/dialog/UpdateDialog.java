package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.adapter.NormalRecyclerAdapter;
import com.pengxh.androidx.lite.adapter.ViewHolder;
import com.pengxh.androidx.lite.kit.DialogKit;

import java.util.ArrayList;

public class UpdateDialog extends Dialog {

    private final ArrayList<String> message;
    private final OnUpdateListener listener;

    public static class Builder {
        private Context context;
        private ArrayList<String> message;
        private OnUpdateListener listener;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setUpdateMessage(ArrayList<String> message) {
            this.message = message;
            return this;
        }

        public Builder setOnUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
            return this;
        }

        public UpdateDialog build() {
            return new UpdateDialog(this);
        }
    }

    private UpdateDialog(Builder builder) {
        super(builder.context, R.style.UserDefinedDialogStyle);
        this.message = builder.message;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogKit.resetParams(this, 1f);
        setContentView(R.layout.dialog_update);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new NormalRecyclerAdapter<String>(R.layout.item_update_rv_l, message) {

            @Override
            public void convertView(ViewHolder viewHolder, int position, String item) {
                viewHolder.setText(R.id.indexView, (position + 1) + ".")
                        .setText(R.id.textView, item);
            }
        });

        findViewById(R.id.updateVersionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUpdate();
                dismiss();
            }
        });

        findViewById(R.id.cancelVersionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnUpdateListener {
        void onUpdate();
    }
}
