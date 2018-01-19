package com.kop.fastlive.widget

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.kop.fastlive.R

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/19 13:52
 */
class HostControlDialog(val activity: Activity) : TransParentDialog(activity), OnClickListener {

  interface OnControlClickListener {

    fun onBeautyClick()

    fun onFlashClick()

    fun onVoiceClick()

    fun onCameraClick()

    fun onDialogDismiss()
  }

  private var listener: OnControlClickListener? = null

  fun setOnControlClickListener(l: OnControlClickListener) {
    listener = l
  }

  private var mDialogWidth = 0
  private var mDialogHeight = 0

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_host_control, null, false)
    setContentView(view)

    val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    view.measure(width, height)
    mDialogWidth = view.measuredWidth
    mDialogHeight = view.measuredHeight
    setWidthAndHeight(mDialogWidth, mDialogHeight)

    dialog.setOnDismissListener {
      listener?.onDialogDismiss()
    }
  }

  fun showAtTop(view: View) {
    val ints = IntArray(2)
    view.getLocationOnScreen(ints)

    val dialogX = ints[0] - (mDialogWidth - view.width) / 2
    val dialogY = ints[1] - mDialogHeight - view.height

    val window = dialog.window
    val params = window.attributes
    params.x = dialogX
    params.y = dialogY
    params.alpha = 0.7f
    window.attributes = params
    window.setGravity(Gravity.START or Gravity.TOP)
    show()
  }

  override fun onClick(v: View?) {
    listener?.let {
      when (v?.id) {
        R.id.tv_beauty -> it.onBeautyClick()
        R.id.tv_flash_light -> it.onFlashClick()
        R.id.tv_voice -> it.onVoiceClick()
        R.id.tv_camera -> it.onCameraClick()
        else -> {

        }
      }
      hide()
    }
  }
}