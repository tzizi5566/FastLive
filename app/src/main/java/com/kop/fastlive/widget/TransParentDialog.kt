package com.kop.fastlive.widget

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.kop.fastlive.R

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/21 14:41
 */
open class TransParentDialog(activity: Activity) {

  var dialog: Dialog = Dialog(activity, R.style.dialog)

  fun setContentView(view: View) {
    dialog.setContentView(view)
  }

  fun setWidthAndHeight(width: Int, height: Int) {
    val win = dialog.window
    val params = win!!.attributes
    if (params != null) {
      params.width = width//设置宽度
      params.height = height//设置高度
      win.attributes = params
    }
  }

  fun show() {
    dialog.show()
  }

  fun hide() {
    dialog.hide()
  }
}