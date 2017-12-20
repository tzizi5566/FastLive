package com.kop.fastlive

import android.app.Application
import com.tencent.ilivesdk.ILiveSDK
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/19 16:51
 */
class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initLiveSdk()
  }

  private fun initLiveSdk() {
    ILiveSDK.getInstance().initSdk(this, 1400054333, 20062)
    ILVLiveManager.getInstance().init(ILVLiveConfig())
  }
}