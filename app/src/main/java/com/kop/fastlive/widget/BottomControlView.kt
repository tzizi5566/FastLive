package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.kop.fastlive.R
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_chat
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_close
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_gift
import kotlinx.android.synthetic.main.view_bottom_control.view.iv_operate

/**
 * 功    能: 直播界面底部操作栏
 * 创 建 人: KOP
 * 创建日期: 2018/1/3 15:11
 */
class BottomControlView : LinearLayoutCompat, OnClickListener {

  interface OnControlListener {
    fun onChatClick()

    fun onCloseClick()

    fun onGiftClick()

    fun onOperateClick(view: View)
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
    iv_operate.setOnClickListener(this)
  }

  fun setIsHost(isHost: Boolean) {
    if (isHost) {
      iv_gift.visibility = View.INVISIBLE
      iv_operate.visibility = View.VISIBLE
    } else {
      iv_gift.visibility = View.VISIBLE
      iv_operate.visibility = View.INVISIBLE
    }
  }

  fun setOperateRes(dismiss: Boolean) {
    if (dismiss) {
      iv_operate.setImageResource(R.drawable.icon_op_open)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.iv_chat -> {
        mOnControlListener?.onChatClick()
      }

      R.id.iv_close -> {
        mOnControlListener?.onCloseClick()
      }

      R.id.iv_gift -> {
        mOnControlListener?.onGiftClick()
      }

      R.id.iv_operate -> {
        mOnControlListener?.let {
          it.onOperateClick(v)
          iv_operate.setImageResource(R.drawable.icon_op_close)
        }
      }
    }
  }
}