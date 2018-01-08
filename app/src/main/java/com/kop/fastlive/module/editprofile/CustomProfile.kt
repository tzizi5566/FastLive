package com.kop.fastlive.module.editprofile

import com.tencent.TIMFriendshipManager

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/20 16:47
 */
class CustomProfile {

  companion object {
    //自定义字段
    private const val PREFIX = "Tag_Profile_Custom_"
    const val CUSTOM_RENZHENG = PREFIX + "ATTEST"
    const val CUSTOM_LEVEL = PREFIX + "LEVEL"
    const val CUSTOM_GET: String = PREFIX + "GET"
    const val CUSTOM_SEND = PREFIX + "SEND"

    //腾讯基础字段
    val allBaseInfo = (TIMFriendshipManager.TIM_PROFILE_FLAG_BIRTHDAY or
        TIMFriendshipManager.TIM_PROFILE_FLAG_FACE_URL or
        TIMFriendshipManager.TIM_PROFILE_FLAG_GENDER or
        TIMFriendshipManager.TIM_PROFILE_FLAG_LANGUAGE or
        TIMFriendshipManager.TIM_PROFILE_FLAG_LOCATION or
        TIMFriendshipManager.TIM_PROFILE_FLAG_NICK or
        TIMFriendshipManager.TIM_PROFILE_FLAG_SELF_SIGNATURE or
        TIMFriendshipManager.TIM_PROFILE_FLAG_REMARK or
        TIMFriendshipManager.TIM_PROFILE_FLAG_GROUP).toLong()
  }
}