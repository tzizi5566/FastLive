package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kop.fastlive.R
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.widget.GiftRepeatItemView.OnGiftItemAvaliableListener
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.view_gift_repeat.view.gift_item_1
import kotlinx.android.synthetic.main.view_gift_repeat.view.gift_item_2
import java.util.LinkedList

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/10 14:31
 */
class GiftRepeatView : LinearLayoutCompat, OnGiftItemAvaliableListener {

  private val mList = LinkedList<GiftCachInfo>()

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

    gift_item_1.setOnGiftItemAvaliableListener(this)
    gift_item_2.setOnGiftItemAvaliableListener(this)
  }

  fun showGift(giftInfo: GiftInfo?, repeatId: String, userProfile: TIMUserProfile) {
    val repeatItemView = getAvaliableItemView(giftInfo, repeatId, userProfile)
    if (repeatItemView == null) {
      val cachInfo = GiftCachInfo(giftInfo!!, repeatId, userProfile)
      mList.add(cachInfo)
    } else {
      repeatItemView.showGift(giftInfo, repeatId, userProfile)
    }
  }

  private fun getAvaliableItemView(giftInfo: GiftInfo?, repeatId: String,
      userProfile: TIMUserProfile): GiftRepeatItemView? {
    if (gift_item_1.isMatch(giftInfo, repeatId, userProfile)) {
      return gift_item_1
    }

    if (gift_item_2.isMatch(giftInfo, repeatId, userProfile)) {
      return gift_item_2
    }

    if (gift_item_1.isAvaliable()) {
      return gift_item_1
    }

    if (gift_item_2.isAvaliable()) {
      return gift_item_2
    }
    return null
  }

  override fun onAvaliable() {
    if (mList.size > 0) {
      val gift = mList.removeAt(0)
      showGift(gift.giftInfo, gift.repeatId, gift.senderProfile)

      val sameList = mList.filter {
        it.senderProfile.identifier == gift.senderProfile.identifier &&
            it.repeatId == gift.repeatId &&
            it.giftInfo.giftId == gift.giftInfo.giftId
      }
      mList.removeAll(sameList)

      for (it in sameList) {
        showGift(it.giftInfo, it.repeatId, it.senderProfile)
      }
    }
  }

  class GiftCachInfo(
      val giftInfo: GiftInfo,
      val repeatId: String,
      val senderProfile: TIMUserProfile
  )
}