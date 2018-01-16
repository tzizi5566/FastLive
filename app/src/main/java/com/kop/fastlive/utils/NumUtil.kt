package com.kop.fastlive.utils

import android.graphics.Color
import java.util.Random


/**
 * 功    能: 获得5位随机整数
 * 创 建 人: KOP
 * 创建日期: 2017/12/30 15:23
 */
class NumUtil {

  companion object {

    private val RANDOM = Random()

    fun getRandomNum(): String {
      val random = Random()
      val builder = StringBuilder()
      for (i in 0..6) {
        builder.append(random.nextInt(10))
      }
      return builder.toString()
    }

    fun getRandomColor(): Int {
      return Color.rgb(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255))
    }
  }
}