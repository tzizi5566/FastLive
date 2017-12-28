package com.kop.fastlive

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.kop.fastlive.module.editprofile.CustomProfile
import com.kop.fastlive.utils.QnUploadHelper
import com.tencent.TIMManager
import com.tencent.TIMUserProfile
import com.tencent.ilivesdk.ILiveSDK
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/19 16:51
 */
class MyApplication : Application() {

  private var mSelfProfile: TIMUserProfile? = null

  override fun onCreate() {
    super.onCreate()
    initLiveSdk()
    initQiNiu()
    Utils.init(this)
  }

  private fun initLiveSdk() {
    ILiveSDK.getInstance().initSdk(this, 1400054333, 20062)

    val custom = arrayListOf<String>()
    custom.add(CustomProfile.CUSTOM_GET)
    custom.add(CustomProfile.CUSTOM_LEVEL)
    custom.add(CustomProfile.CUSTOM_SEND)
    custom.add(CustomProfile.CUSTOM_RENZHENG)
    TIMManager.getInstance().initFriendshipSettings(CustomProfile.allBaseInfo, custom)

    ILVLiveManager.getInstance().init(ILVLiveConfig())
  }

  private fun initQiNiu() {
    QnUploadHelper.init(
        "c9SZK9sXguQ4hQBksgk1b5qJuXNtBVLDxPi2WSFF",
        "DuWKkKwfW5EDaCE5OEfytklw3JP0Astk3seX3usR",
        "http://7xslu7.com1.z0.glb.clouddn.com/",
        "tzizi5566")
  }

  fun setSelfProfile(userProfile: TIMUserProfile) {
    mSelfProfile = userProfile
  }

  fun getSelfProfile(): TIMUserProfile? {
    return mSelfProfile
  }
}