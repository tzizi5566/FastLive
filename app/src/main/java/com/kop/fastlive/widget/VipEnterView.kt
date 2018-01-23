package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.kop.fastlive.R
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.view_vip_enter.view.iv_splash
import kotlinx.android.synthetic.main.view_vip_enter.view.tv_user_name
import java.util.LinkedList

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/23 14:35
 */
class VipEnterView : RelativeLayout {

  private lateinit var mViewIn: Animation
  private lateinit var mSplashIn: Animation
  private val mList = LinkedList<TIMUserProfile>()
  private var isInAnim = false

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
    LayoutInflater.from(context).inflate(R.layout.view_vip_enter, this, true)
    visibility = View.INVISIBLE
    iv_splash.visibility = View.INVISIBLE

    initAnim()
  }

  private fun initAnim() {
    mViewIn = AnimationUtils.loadAnimation(context, R.anim.vip_enter_view)
    mSplashIn = AnimationUtils.loadAnimation(context, R.anim.vip_enter_splash)

    mViewIn.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        post {
          iv_splash.startAnimation(mSplashIn)
        }
      }

      override fun onAnimationStart(animation: Animation?) {
        visibility = View.VISIBLE
      }
    })

    mSplashIn.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {

      }

      override fun onAnimationEnd(animation: Animation?) {
        iv_splash.visibility = View.INVISIBLE

        postDelayed({
          visibility = View.INVISIBLE
          isInAnim = false

          if (mList.size > 0) {
            val profile = mList.removeAt(0)
            showVipEnter(profile)
          }
        }, 2000)
      }

      override fun onAnimationStart(animation: Animation?) {
        iv_splash.visibility = View.VISIBLE
      }
    })
  }

  fun showVipEnter(userProfile: TIMUserProfile) {
    if (isInAnim) {
      mList.add(userProfile)
    } else {
      isInAnim = true
      bindData(userProfile)
      startAnim()
    }
  }

  private fun startAnim() {
    startAnimation(mViewIn)
  }

  @SuppressLint("SetTextI18n")
  private fun bindData(userProfile: TIMUserProfile) {
    var nickName = userProfile.nickName
    if (nickName.isNullOrEmpty()) {
      nickName = userProfile.identifier
    }
    tv_user_name.text = "${nickName}进入房间"
  }
}