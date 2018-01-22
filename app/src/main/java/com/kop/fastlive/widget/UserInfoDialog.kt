package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.kop.fastlive.R
import com.kop.fastlive.module.editprofile.CustomProfile
import com.kop.fastlive.utils.ImgUtil
import com.tencent.TIMUserProfile
import kotlinx.android.synthetic.main.dialog_userinfo.view.iv_user_avatar
import kotlinx.android.synthetic.main.dialog_userinfo.view.iv_user_close
import kotlinx.android.synthetic.main.dialog_userinfo.view.iv_user_gender
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_bopiao
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_id
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_level
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_name
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_renzhen
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_sign
import kotlinx.android.synthetic.main.dialog_userinfo.view.tv_user_songchu
import java.text.DecimalFormat

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/22 15:58
 */
class UserInfoDialog(
    val activity: Activity,
    private val userInfo: TIMUserProfile) : TransParentDialog(activity) {

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_userinfo, null, false)
    setContentView(view)
    setWidthAndHeight(activity.window.decorView.width * 80 / 100,
        WindowManager.LayoutParams.WRAP_CONTENT)

    bindDataToViews(view)
    view.iv_user_close.setOnClickListener {
      hide()
    }
  }

  @SuppressLint("SetTextI18n")
  private fun bindDataToViews(view: View) {
    val avatarUrl = userInfo.faceUrl
    if (avatarUrl.isNullOrEmpty()) {
      ImgUtil.loadRound(activity, R.drawable.default_avatar, view.iv_user_avatar)
    } else {
      ImgUtil.loadRound(activity, avatarUrl, view.iv_user_avatar)
    }

    var nickName = userInfo.nickName
    if (nickName.isNullOrEmpty()) {
      nickName = "用户"
    }
    view.tv_user_name.text = nickName

    val genderValue = userInfo.gender.value
    view.iv_user_gender.setImageResource(
        if (genderValue == 1L) R.drawable.ic_male else R.drawable.ic_female)

    view.tv_user_id.text = "ID：${userInfo.identifier}"

    val sign = userInfo.selfSignature
    view.tv_user_sign.text = if (sign.isNullOrEmpty()) "Ta好像忘记写签名了..." else sign

    val customInfo = userInfo.customInfo

    val rezhen = getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知")
    view.tv_user_renzhen.text = rezhen
    val sendNum = Integer.valueOf(getValue(customInfo, CustomProfile.CUSTOM_SEND, "0"))
    view.tv_user_songchu.text = "送出：${formatLargNum(sendNum)}"
    val getNum = Integer.valueOf(getValue(customInfo, CustomProfile.CUSTOM_GET, "0"))
    view.tv_user_bopiao.text = "播票：${formatLargNum(getNum)}"
    val level = getValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0")
    view.tv_user_level.text = level
  }

  private fun getValue(customInfo: Map<String, ByteArray>?, key: String,
      defaultValue: String): String {
    if (customInfo != null) {
      val valueBytes = customInfo[key]
      if (valueBytes != null) {
        return String(valueBytes)
      }
    }
    return defaultValue
  }

  private fun formatLargNum(num: Int): String {
    val wan = num * 1.0f / 10000
    return if (wan < 1) {
      "" + num
    } else {
      DecimalFormat("#.00").format(wan.toDouble()) + "万"
    }
  }
}