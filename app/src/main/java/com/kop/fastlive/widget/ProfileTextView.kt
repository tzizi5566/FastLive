package com.kop.fastlive.widget

import android.content.Context
import android.util.AttributeSet

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/20 16:20
 */
class ProfileTextView : ProfileEdit {

  constructor(context: Context?) : super(context) {
    disableEdit()
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    disableEdit()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    disableEdit()
  }
}