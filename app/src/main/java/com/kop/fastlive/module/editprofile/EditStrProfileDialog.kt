package com.kop.fastlive.module.editprofile

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.kop.fastlive.R
import com.kop.fastlive.widget.TransParentDialog
import kotlinx.android.synthetic.main.dialog_edit_str_profile.view.edt_content
import kotlinx.android.synthetic.main.dialog_edit_str_profile.view.tv_ok
import kotlinx.android.synthetic.main.dialog_edit_str_profile.view.tv_title

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/21 14:38
 */
class EditStrProfileDialog(activity: Activity) : TransParentDialog(activity) {

  private var mainView: View? = null
  private var mTitle: String? = null

  interface OnOKListener {
    fun onOk(title: String, content: String)
  }

  private var onOKListener: OnOKListener? = null

  fun setOnOKListener(l: OnOKListener) {
    onOKListener = l
  }

  init {
    mainView = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_str_profile, null,
        false)
    mainView?.let {
      setContentView(mainView!!)

      setWidthAndHeight(activity.window.decorView.width * 80 / 100,
          WindowManager.LayoutParams.WRAP_CONTENT)
      mainView!!.tv_ok.setOnClickListener({
        val content = mainView!!.edt_content.text.toString()
        onOKListener?.let {
          onOKListener!!.onOk(mTitle!!, content)
        }
        hide()
      })
    }
  }

  fun show(title: String, resId: Int, defaultContent: String) {
    mTitle = title
    mainView!!.tv_title.text = "请输入$title"

    mainView!!.edt_content.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
    mainView!!.edt_content.setText(defaultContent)
    show()
  }
}