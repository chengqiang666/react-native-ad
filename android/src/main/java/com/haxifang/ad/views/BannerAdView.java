package com.haxifang.ad.views;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.haxifang.R;
import com.haxifang.ad.AdBoss;
import com.haxifang.ad.utils.DislikeDialog;
import com.haxifang.ad.utils.Utils;

import java.util.List;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class BannerAdView extends RelativeLayout {
    private ReactContext reactContext;
    private Activity activity;

    private String _codeId;
    private int _adWidth;

    final protected RelativeLayout relativeLayout;

    private long startTime = 0;

    public BannerAdView(ReactContext context) {
        super(context);
        reactContext = context;
        activity = context.getCurrentActivity();
        // 根据布局id把这个布局加载成一个View并返回的
        inflate(context, R.layout.activity_banner, this);
        relativeLayout = findViewById(R.id.banner_container);

        Utils.setupLayoutHack(this);
    }

    public void setAdWidth(int width) {
        _adWidth = width;
        loadBannerAd();
    }

    public void setCodeId(String codeId) {
        _codeId = codeId;
        loadBannerAd();
    }

    public void loadBannerAd() {
        if (_codeId == null || _adWidth == 0) {
            return;
        }

        runOnUiThread(this::showBannerAd);
    }


    public void showBannerAd() {

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(_codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(_adWidth, 0) //期望个性化模板广告view的size,单位dp
                .setImageAcceptedSize(600, 300)//这个参数设置即可，不影响个性化模板广告的size
                .build();

        final BannerAdView _this = this;

        AdBoss.TTAdSdk.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                relativeLayout.removeAllViews();
                _this.onAdError(message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    _this.onAdError("加载成功，无广告内容");
                    return;
                }
                TTNativeExpressAd ad = ads.get(0);
                ad.setSlideIntervalTime(30 * 1000);
                bindAdListener(ad);
                startTime = System.currentTimeMillis();
                ad.render();
            }
        });

    }


    // 绑定Feed express ================================
    final private void bindAdListener(TTNativeExpressAd ad) {

        final BannerAdView _this = this;

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                onAdClick();
            }

            @Override
            public void onAdShow(View view, int type) {
                onAdBannerShow();
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                _this.onAdError("加载成功 渲染失败 code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                relativeLayout.removeAllViews();
                relativeLayout.addView(view);
                onAdLayout((int) width, (int) height);
            }
        });

        // dislike设置
        bindDislike(ad, true);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }

        // 可选，下载监听设置
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                // TToast.show(getContext(), "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            }

            @Override
            public void onInstalled(String fileName, String appName) {
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            }
        });
    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     *
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            // 使用自定义样式
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(getContext(), words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    // 屏蔽广告
                    // TToast.show(mContext, "点击=" + filterWord.getName());
                    // 用户选择不喜欢原因后，移除广告展示
                    relativeLayout.removeAllViews();
                    onAdClose(filterWord.getName());
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        // 使用默认个性化模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                // TToast.show(mContext, "点击 " + value);
                // 用户选择不喜欢原因后，移除广告展示
                relativeLayout.removeAllViews();
                onAdClose(value);
            }

            @Override
            public void onCancel() {
                // TToast.show(mContext, "点击取消 ");
            }

            @Override
            public void onRefuse() {

            }
        });
    }

    // 外部事件..
    public void onAdError(String message) {
        WritableMap event = Arguments.createMap();
        event.putString("message", message);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdError", event);
    }

    public void onAdClick() {
        WritableMap event = Arguments.createMap();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdClick", event);
    }

    public void onAdBannerShow() {
        WritableMap event = Arguments.createMap();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdBannerShow", event);
    }

    public void onAdClose(String reason) {
        WritableMap event = Arguments.createMap();
        event.putString("reason", reason);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdClose", event);
    }

    public void onAdLayout(int width, int height) {
        WritableMap event = Arguments.createMap();
        event.putInt("width", width);
        event.putInt("height", height);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdLayout", event);
    }


}
