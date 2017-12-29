package com.kop.fastlive.module.editprofile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.SPUtils
import com.kop.fastlive.R
import com.kop.fastlive.choosePicWithPermissionCheck
import com.kop.fastlive.module.main.MainActivity
import com.kop.fastlive.utils.ImgUtil
import com.kop.fastlive.utils.callblack.CallbackManager
import com.kop.fastlive.utils.callblack.CallbackType
import com.kop.fastlive.utils.callblack.IGlobalCallback
import com.tencent.TIMCallBack
import com.tencent.TIMFriendGenderType
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import kotlinx.android.synthetic.main.fragment_edit_profile.btn_complete
import kotlinx.android.synthetic.main.fragment_edit_profile.iv_avatar
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_avatar
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_gender
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_get_nums
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_id
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_level
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_location
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_nick_name
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_renzheng
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_send_nums
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_sign

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/26 17:12
 */
class EditProfileFragment : Fragment(), View.OnClickListener {

  companion object {
    fun newInstance(): EditProfileFragment {
      return EditProfileFragment()
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_edit_profile, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val spUtils = SPUtils.getInstance("FirstLogin")
    spUtils.put("firstLogin", false)

    setIconKey()
    registerListener()
    getSelfInfo()

    CallbackManager
        .getInstance()
        .addCallback(CallbackType.CHOOSE_PIC_AVATAR, object : IGlobalCallback<Any> {
          override fun executeCallback(args: Any) {
            updateAvatar(args.toString())
          }
        })
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
        Toast.makeText(activity, "获取信息失败：$s", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun updateView(timUserProfile: TIMUserProfile) {
    val faceUrl = timUserProfile.faceUrl
    if (TextUtils.isEmpty(faceUrl)) {
      ImgUtil.loadRound(activity, R.drawable.default_avatar, iv_avatar)
    } else {
      ImgUtil.loadRound(activity, faceUrl, iv_avatar)
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

  private fun getValue(customInfo: Map<String, ByteArray>, key: String, default: String): String {
    val valueBytes = customInfo[key]
    return valueBytes?.toString() ?: default
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      ll_avatar.id -> {
        if (activity is EditProfileActivity) {
          (activity as EditProfileActivity).choosePicWithPermissionCheck()
        } else if (activity is MainActivity) {
          (activity as MainActivity).choosePicWithPermissionCheck()
        }
      }
      ll_nick_name.id -> showEditNickNameDialog()
      ll_gender.id -> showEditGenderDialog()
      ll_sign.id -> showEditSignDialog()
      ll_renzheng.id -> showEditRenzhengDialog()
      ll_location.id -> showEditLocationDialog()
      btn_complete.id -> {
        if (activity is EditProfileActivity) {
          val intent = Intent(activity, MainActivity::class.java)
          startActivity(intent)
          activity.finish()
        } else if (activity is MainActivity) {

        }
      }
    }
  }

  private fun showEditLocationDialog() {
    val dialog = EditStrProfileDialog(activity)
    dialog.setOnOKListener(object : EditStrProfileDialog.OnOKListener {
      override fun onOk(title: String, content: String) {
        TIMFriendshipManager.getInstance().setLocation(content, object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(activity, "更新地区失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show("地区", R.drawable.ic_info_location, ll_location.getValue())
  }

  private fun showEditRenzhengDialog() {
    val dialog = EditStrProfileDialog(activity)
    dialog.setOnOKListener(object : EditStrProfileDialog.OnOKListener {
      override fun onOk(title: String, content: String) {
        TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_RENZHENG,
            content.toByteArray(), object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(activity, "更新认证失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show("认证", R.drawable.ic_info_renzhen, ll_renzheng.getValue())
  }

  private fun showEditSignDialog() {
    val dialog = EditStrProfileDialog(activity)
    dialog.setOnOKListener(object : EditStrProfileDialog.OnOKListener {
      override fun onOk(title: String, content: String) {
        TIMFriendshipManager.getInstance().setSelfSignature(content, object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(activity, "更新签名失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show("签名", R.drawable.ic_info_sign, ll_sign.getValue())
  }

  private fun showEditGenderDialog() {
    val dialog = EditGenderDialog(activity)
    dialog.setOnChangeGenderListener(object : EditGenderDialog.OnChangeGenderListener {
      override fun onChangeGender(isMale: Boolean) {
        val gender = if (isMale) TIMFriendGenderType.Male else TIMFriendGenderType.Female
        TIMFriendshipManager.getInstance().setGender(gender, object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(activity, "更新性别失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show(ll_gender.getValue() == "男")
  }

  private fun showEditNickNameDialog() {
    val dialog = EditStrProfileDialog(activity)
    dialog.setOnOKListener(object : EditStrProfileDialog.OnOKListener {
      override fun onOk(title: String, content: String) {
        TIMFriendshipManager.getInstance().setNickName(content, object : TIMCallBack {
          override fun onSuccess() {
            getSelfInfo()
          }

          override fun onError(i: Int, s: String) {
            Toast.makeText(activity, "更新昵称失败：$s", Toast.LENGTH_SHORT).show()
          }
        })
      }
    })
    dialog.show("昵称", R.drawable.ic_info_nickname, ll_nick_name.getValue())
  }

  private fun updateAvatar(url: String) {
    TIMFriendshipManager.getInstance().setFaceUrl(url, object : TIMCallBack {
      override fun onSuccess() {
        getSelfInfo()
      }

      override fun onError(i: Int, s: String) {
        Toast.makeText(activity, "头像更新失败：" + s, Toast.LENGTH_SHORT).show()
      }
    })
  }
}