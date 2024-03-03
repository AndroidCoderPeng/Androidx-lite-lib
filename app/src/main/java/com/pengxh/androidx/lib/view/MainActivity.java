package com.pengxh.androidx.lib.view;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.ObjectHub;
import com.pengxh.androidx.lite.hub.StringHub;
import com.pengxh.androidx.lite.utils.LoadState;
import com.pengxh.androidx.lite.utils.LoadingDialogHub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private NetworkViewModel viewModel;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
    }

    @Override
    protected void initEvent() {
        ArrayList<String> list = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //非系统应用
                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                list.add(appName.replace(" ", "").replace("-", "")
                        .replace("（", "").replace("）", ""));
            }
        }
        list.add("Play商店");
        list.add("设置");
        list.add("相机");
        list.add("相册");
        list.add("浏览器");

        ArrayList<App> apps = new ArrayList<>();
        for (String s : list) {
            App app = new App();
            app.setName(s);
            if (s.contains("7881")) {
                app.setPy("QIBABAYIYOUXIJIAOYI");
            } else if (s.contains("长沙")) {
                app.setPy("CHANGSHADITIE");
            } else if (s.contains("铁路12306")) {
                app.setPy("TIELUYIERSANLINGLIU");
            } else {
                app.setPy(StringHub.getHanYuPinyin(s).toUpperCase(Locale.ROOT));
            }
            apps.add(app);
        }

        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App o1, App o2) {
                return o1.py.compareTo(o2.py);
            }
        });
        Log.d(TAG, ObjectHub.toJson(apps));
    }

    static class App {
        private String name;
        private String py;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPy() {
            return py;
        }

        public void setPy(String py) {
            this.py = py;
        }
    }

    @Override
    protected void observeRequestState() {
        viewModel.loadState.observe(this, new Observer<LoadState>() {
            @Override
            public void onChanged(LoadState loadState) {
                if (loadState == LoadState.Loading) {
                    LoadingDialogHub.show(MainActivity.this, "数据加载中，请稍后");
                } else {
                    LoadingDialogHub.dismiss();
                }
            }
        });
    }
}