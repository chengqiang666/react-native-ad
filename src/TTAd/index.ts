import { NativeModules } from "react-native";

import loadSplashAd from "./Splash";
import loadFullVideoAd from "./FullVideo";
import loadRewardVideoAd from "./RewardVideo";
import DrawFeedAd from "./DrawFeed";
import FeedAd from "./Feed";
import BannerAd from "./Banner";

const { TTAdManager } = NativeModules;
export const init = (appid: string) => {
  TTAdManager.init(appid);
};

export default {
  init,
  loadSplashAd,
  loadFullVideoAd,
  loadRewardVideoAd,
  DrawFeedAd,
  FeedAd,
  BannerAd,
};
