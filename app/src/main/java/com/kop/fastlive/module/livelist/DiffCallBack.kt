package com.kop.fastlive.module.livelist

import android.support.v7.util.DiffUtil
import com.avos.avoscloud.AVObject

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/1 17:24
 */
class DiffCallBack(oldList: List<AVObject>, newList: List<AVObject>) : DiffUtil.Callback() {

  private var mOldList = oldList
  private var mNewList = newList

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return mOldList[oldItemPosition].getString("objectId") ==
        mNewList[newItemPosition].getString("objectId")
  }

  override fun getOldListSize(): Int {
    return mOldList.size
  }

  override fun getNewListSize(): Int {
    return mNewList.size
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val beanOld = mOldList[oldItemPosition]
    val beanNew = mNewList[newItemPosition]
    if (beanOld.getString("room_id") != beanNew.getString("room_id")) {
      return false//如果有内容不同，就返回false
    }
    if (beanOld.getString("user_id") != beanNew.getString("user_id")) {
      return false//如果有内容不同，就返回false
    }
    return true//默认两个data内容是相同的
  }
}