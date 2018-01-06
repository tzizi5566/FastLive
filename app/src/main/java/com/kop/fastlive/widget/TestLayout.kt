package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.ContentFrameLayout
import android.util.AttributeSet

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/6 17:10
 */
class TestLayout : ContentFrameLayout {

  private var mL: Int = 0
  private var mT: Int = 0
  private var mR: Int = 0
  private var mB: Int = 0
  private var isFist = true

  constructor(context: Context?) : super(context)

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (isFist) {
      mL = left
      mT = top
      mR = right
      mB = bottom
      isFist = false
    }
    getChildAt(0).layout(mL, mT, mR, mB);
  }
}