package com.rank.tempbox.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

object UnityAdsManager : IUnityAdsInitializationListener {
    private const val GAME_ID = "6201251"
    
    fun initialize(context: Context) {
        UnityAds.initialize(context, GAME_ID, true, this)
    }

    override fun onInitializationComplete() {
        Log.d("UnityAds", "Initialization Complete")
    }

    override fun onInitializationFailed(error: UnityAds.UnityAdsInitializationError?, message: String?) {
        Log.e("UnityAds", "Init Failed: $message")
    }
}

class InterstitialAdManager(private val context: Context) {
    fun load() {
        UnityAds.load("Interstitial_Android", object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {}
            override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAds.UnityAdsLoadError, message: String) {}
        })
        UnityAds.load("AppOpen_Android", object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {}
            override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAds.UnityAdsLoadError, message: String) {}
        })
    }

    fun onUserAction() {
        UnityAds.show(context as Activity, "Interstitial_Android", UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(placementId: String, error: UnityAds.UnityAdsShowError, message: String) { load() }
            override fun onUnityAdsShowStart(placementId: String) {}
            override fun onUnityAdsShowClick(placementId: String) {}
            override fun onUnityAdsShowComplete(placementId: String, state: UnityAds.UnityAdsShowCompletionState) { load() }
        })
    }

    fun showAppOpenAd() {
        UnityAds.show(context as Activity, "AppOpen_Android", UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(placementId: String, error: UnityAds.UnityAdsShowError, message: String) { load() }
            override fun onUnityAdsShowStart(placementId: String) {}
            override fun onUnityAdsShowClick(placementId: String) {}
            override fun onUnityAdsShowComplete(placementId: String, state: UnityAds.UnityAdsShowCompletionState) { load() }
        })
    }
}

class RewardedAdManager(private val context: Context) {
    fun load() {
        UnityAds.load("Rewarded_Android", object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {}
            override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAds.UnityAdsLoadError, message: String) {}
        })
    }

    fun show(onRewarded: () -> Unit): Boolean {
        UnityAds.show(context as Activity, "Rewarded_Android", UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(placementId: String, error: UnityAds.UnityAdsShowError, message: String) { load() }
            override fun onUnityAdsShowStart(placementId: String) {}
            override fun onUnityAdsShowClick(placementId: String) {}
            override fun onUnityAdsShowComplete(placementId: String, state: UnityAds.UnityAdsShowCompletionState) {
                if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                    onRewarded()
                }
                load()
            }
        })
        return true
    }
}
