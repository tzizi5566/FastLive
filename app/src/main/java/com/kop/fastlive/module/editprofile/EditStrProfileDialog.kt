package com.kop.fastlive.module.editprofile

import android.app.Activity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
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

  private var titleView: AppCompatTextView? = null
  private var contentView: AppCompatEditText? = null
  private var mTitle: String? = null

  interface OnOKListener {
    fun onOk(title: String, content: String)
  }

  private var onOKListener: OnOKListener? = null

  fun setOnOKListener(l: OnOKListener) {
    onOKListener = l
  }

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_str_profile, null,
        false)
    titleView = view.tv_title
    contentView = view.edt_content
    view.tv_ok.setOnClickListener({
      val content = contentView?.text.toString()
      mTitle?.let { str -> onOKListener?.onOk(str, content) }
      hide()
    })

    setContentView(view)

    setWidthAndHeight(activity.window.decorView.width * 80 / 100,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  fun show(title: String, resId: Int, defaultContent: String) {
    mTitle = title
    titleView?.text = "请输入$title"

    contentView?.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
    contentView?.setText(defaultContent)
    contentView?.setSelection(defaultContent.length)
    show()
  }
}