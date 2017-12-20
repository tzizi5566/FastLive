package com.kop.fastlive.register

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kop.fastlive.R
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
    ILiveLoginManager.getInstance().tlsRegister(userName, password, object : ILiveCallBack<Int> {
      override fun onSuccess(data: Int) {
        Toast.makeText(this@RegisterActivity, "创建用户成功！请登录！", Toast.LENGTH_SHORT).show()
        finish()
      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
        Toast.makeText(this@RegisterActivity, "创建用户失败！$errMsg", Toast.LENGTH_SHORT).show()
      }
    })
  }
}
