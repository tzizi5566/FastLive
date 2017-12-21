package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kop.fastlive.R
import kotlinx.android.synthetic.main.view_profile_edit.view.profile_icon
import kotlinx.android.synthetic.main.view_profile_edit.view.profile_key
import kotlinx.android.synthetic.main.view_profile_edit.view.profile_value
import kotlinx.android.synthetic.main.view_profile_edit.view.right_arrow

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/20 15:39
 */
open class ProfileEdit : LinearLayoutCompat {

  constructor(context: Context?) : super(context) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  private fun init() {
    LayoutInflater.from(context).inflate(R.layout.view_profile_edit, this, true)
  }

  fun set(iconResId: Int, key: String, value: String) {
    profile_icon.setImageResource(iconResId)
    profile_key.text = key
    profile_value.text = value
  }

  fun updateValue(value: String) {
    profile_value.text = value
  }

  fun getValue(): String {
    return profile_value.text.toString()
  }

  protected fun disableEdit() {
    right_arrow.visibility = View.GONE
  }
}