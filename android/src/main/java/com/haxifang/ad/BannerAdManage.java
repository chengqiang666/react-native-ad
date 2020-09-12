package com.haxifang.ad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.haxifang.ad.views.BannerAdView;

import java.util.Map;

public class BannerAdManage extends ViewGroupManager<BannerAdView> {

    public static ReactApplicationContext reactAppContext;

    public BannerAdManage(ReactApplicationContext context) {
        reactAppContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "BannerAdManage";
    }

    @NonNull
    @Override
    protected BannerAdView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new BannerAdView(reactContext);
    }

    // 该方法告诉react-native当你添加一个react view时，会调用Android原生的layout方法
    @Override
    public boolean needsCustomLayoutForChildren() {
        return true;
    }


    @ReactProp(name = "codeId")
    public void setCodeId(BannerAdView view, @Nullable String codeId) {
        view.setCodeId(codeId);
    }

    @ReactProp(name = "adWidth")
    public void setAdWidth(BannerAdView view, @Nullable int adWidth) {
        view.setAdWidth(adWidth);
    }


    // getExportedCustomBubblingEventTypeConstants 方法将事件通知映射到JavaScript端
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("onAdClick",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAdClick")))
                .put("onAdError",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAdError")))
                .put("onAdClose",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAdClose")))
                .put("onAdLayout",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAdLayout")))
                .put("onAdBannerShow",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAdBannerShow")))
                .build();
    }
}
