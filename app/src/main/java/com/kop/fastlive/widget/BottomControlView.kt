package com.kop.fastlive.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import com.kop.fastlive.R
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_chat
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_close
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_gift

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/3 15:11
 */
class BottomControlView : RelativeLayout, OnClickListener {

  interface OnControlListener {
    fun onChatClick()

    fun onCloseClick()

    fun onGiftClick()
  }

  private var mOnControlListener: OnControlListener? = null

  fun setOnControlListener(l: OnControlListener) {
    mOnControlListener = l
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
    LayoutInflater.from(context).inflate(R.layout.view_bottom_control, this, true)
    registerListener()
  }

  private fun registerListener() {
    iv_chat.setOnClickListener(this)
    iv_close.setOnClickListener(this)
    iv_gift.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.iv_chat -> {
        if (mOnControlListener != null) {
          mOnControlListener!!.onChatClick()
        }
      }

      R.id.iv_close -> {
        if (mOnControlListener != null) {
          mOnControlListener!!.onCloseClick()
        }
      }

      R.id.iv_gift -> {
        if (mOnControlListener != null) {
          mOnControlListener!!.onGiftClick()
        }
      }
    }
  }
}