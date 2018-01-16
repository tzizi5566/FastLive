package com.kop.fastlive.model

import com.kop.fastlive.R

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/9 14:38
 */
data class GiftInfo(
    val giftId: Int,
    val giftResId: Int,
    val expValue: Int,
    val name: String,
    val type: GiftType
) {
  companion object {
    var Gift_Heart = GiftInfo(-1, 0, 0, "", GiftType.HeartGift)
    var Gift_Empty = GiftInfo(0, R.drawable.gift_none, 0, "", GiftType.ContinueGift)
    var Gift_BingGun = GiftInfo(1, R.drawable.gift_1, 1, "冰棍", GiftType.ContinueGift)
    var Gift_BingJiLing = GiftInfo(2, R.drawable.gift_2, 5, "冰激凌", GiftType.ContinueGift)
    var Gift_MeiGui = GiftInfo(3, R.drawable.gift_3, 10, "玫瑰花", GiftType.ContinueGift)
    var Gift_PiJiu = GiftInfo(4, R.drawable.gift_4, 15, "啤酒", GiftType.ContinueGift)
    var Gift_HongJiu = GiftInfo(5, R.drawable.gift_5, 20, "红酒", GiftType.ContinueGift)
    var Gift_Hongbao = GiftInfo(6, R.drawable.gift_6, 50, "红包", GiftType.ContinueGift)
    var Gift_ZuanShi = GiftInfo(7, R.drawable.gift_7, 100, "钻石", GiftType.ContinueGift)
    var Gift_BaoXiang = GiftInfo(8, R.drawable.gift_8, 200, "宝箱", GiftType.ContinueGift)
    var Gift_BaoShiJie = GiftInfo(9, R.drawable.gift_9, 1000, "保时捷", GiftType.FullScreenGift)

    fun getGiftById(id: Int): GiftInfo? {
      when (id) {
        -1 -> return Gift_Heart
        1 -> return Gift_BingGun
        2 -> return Gift_BingJiLing
        3 -> return Gift_MeiGui
        4 -> return Gift_PiJiu
        5 -> return Gift_HongJiu
        6 -> return Gift_Hongbao
        7 -> return Gift_ZuanShi
        8 -> return Gift_BaoXiang
        9 -> return Gift_BaoShiJie
      }
      return null
    }
  }
}