package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.DialogHub;
import com.pengxh.androidx.lite.hub.FloatHub;

import java.util.List;

/**
 * 底部列表Sheet
 */
public class BottomActionSheet extends Dialog {


    private final Context ctx;
    private final List<String> sheetItems;
    private final int itemTextColor;
    private final OnActionSheetListener sheetListener;

    public static class Builder {
        private Context context;
        private List<String> actionItems;
        private int color;
        private OnActionSheetListener listener;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setActionItemTitle(List<String> items) {
            this.actionItems = items;
            return this;
        }

        public Builder setItemTextColor(@ColorInt int color) {
            this.color = color;
            return this;
        }

        public Builder setOnActionSheetListener(OnActionSheetListener listener) {
            this.listener = listener;
            return this;
        }

        public BottomActionSheet build() {
            return new BottomActionSheet(this);
        }
    }

    private BottomActionSheet(Builder builder) {
        super(builder.context, R.style.UserDefinedActionStyle);
        this.ctx = builder.context;
        this.sheetItems = builder.actionItems;
        this.itemTextColor = builder.color;
        this.sheetListener = builder.listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogHub.resetParams(this, Gravity.BOTTOM, R.style.ActionSheetDialogAnimation, 1);
        setContentView(R.layout.bottom_action_sheet);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        ListView itemListView = findViewById(R.id.itemListView);
        itemListView.setAdapter(new ItemListAdapter(ctx));
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dismiss();
                sheetListener.onActionItemClick(position);
            }
        });

        TextView sheetCancelView = findViewById(R.id.sheetCancelView);
        sheetCancelView.setTextColor(itemTextColor);
        sheetCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public interface OnActionSheetListener {
        void onActionItemClick(int position);
    }

    class ItemListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;

        ItemListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return sheetItems.size();
        }

        @Override
        public Object getItem(int position) {
            return sheetItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                convertView = inflater.inflate(R.layout.item_action_sheet, null);
                holder.sheetItemView = convertView.findViewById(R.id.sheetItemView);
                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.sheetItemView.setBackgroundResource(R.drawable.sheet_item_top_selector);
            } else if (position == sheetItems.size() - 1) {
                holder.sheetItemView.setBackgroundResource(R.drawable.sheet_item_bottom_selector);
            } else {
                holder.sheetItemView.setBackgroundResource(R.drawable.sheet_item_middle_selector);
            }
            holder.sheetItemView.setText(sheetItems.get(position));
            holder.sheetItemView.setTextSize(16);
            holder.sheetItemView.setTextColor(itemTextColor);
            //需要动态设置item的高度
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, FloatHub.dp2px(ctx, 44));
            convertView.setLayoutParams(param);
            return convertView;
        }
    }

    static class ItemViewHolder {
        TextView sheetItemView;
    }
}
