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
import com.pengxh.androidx.lite.kit.ContextKit;

import java.util.List;

public class EasyPopupWindow extends PopupWindow {

    public EasyPopupWindow(Context context) {
        setWidth((int) (ContextKit.getScreenWidth(context) * 0.4));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.EasyPopupAnimation);
        setBackgroundDrawable(null);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_option, null, false);
        setContentView(view);
    }

    public void set(List<MenuItem> menuItems, OnPopupWindowClickListener windowClickListener) {
        View contentView = getContentView();
        ListView listView = contentView.findViewById(R.id.listView);
        final LayoutInflater inflater = LayoutInflater.from(contentView.getContext());
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return menuItems.size();
            }

            @Override
            public Object getItem(int position) {
                return menuItems.get(position);
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

                MenuItem menuItem = menuItems.get(position);
                holder.imageView.setBackgroundResource(menuItem.getIcon());
                holder.titleView.setText(menuItem.getName());
                return convertView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                windowClickListener.onPopupItemClicked(position);
                dismiss();
            }
        });
    }

    public interface OnPopupWindowClickListener {
        void onPopupItemClicked(int position);
    }

    static class PopupWindowHolder {
        ImageView imageView;
        TextView titleView;
    }

    static class MenuItem {
        private int icon;
        private String name;

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
