package com.kop.fastlive.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kop.fastlive.R
import com.kop.fastlive.register.RegisterActivity
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

    loginActurally(userName, password)
  }

  private fun loginActurally(userName: String, password: String) {
    ILiveLoginManager.getInstance().tlsLoginAll(userName, password, object : ILiveCallBack<Int> {
      override fun onSuccess(data: Int?) {
        ILiveLoginManager.getInstance().iLiveLogin(userName, password,
            object : ILiveCallBack<Int> {
              override fun onSuccess(data: Int?) {
                Toast.makeText(this@LoginActivity, "登录成功！", Toast.LENGTH_SHORT).show()
              }

              override fun onError(module: String?, errCode: Int, errMsg: String?) {
                Toast.makeText(this@LoginActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
              }

            })
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@LoginActivity, "登录失败！$errMsg", Toast.LENGTH_SHORT).show()
      }
    })
  }
}
