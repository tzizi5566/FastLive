package com.kop.fastlive.module.register

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.module.editprofile.EditProfileActivity
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.core.ILiveLoginManager
import kotlinx.android.synthetic.main.activity_register.btn_register
import kotlinx.android.synthetic.main.activity_register.edt_password
import kotlinx.android.synthetic.main.activity_register.edt_rePassword
import kotlinx.android.synthetic.main.activity_register.edt_user_name


class RegisterActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)

    registerListener()
  }

  private fun registerListener() {
    btn_register.setOnClickListener({
      register()
    })
  }

  private fun register() {
    val userName = edt_user_name.text.toString()
    val password = edt_password.text.toString()
    val rePassword = edt_rePassword.text.toString()
    if (userName.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
      Toast.makeText(this, "用户名或密码为空！", Toast.LENGTH_SHORT).show()
      return
    }

    if (password != rePassword) {
      Toast.makeText(this, "两次输入密码不一致！", Toast.LENGTH_SHORT).show()
      return
    }

    if (userName.length < 8 || password.length < 8) {
      Toast.makeText(this, "用户名或密码长度不能小于8位！", Toast.LENGTH_SHORT).show()
      return
    }

    registerActurally(userName, password)
  }

  private fun registerActurally(userName: String, password: String) {
    ILiveLoginManager.getInstance().tlsRegister(userName, password, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any?) {
        Toast.makeText(this@RegisterActivity, "创建用户成功！请登录！", Toast.LENGTH_SHORT).show()
        login()
      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
        Toast.makeText(this@RegisterActivity, "创建用户失败！$errMsg", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun login() {
    val userName = edt_user_name.text.toString()
    val password = edt_password.text.toString()

    ILiveLoginManager.getInstance().tlsLogin(userName, password, object : ILiveCallBack<String> {
      override fun onSuccess(data: String?) {
        loginLive(userName, password)
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@RegisterActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun loginLive(userName: String, password: String) {
    ILiveLoginManager.getInstance().iLiveLogin(userName, password, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any?) {
        Toast.makeText(this@RegisterActivity, "登录成功！", Toast.LENGTH_SHORT).show()
        getSelfProfile()
        startActivity(Intent(this@RegisterActivity, EditProfileActivity::class.java))
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@RegisterActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
      }

    })
  }

  private fun getSelfProfile() {
    TIMFriendshipManager.getInstance().getSelfProfile(object : TIMValueCallBack<TIMUserProfile> {
      override fun onSuccess(p0: TIMUserProfile?) {
        p0?.let {
          (application as MyApplication).setSelfProfile(p0)
        }
      }

      override fun onError(p0: Int, p1: String?) {
        Toast.makeText(this@RegisterActivity, "获取个人信息失败！$p1", Toast.LENGTH_SHORT).show()
      }
    })
  }
}
