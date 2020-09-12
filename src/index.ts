import { NativeModules,NativeEventEmitter } from 'react-native';

import startSplash from './Splash';
import startFullVideo from './FullScreenVideo';
import startRewardVideo from './RewardVideo';
import DrawFeed from './DrawFeed';
import Feed from './Feed';

const { AdManager,SplashAd } = NativeModules;

type appInfo = {
    appid: string;
    app: string;
    uid: string;
    amount: number;
    reward: string;
};

export const init = (appInfo) => {
    //FIXME: init 传入一些codeid可以提前加载广告，比如视频类
    AdManager.init(appInfo);
};

type feedInfo = {
    appid: string;
    codeid: string;
};

export const loadFeedAd = (info: feedInfo) => {
    //提前加载信息流FeedAd, 结果返回promise
    return AdManager.loadFeedAd(info);
};

type adInfo = {
    appId: string;
    codeId: string;
    extra: string;
    userId: string;
};
export const loadRewardAd = (info: adInfo) => {
    //提前加载激励视频, 结果返回promise
    return AdManager.loadRewardAd(info);
};

const listenerLoadCache = {};
export const initSplashAd = (info: adInfo) => {
    console.log(info);
    const eventEmitter = new NativeEventEmitter(SplashAd);
    SplashAd.initSplashAd(info);
    const subscribe = (type, callback) => {
        if (listenerLoadCache[type]) {
            listenerLoadCache[type].remove();
        }
        return (listenerLoadCache[type] = eventEmitter.addListener('SplashAd-' + type, (event: any) => {
            callback(event);
        }));
    };

    return {
        subscribe,
    };
};

export default {
    init,
    loadFeedAd,
    startSplash,
    startFullVideo,
    startRewardVideo,
    DrawFeed,
    Feed,
    loadRewardAd,
    initSplashAd
};
