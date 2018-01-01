package com.kop.fastlive.utils

import java.util.Random


/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/30 15:23
 */
object NumUtil {

  fun getRandomNum(): String {
    val random = Random()
    val builder = StringBuilder()
    for (i in 0..6) {
      builder.append(random.nextInt(10))
    }
    return builder.toString()
  }
}