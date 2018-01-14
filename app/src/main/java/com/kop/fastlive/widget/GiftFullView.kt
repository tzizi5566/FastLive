package com.kop.fastlive.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.widget.PorcheView.OnAvaliableListener
import com.tencent.TIMUserProfile
import java.util.LinkedList

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/14 13:33
 */
class GiftFullView : RelativeLayout, OnAvaliableListener {

  private lateinit var mPorcheView: PorcheView
  private val mList = LinkedList<GiftCachInfo>()
  private var mIsShow = false

  constructor(context: Context?) : super(context) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  private fun init() {

  }

  fun showGift(giftInfo: GiftInfo?, userProfile: TIMUserProfile) {
    if (mIsShow) {
      val cachInfo = GiftCachInfo(giftInfo!!, userProfile)
      mList.add(cachInfo)
    } else {
      mIsShow = true
      if (giftInfo?.giftId == GiftInfo.Gift_BaoShiJie.giftId) {
        showPorch(userProfile)
      }
    }
  }

  private fun showPorch(userProfile: TIMUserProfile) {
    mPorcheView = PorcheView(context)
    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)
    params.addRule(CENTER_IN_PARENT)
    mPorcheView.setOnAvaliableListener(this)
    addView(mPorcheView, params)

    if (mPorcheView.isAvaliable()) {
      mPorcheView.showGift(userProfile)
    }
  }

  override fun onAvaliable() {
    mIsShow = false
    if (mList.size > 0) {
      val cachInfo = mList.removeAt(0)
      showGift(cachInfo.giftInfo, cachInfo.senderProfile)
    }
  }

  class GiftCachInfo(
      val giftInfo: GiftInfo,
      val senderProfile: TIMUserProfile
  )
}