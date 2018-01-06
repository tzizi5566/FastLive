package com.kop.fastlive.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.utils.ImgUtil
import kotlinx.android.synthetic.main.view_danmu_item.view.iv_avatar
import kotlinx.android.synthetic.main.view_danmu_item.view.tv_chat_content
import kotlinx.android.synthetic.main.view_danmu_item.view.tv_user_name

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/6 14:08
 */
class DanmuItemView : RelativeLayout, Animation.AnimationListener {

  interface OnAvaliableListener {
    fun onAvaliable()
  }

  private var onAvaliableListener: OnAvaliableListener? = null

  fun setOnAvaliableListener(l: OnAvaliableListener) {
    onAvaliableListener = l
  }

  private var translateAnim: Animation? = null

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
    LayoutInflater.from(context).inflate(R.layout.view_danmu_item, this, true)
    //创建动画，水平位移
    translateAnim = AnimationUtils.loadAnimation(context, R.anim.danmu_item_anim)
    translateAnim!!.setAnimationListener(this)
  }

  fun showMsg(danmuInfo: ChatMsgInfo) {
    bindMsgToView(danmuInfo)

    //在动画监听里面做处理，调用post保证在动画结束之后再start
    //解决start之后直接end的情况。
    post { startAnimation(translateAnim) }
  }

  private fun bindMsgToView(danmuInfo: ChatMsgInfo) {
    val avatar = danmuInfo.avatar
    if (avatar.isEmpty()) {
      ImgUtil.loadRound(context, R.drawable.default_avatar, iv_avatar)
    } else {
      ImgUtil.loadRound(context, avatar, iv_avatar)
    }
    tv_user_name.text = danmuInfo.nickname
    tv_chat_content.text = danmuInfo.text
  }

  override fun onAnimationEnd(animation: Animation?) {
    visibility = View.INVISIBLE
    onAvaliableListener?.onAvaliable()
  }

  override fun onAnimationStart(animation: Animation?) {
    visibility = View.VISIBLE
  }

  override fun onAnimationRepeat(animation: Animation?) {

  }
}