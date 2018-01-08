package com.kop.fastlive.utils.callblack

import java.util.WeakHashMap

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/28 16:55
 */
class CallbackManager {

  private val weakHashMap = WeakHashMap<Any, IGlobalCallback<Any>>()

  private object Holder {
    val INSTANCE = CallbackManager()
  }

  companion object {
    fun getInstance(): CallbackManager {
      return Holder.INSTANCE
    }
  }

  fun addCallback(tag: Any, callback: IGlobalCallback<Any>): CallbackManager {
    weakHashMap.put(tag, callback)
    return this
  }

  fun getCallback(tag: Any): IGlobalCallback<Any>? {
    return weakHashMap[tag]
  }
}