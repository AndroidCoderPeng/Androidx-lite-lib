package com.pengxh.androidx.lib.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lib.calendar.CalendarView;
import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = this;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void observeRequestState() {

    }

    @Override
    protected void initEvent() {
        mBinding.calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateStr = sdf.format(calendar.getTime());
                Toast.makeText(context, "选择了: " + dateStr, Toast.LENGTH_SHORT).show();
                Log.d("Calendar", "Selected date: " + dateStr);
            }
        });
    }
}