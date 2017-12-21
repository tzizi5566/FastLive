package com.kop.fastlive.module.editprofile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.kop.fastlive.R
import com.kop.fastlive.module.editprofile.EditStrProfileDialog.OnOKListener
import com.kop.fastlive.utils.ImgUtil
import com.tencent.TIMCallBack
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import kotlinx.android.synthetic.main.activity_edit_profile.btn_complete
import kotlinx.android.synthetic.main.activity_edit_profile.iv_avatar
import kotlinx.android.synthetic.main.activity_edit_profile.ll_avatar
import kotlinx.android.synthetic.main.activity_edit_profile.ll_gender
import kotlinx.android.synthetic.main.activity_edit_profile.ll_get_nums
import kotlinx.android.synthetic.main.activity_edit_profile.ll_id
import kotlinx.android.synthetic.main.activity_edit_profile.ll_level
import kotlinx.android.synthetic.main.activity_edit_profile.ll_location
import kotlinx.android.synthetic.main.activity_edit_profile.ll_nick_name
import kotlinx.android.synthetic.main.activity_edit_profile.ll_renzheng
import kotlinx.android.synthetic.main.activity_edit_profile.ll_send_nums
import kotlinx.android.synthetic.main.activity_edit_profile.ll_sign

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_profile)

    setIconKey()
    registerListener()
    getSelfInfo()
  }

  private fun setIconKey() {
    ll_nick_name.set(R.drawable.ic_info_nickname, "昵称", "")
    ll_gender.set(R.drawable.ic_info_gender, "性别", "")
    ll_sign.set(R.drawable.ic_info_sign, "签名", "无")
    ll_renzheng.set(R.drawable.ic_info_renzhen, "认证", "未知")
    ll_location.set(R.drawable.ic_info_location, "地区", "未知")
    ll_id.set(R.drawable.ic_info_id, "ID", "")
    ll_level.set(R.drawable.ic_info_level, "等级", "0")
    ll_get_nums.set(R.drawable.ic_info_get, "获得票数", "0")
    ll_send_nums.set(R.drawable.ic_info_send, "送出票数", "0")
  }

  private fun registerListener() {
    ll_avatar.setOnClickListener(this)
    ll_nick_name.setOnClickListener(this)
    ll_gender.setOnClickListener(this)
    ll_sign.setOnClickListener(this)
    ll_renzheng.setOnClickListener(this)
    ll_location.setOnClickListener(this)
    btn_complete.setOnClickListener(this)
  }

  private fun getSelfInfo() {
    TIMFriendshipManager.getInstance().getSelfProfile(object : TIMValueCallBack<TIMUserProfile> {
      override fun onSuccess(timUserProfile: TIMUserProfile) {
        //获取自己信息成功
        updateView(timUserProfile)
      }

      override fun onError(i: Int, s: String) {
        Toast.makeText(this@EditProfileActivity, "获取信息失败：$s", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun updateView(timUserProfile: TIMUserProfile) {
    val faceUrl = timUserProfile.faceUrl
    if (TextUtils.isEmpty(faceUrl)) {
      ImgUtil.loadRound(this, R.drawable.default_avatar, iv_avatar)
    } else {
      ImgUtil.loadRound(this, faceUrl, iv_avatar)
    }

    ll_nick_name.updateValue(timUserProfile.nickName)
    val value = timUserProfile.gender.value.toInt()
    val genderStr = if (value == 1) "男" else "女"
    ll_gender.updateValue(genderStr)
    ll_sign.updateValue(timUserProfile.selfSignature)
    ll_location.updateValue(timUserProfile.location)
    ll_id.updateValue(timUserProfile.identifier)

    val customInfo = timUserProfile.customInfo
    ll_renzheng.updateValue(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知"))
    ll_level.updateValue(getValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0"))
    ll_get_nums.updateValue(getValue(customInfo, CustomProfile.CUSTOM_GET, "0"))
    ll_send_nums.updateValue(getValue(customInfo, CustomProfile.CUSTOM_SEND, "0"))
  }

  private fun getValue(customInfo: Map<String, ByteArray>?, key: String,
      defaultValue: String): String {
    customInfo?.let {
      val valueBytes = customInfo[key]
      if (valueBytes != null) {
        return String(valueBytes)
      }
    }
    return defaultValue
  }

  override fun onClick(v: View?) {
    when (v!!.id) {
      ll_avatar.id -> choosePic()
      ll_nick_name.id -> showEditNickNameDialog()
      ll_gender.id -> showEditGenderDialog()
      ll_sign.id -> showEditSignDialog()
      ll_renzheng.id -> showEditRenzhengDialog()
      ll_location.id -> showEditLocationDialog()
//      btn_complete.id -> startActivity(Intent(this@EditProfileActivity, LoginActivity::class.java))
    }
  }

  private fun showEditLocationDialog() {

  }

  private fun showEditRenzhengDialog() {

  }

  private fun showEditSignDialog() {
    val dialog = EditStrProfileDialog(this)
    dialog.setOnOKListener(object : OnOKListener {
      override fun onOk(title: String, content: String) {
        TIMFriendshipManager.getInstance().setSelfSignature(content, object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(this@EditProfileActivity, "更新签名失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show("签名", R.drawable.ic_info_sign, ll_sign.getValue())
  }

  private fun showEditGenderDialog() {

  }

  private fun showEditNickNameDialog() {

  }

  private fun choosePic() {

  }
}
