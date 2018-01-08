package com.kop.fastlive.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * 功    能: Glide工具类
 * 创 建 人: KOP
 * 创建日期: 2017/12/20 17:50
 */
class ImgUtil {

  companion object {
    private val OPTIONS = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .dontTransform()

    private val ROUND_OPTIONS = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .dontTransform()
        .apply(RequestOptions.circleCropTransform())

    fun load(context: Context, url: String, targetView: ImageView) {
      Glide.with(context)
          .load(url)
          .apply(OPTIONS)
          .into(targetView)
    }

    fun load(context: Context, resId: Int, targetView: ImageView) {
      Glide.with(context)
          .load(resId)
          .apply(OPTIONS)
          .into(targetView)
    }

    fun loadRound(context: Context, url: String, targetView: ImageView) {
      Glide.with(context)
          .load(url)
          .apply(ROUND_OPTIONS)
          .into(targetView)
    }

    fun loadRound(context: Context, resId: Int, targetView: ImageView) {
      Glide.with(context)
          .load(resId)
          .apply(ROUND_OPTIONS)
          .into(targetView)
    }
  }
}