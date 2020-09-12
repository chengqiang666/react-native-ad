package com.haxifang.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.haxifang.ad.activities.SplashActivity;

public class SplashAd extends ReactContextBaseJavaModule {

    static String TAG = "SplashAd";
    static ReactApplicationContext mContext;

    public SplashAd(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "SplashAd";
    }

    @ReactMethod
    public void loadSplashAd(ReadableMap options) {
        String codeId = options.getString("codeid");
        startSplash(codeId);
    }


    @ReactMethod
    public void initSplashAd(ReadableMap options) {
        String codeId = options.getString("codeId");
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();

        // 请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        AdBoss.TTAdSdk.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {

            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, message);
                fireEvent("onLoadAdError", code, message);
            }

            @Override
            @MainThread
            public void onTimeout() {
                fireEvent("onLoadAdError", 400, "加载超时");
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                if (ad == null) {
                    fireEvent("onLoadAdError", 400, "未拉取到开屏广告");
                    return;

                }
                fireEvent("onLoadAdSuccess", 200, "开屏广告缓存成功");
                AdBoss.splashAd = ad;
            }
        }, 3000);
    }


    private void startSplash(String codeid) {
        Intent intent = new Intent(mContext, SplashActivity.class);
        try {
            intent.putExtra("codeId", codeid);
            final Activity context = getCurrentActivity();
            context.overridePendingTransition(0, 0); // 不要过渡动画
            context.startActivityForResult(intent, 10000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送事件到RN
    public static void sendEvent(String eventName, @Nullable WritableMap params) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(TAG + "-" + eventName, params);
    }

    // 二次封装发送到RN的事件函数
    public static void fireEvent(String eventName, int startCode, String message) {
        WritableMap p = Arguments.createMap();
        p.putInt("code", startCode);
        p.putString("message", message);
        sendEvent(eventName, p);
    }
}
