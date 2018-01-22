package com.kop.fastlive.widget

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.kop.fastlive.R
import kotlinx.android.synthetic.main.dialog_pic_choose.view.tv_album
import kotlinx.android.synthetic.main.dialog_pic_choose.view.tv_camera

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/25 13:52
 */
class PicChooseDialog(activity: Activity) : TransParentDialog(activity) {

  interface OnDialogClickListener {
    fun onCamera()

    fun onAlbum()
  }

  private var onDialogClickListener: OnDialogClickListener? = null

  fun setOnDialogClickListener(l: OnDialogClickListener) {
    onDialogClickListener = l
  }

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_pic_choose, null, false)
    setContentView(view)
    setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)

    val camera = view.tv_camera
    val picLib = view.tv_album

    camera.setOnClickListener {
      hide()
      onDialogClickListener?.onCamera()
    }

    picLib.setOnClickListener {
      hide()
      onDialogClickListener?.onAlbum()
    }
  }

  override fun show() {
    val window = dialog.window
    window.setWindowAnimations(R.style.dialogAnimation)
    val lp = window.attributes
    lp.gravity = Gravity.BOTTOM
    dialog.window.attributes = lp

    super.show()
  }
}