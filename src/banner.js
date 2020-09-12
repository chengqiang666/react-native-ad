import React from 'react';
import { requireNativeComponent, StyleSheet } from 'react-native';
const BannerAdManage = requireNativeComponent('BannerAdManage');

const Banner = (props) => {
	const { codeId, adWidth,onAdLayout, onAdError, onAdClose, onAdClick,onAdBannerShow } = props;
	const [height, setHeight] = React.useState(0); // 默认高度

    console.log(props);
	return (
        <BannerAdManage
            codeId={codeId}
            adWidth={adWidth}
            style={{ width: adWidth, height }}
            onAdError={(e: any) => {
                onAdError && onAdError(e.nativeEvent);
            }}
            onAdClick={(e: any) => {
                onAdClick && onAdClick(e.nativeEvent);
            }}
            onAdClose={(e: any) => {
                onAdClose && onAdClose(e.nativeEvent);
            }}
            onAdBannerShow={(e: any) => {
                onAdClose && onAdClose(e.nativeEvent);
            }}
            onAdLayout={(e: any) => {
                if (e.nativeEvent.height) {
                    setHeight(e.nativeEvent.height);
                    onAdLayout && onAdLayout(e.nativeEvent);
                }
            }}
        />
    );
}

export default Banner;