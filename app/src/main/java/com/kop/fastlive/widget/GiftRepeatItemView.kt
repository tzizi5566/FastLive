package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import com.kop.fastlive.R
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.utils.ImgUtil
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.view_gift_repeat_item.view.iv_gift_img
import kotlinx.android.synthetic.main.view_gift_repeat_item.view.iv_user_header
import kotlinx.android.synthetic.main.view_gift_repeat_item.view.tv_gift_name
import kotlinx.android.synthetic.main.view_gift_repeat_item.view.tv_gift_num
import kotlinx.android.synthetic.main.view_gift_repeat_item.view.tv_user_name

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/10 14:28
 */
class GiftRepeatItemView : LinearLayoutCompat {

  private lateinit var mViewInAnim: Animation
  private lateinit var mViewOutAnim: Animation
  private lateinit var mIconInAnim: Animation
  private lateinit var mNumScaleAnim: Animation

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
    LayoutInflater.from(context).inflate(R.layout.view_gift_repeat_item, this, true)

    initAnim()
  }

  private fun initAnim() {
    mViewInAnim = AnimationUtils.loadAnimation(context, R.anim.repeat_gift_view_in)
    mViewOutAnim = AnimationUtils.loadAnimation(context, R.anim.repeat_gift_view_out)
    mIconInAnim = AnimationUtils.loadAnimation(context, R.anim.repeat_gift_icon_in)
    mNumScaleAnim = AnimationUtils.loadAnimation(context, R.anim.repeat_gift_num_scale)

    mViewInAnim.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        post {
          iv_gift_img.startAnimation(mIconInAnim)
        }
      }

      override fun onAnimationStart(animation: Animation?) {
        visibility = View.VISIBLE
        iv_gift_img.visibility = View.INVISIBLE
        tv_gift_num.visibility = View.INVISIBLE
      }
    })

    mIconInAnim.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        post {
          tv_gift_num.startAnimation(mNumScaleAnim)
        }
      }

      override fun onAnimationStart(animation: Animation?) {
        iv_gift_img.visibility = View.VISIBLE
      }
    })

    mNumScaleAnim.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        postDelayed({
          startAnimation(mViewOutAnim)
        }, 1000)
      }

      override fun onAnimationStart(animation: Animation?) {
        tv_gift_num.visibility = View.VISIBLE
      }
    })

    mViewOutAnim.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        visibility = View.INVISIBLE
      }

      override fun onAnimationStart(animation: Animation?) {

      }
    })
  }

  fun isAvaliable(): Boolean {
    return visibility != View.VISIBLE
  }

  fun showGift(giftInfo: GiftInfo?, userProfile: TIMUserProfile) {
    bindData(giftInfo, userProfile)
    startAnim()
  }

  private fun startAnim() {
    post {
      startAnimation(mViewInAnim)
    }
  }

  @SuppressLint("SetTextI18n")
  private fun bindData(giftInfo: GiftInfo?, userProfile: TIMUserProfile) {
    val avatarUrl = userProfile.faceUrl
    if (avatarUrl.isNullOrEmpty()) {
      ImgUtil.load(context, R.drawable.default_avatar, iv_user_header)
    } else {
      ImgUtil.load(context, avatarUrl, iv_user_header)
    }

    var nickName = userProfile.nickName
    if (nickName.isNullOrEmpty()) {
      nickName = userProfile.identifier
    }
    tv_user_name.text = nickName

    tv_gift_name.text = "送了一个${giftInfo?.name}"

    ImgUtil.load(context, giftInfo?.giftResId!!, iv_gift_img)

    tv_gift_num.text = "x1"
  }
}