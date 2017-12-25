package com.kop.fastlive.module.editprofile

import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.RadioButton
import com.kop.fastlive.R
import com.kop.fastlive.widget.TransParentDialog
import kotlinx.android.synthetic.main.dialog_edit_gender.view.rb_female
import kotlinx.android.synthetic.main.dialog_edit_gender.view.rb_male
import kotlinx.android.synthetic.main.dialog_edit_gender.view.tv_ok

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/25 13:10
 */
class EditGenderDialog(activity: Activity) : TransParentDialog(activity) {

  private var maleView: RadioButton? = null
  private var femaleView: RadioButton? = null

  interface OnChangeGenderListener {
    fun onChangeGender(isMale: Boolean)
  }

  private var onChangeGenderListener: OnChangeGenderListener? = null

  fun setOnChangeGenderListener(l: OnChangeGenderListener) {
    onChangeGenderListener = l
  }

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_gender, null, false)
    maleView = view.rb_male
    femaleView = view.rb_female
    view.tv_ok.setOnClickListener({
      val isMaleChecked = maleView?.isChecked
      isMaleChecked?.let { checked -> onChangeGenderListener?.onChangeGender(checked) }
      hide()
    })

    setContentView(view)

    setWidthAndHeight(activity.window.decorView.width * 80 / 100,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  fun show(isMale: Boolean) {
    maleView?.isChecked = isMale
    femaleView?.isChecked = !isMale
    show()
  }
}