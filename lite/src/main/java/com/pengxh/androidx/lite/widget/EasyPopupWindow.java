package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

public class EasyPopupWindow extends PopupWindow {

    private OnPopupWindowClickListener clickListener;

    public EasyPopupWindow(Context context) {
        setWidth((int) (DeviceSizeUtil.getScreenWidth(context) * 0.3));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.PopupAnimation);
        setBackgroundDrawable(null);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_option, null, false);
        setContentView(view);
    }

    public void setPopupMenuItem(int[] imageArray, String[] titleArray) {
        View contentView = getContentView();
        try {
            ListView popupListView = contentView.findViewById(R.id.popupListView);
            popupListView.setAdapter(new BaseAdapter() {

                private final LayoutInflater inflater = LayoutInflater.from(contentView.getContext());

                @Override
                public int getCount() {
                    return imageArray.length;
                }

                @Override
                public Object getItem(int position) {
                    return imageArray[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    PopupWindowHolder holder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.item_popup_menu, null);
                        holder = new PopupWindowHolder();
                        holder.imageView = convertView.findViewById(R.id.imageView);
                        holder.titleView = convertView.findViewById(R.id.titleView);
                        convertView.setTag(holder);
                    } else {
                        holder = (PopupWindowHolder) convertView.getTag();
                    }
                    holder.imageView.setBackgroundResource(imageArray[position]);
                    holder.titleView.setText(titleArray[position]);
                    return convertView;
                }
            });
            popupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickListener.onPopupItemClicked(position);
                    dismiss();
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public interface OnPopupWindowClickListener {
        void onPopupItemClicked(int position);
    }

    public void setOnPopupWindowClickListener(OnPopupWindowClickListener windowClickListener) {
        this.clickListener = windowClickListener;
    }

    static class PopupWindowHolder {
        ImageView imageView;
        TextView titleView;
    }
}
