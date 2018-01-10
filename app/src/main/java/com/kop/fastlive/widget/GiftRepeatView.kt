package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kop.fastlive.R
import com.kop.fastlive.model.GiftInfo
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.view_gift_repeat.view.gift_item_1
import kotlinx.android.synthetic.main.view_gift_repeat.view.gift_item_2

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/10 14:31
 */
class GiftRepeatView : LinearLayoutCompat {

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
    LayoutInflater.from(context).inflate(R.layout.view_gift_repeat, this, true)

    gift_item_1.visibility = View.INVISIBLE
    gift_item_2.visibility = View.INVISIBLE
  }

  fun showGift(giftInfo: GiftInfo?, userProfile: TIMUserProfile) {
    val repeatItemView = getAvaliableItemView()
    if (repeatItemView == null) {

    } else {
      repeatItemView.showGift(giftInfo, userProfile)
    }
  }

  private fun getAvaliableItemView(): GiftRepeatItemView? {
    if (gift_item_1.isAvaliable()) {
      return gift_item_1
    }
    if (gift_item_2.isAvaliable()) {
      return gift_item_2
    }
    return null
  }
}