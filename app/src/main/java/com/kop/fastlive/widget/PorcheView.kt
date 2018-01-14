package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import com.kop.fastlive.R
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.utils.ImgUtil
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.view_porche.view.iv_user_header
import kotlinx.android.synthetic.main.view_porche.view.iv_wheel_back
import kotlinx.android.synthetic.main.view_porche.view.iv_wheel_front
import kotlinx.android.synthetic.main.view_porche.view.tv_gift_name
import kotlinx.android.synthetic.main.view_porche.view.tv_user_name


/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/14 13:10
 */
class PorcheView : LinearLayoutCompat {

  private var onAvaliableListener: OnAvaliableListener? = null

  fun setOnAvaliableListener(l: OnAvaliableListener) {
    onAvaliableListener = l
  }

  interface OnAvaliableListener {
    fun onAvaliable()
  }

  private var mBackDrawable: AnimationDrawable? = null
  private var mFrontDrawable: AnimationDrawable? = null
  private var mIntAnim: Animation? = null
  private var mOutAnim: Animation? = null

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
    LayoutInflater.from(context).inflate(R.layout.view_porche, this, true)

    mBackDrawable = iv_wheel_back.drawable as AnimationDrawable
    mFrontDrawable = iv_wheel_front.drawable as AnimationDrawable
    mBackDrawable?.isOneShot = false
    mFrontDrawable?.isOneShot = false

    visibility = View.INVISIBLE
  }

  fun showGift(userProfile: TIMUserProfile) {
    bindData(userProfile)
    startAnim()
  }

  private fun createAnim() {
    val left = left
    val width = width

    mIntAnim = TranslateAnimation(
        Animation.RELATIVE_TO_SELF,
        (0 - (left + width) / width).toFloat(),
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        -1f,
        Animation.RELATIVE_TO_SELF,
        0f)
    mIntAnim?.duration = 500

    mOutAnim = TranslateAnimation(
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        ((left + width) / width).toFloat(),
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        1f)
    mOutAnim?.duration = 500

    mIntAnim?.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        postDelayed({
          startAnimation(mOutAnim)
        }, 3000)
      }

      override fun onAnimationStart(animation: Animation?) {
        mFrontDrawable?.start()
        mBackDrawable?.start()
        visibility = View.VISIBLE
      }
    })

    mOutAnim?.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        visibility = View.INVISIBLE
        mFrontDrawable?.stop()
        mBackDrawable?.stop()

        onAvaliableListener?.onAvaliable()
      }

      override fun onAnimationStart(animation: Animation?) {

      }
    })
  }

  private fun startAnim() {
    post {
      createAnim()
      startAnimation(mIntAnim)
    }
  }

  @SuppressLint("SetTextI18n")
  private fun bindData(userProfile: TIMUserProfile) {
    val avatarUrl = userProfile.faceUrl
    if (avatarUrl.isNullOrEmpty()) {
      ImgUtil.loadRound(context, R.drawable.default_avatar, iv_user_header)
    } else {
      ImgUtil.loadRound(context, avatarUrl, iv_user_header)
    }

    var nickName = userProfile.nickName
    if (nickName.isNullOrEmpty()) {
      nickName = userProfile.identifier
    }
    tv_user_name.text = nickName

    tv_gift_name.text = "送了一个${GiftInfo.Gift_BaoShiJie.name}"
  }

  fun isAvaliable(): Boolean {
    return visibility != View.VISIBLE
  }
}