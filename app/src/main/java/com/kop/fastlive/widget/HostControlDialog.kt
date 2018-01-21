package com.kop.fastlive.widget

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.kop.fastlive.R
import kotlinx.android.synthetic.main.dialog_host_control.view.tv_beauty
import kotlinx.android.synthetic.main.dialog_host_control.view.tv_camera
import kotlinx.android.synthetic.main.dialog_host_control.view.tv_flash_light
import kotlinx.android.synthetic.main.dialog_host_control.view.tv_voice

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

  private var mView = LayoutInflater.from(activity).inflate(R.layout.dialog_host_control, null,
      false)
  private var mDialogWidth = 0
  private var mDialogHeight = 0

  init {
    setContentView(mView)

    val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    mView.measure(width, height)
    mDialogWidth = mView.measuredWidth
    mDialogHeight = mView.measuredHeight
    setWidthAndHeight(mDialogWidth, mDialogHeight)

    mView.tv_beauty.setOnClickListener(this)
    mView.tv_flash_light.setOnClickListener(this)
    mView.tv_voice.setOnClickListener(this)
    mView.tv_camera.setOnClickListener(this)

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

  fun setStatus(beautyOn: Boolean, voidOn: Boolean, flashOn: Boolean) {
    val beautyResId = if (beautyOn) R.drawable.icon_beauty_on else R.drawable.icon_beauty_off
    mView.tv_beauty.setCompoundDrawablesWithIntrinsicBounds(beautyResId, 0, 0, 0)
    mView.tv_beauty.text = if (beautyOn) "关美颜" else "开美颜"

    val voidResId = if (voidOn) R.drawable.icon_mic_on else R.drawable.icon_mic_off
    mView.tv_voice.setCompoundDrawablesWithIntrinsicBounds(voidResId, 0, 0, 0)
    mView.tv_voice.text = if (beautyOn) "关声音" else "开声音"

    val flashResId = if (flashOn) R.drawable.icon_flashlight_on else R.drawable.icon_flashlight_off
    mView.tv_flash_light.setCompoundDrawablesWithIntrinsicBounds(flashResId, 0, 0, 0)
    mView.tv_flash_light.text = if (beautyOn) "关闪光" else "开闪光"
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