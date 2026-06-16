package com.pengxh.androidx.lib.calendar;

import android.graphics.Rect;

import java.util.Calendar;

public class DayCell {
    public Calendar date;
    public boolean isCurrentMonth;
    public Rect rect = new Rect();

    public DayCell(Calendar date, boolean isCurrentMonth) {
        this.date = (Calendar) date.clone();
        this.isCurrentMonth = isCurrentMonth;
    }
}