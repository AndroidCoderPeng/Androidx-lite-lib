package com.pengxh.androidx.lite.utils;

import android.content.Context;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;

/**
 * 高德导航
 */
public class MapNavigateUtil {

    /**
     * 步行导航
     */
    public static void walkNavigationRoute(Context context, Poi poi) {
        AmapNaviParams naviParams = new AmapNaviParams(null, null, poi, AmapNaviType.WALK, AmapPageType.ROUTE);
        AmapNaviPage.getInstance().showRouteActivity(context, naviParams, null);
    }
}
