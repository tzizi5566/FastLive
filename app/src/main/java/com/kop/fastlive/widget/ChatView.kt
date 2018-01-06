package com.kop.fastlive.widget

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.blankj.utilcode.util.KeyboardUtils
import com.kop.fastlive.R
import com.kop.fastlive.model.Constants
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVText.ILVTextType.eGroupMsg
import kotlinx.android.synthetic.main.view_chat.view.cb_switch
import kotlinx.android.synthetic.main.view_chat.view.edt_chat
import kotlinx.android.synthetic.main.view_chat.view.tv_send

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/3 15:43
 */
class ChatView : LinearLayoutCompat, OnClickListener, OnCheckedChangeListener {

  interface OnChatSendListener {
    fun onChatSend(cmd: ILVCustomCmd)
  }

  private var mOnChatSendListener: OnChatSendListener? = null

  fun setOnChatSendListener(l: OnChatSendListener) {
    mOnChatSendListener = l
  }

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
    LayoutInflater.from(context).inflate(R.layout.view_chat, this, true)
    registerListener()
  }

  private fun registerListener() {
    tv_send.setOnClickListener(this)
    cb_switch.setOnCheckedChangeListener(this)
  }

  private fun sendChatMsg() {
    mOnChatSendListener?.let {
      val ilvCustomCmd = ILVCustomCmd()
      ilvCustomCmd.type = eGroupMsg
      ilvCustomCmd.cmd = if (cb_switch.isChecked) Constants.CMD_CHAT_MSG_DANMU else Constants.CMD_CHAT_MSG_LIST
      ilvCustomCmd.param = edt_chat.text.toString()
      ilvCustomCmd.destId = ILiveRoomManager.getInstance().imGroupId
      mOnChatSendListener!!.onChatSend(ilvCustomCmd)
      edt_chat.text.clear()
    }
  }

  override fun onClick(v: View?) {
    sendChatMsg()
  }

  override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
    val hint = if (isChecked) "发送弹幕聊天消息" else "和大家聊点什么吧"
    edt_chat.hint = hint
  }

  fun setFocusable(activity: Activity) {
    edt_chat.isFocusable = true
    edt_chat.isFocusableInTouchMode = true
    edt_chat.requestFocus()
    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    KeyboardUtils.showSoftInput(activity)
  }
}