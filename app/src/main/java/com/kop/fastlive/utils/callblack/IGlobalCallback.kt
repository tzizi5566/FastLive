package com.kop.fastlive.utils.callblack

import android.support.annotation.Nullable

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/28 16:56
 */
interface IGlobalCallback<in T> {

  fun executeCallback(@Nullable args: T)
}