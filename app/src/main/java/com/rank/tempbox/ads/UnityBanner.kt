package com.rank.tempbox.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun UnityBanner() {
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        factory = { context ->
            val activity = context.findActivity()
            if (activity != null) {
                BannerView(activity, "Banner_Android", UnityBannerSize(320, 50)).apply {
                    listener = object : BannerView.IListener {
                        override fun onBannerLoaded(bannerAdView: BannerView) {
                            Log.d("UnityAds", "Banner loaded successfully")
                        }
                        override fun onBannerClick(bannerAdView: BannerView) {}
                        override fun onBannerFailedToLoad(bannerAdView: BannerView, errorInfo: BannerErrorInfo) {
                            Log.e("UnityAds", "Banner failed to load: ${errorInfo.errorMessage}")
                        }
                        override fun onBannerLeftApplication(bannerView: BannerView) {}
                        override fun onBannerShown(bannerView: BannerView) {}
                    }
                    load()
                }
            } else {
                android.widget.FrameLayout(context) // Fallback empty view
            }
        }
    )
}
