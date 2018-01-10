package com.kop.fastlive.model

import com.tencent.livesdk.ILVLiveConstants

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/9 14:40
 */
class ChatType {

  companion object {
    //自定义发送列表聊天
    const val CMD_CHAT_MSG_LIST = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 1

    //自定义发送弹幕聊天
    const val CMD_CHAT_MSG_DANMU = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 2

    //自定义发送礼物
    const val CMD_CHAT_GIFT = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 3
  }
}