package com.kop.fastlive.module.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.blankj.utilcode.util.SPUtils
import com.gyf.barlibrary.ImmersionBar
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.module.editprofile.EditProfileActivity
import com.kop.fastlive.module.main.MainActivity
import com.kop.fastlive.module.register.RegisterActivity
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.core.ILiveLoginManager
import kotlinx.android.synthetic.main.activity_login.btn_login
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.edt_password
import kotlinx.android.synthetic.main.activity_login.edt_user_name


class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    ImmersionBar.with(this)
        .statusBarDarkFont(true, 0.2f)
        .init()

    registerListener()
  }

  private fun registerListener() {
    btn_login.setOnClickListener({
      login()
    })

    btn_register.setOnClickListener({
      register()
    })
  }

  private fun register() {
    startActivity(Intent(this, RegisterActivity::class.java))
  }

  private fun login() {
    val userName = edt_user_name.text.toString()
    val password = edt_password.text.toString()
    if (userName.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "用户名或密码为空！", Toast.LENGTH_SHORT).show()
      return
    }

    ILiveLoginManager.getInstance().tlsLogin(userName, password, object : ILiveCallBack<String> {
      override fun onSuccess(data: String?) {
        loginLive(userName, password)
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@LoginActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun loginLive(userName: String, password: String) {
    ILiveLoginManager.getInstance().iLiveLogin(userName, password, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any?) {
        getSelfProfile()

        Toast.makeText(this@LoginActivity, "登录成功！", Toast.LENGTH_SHORT).show()
        val spUtils = SPUtils.getInstance("FirstLogin")
        val isFirst = spUtils.getBoolean("firstLogin", true)
        if (isFirst) {
          startActivity(Intent(this@LoginActivity, EditProfileActivity::class.java))
        } else {
          startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@LoginActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
      }

    })
  }

  private fun getSelfProfile() {
    TIMFriendshipManager.getInstance().getSelfProfile(object : TIMValueCallBack<TIMUserProfile> {
      override fun onSuccess(p0: TIMUserProfile?) {
        p0?.let {
          (application as MyApplication).setUserProfile(it)
        }
      }

      override fun onError(p0: Int, p1: String?) {
        Toast.makeText(this@LoginActivity, "获取个人信息失败！$p1", Toast.LENGTH_SHORT).show()
      }
    })
  }
}
