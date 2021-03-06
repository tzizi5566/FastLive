package com.kop.fastlive.module.livelist

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avos.avoscloud.AVObject
import com.kop.fastlive.R
import com.kop.fastlive.utils.ImgUtil
import kotlinx.android.synthetic.main.item_live_list.view.iv_avatar
import kotlinx.android.synthetic.main.item_live_list.view.iv_cover
import kotlinx.android.synthetic.main.item_live_list.view.tv_name
import kotlinx.android.synthetic.main.item_live_list.view.tv_num
import kotlinx.android.synthetic.main.item_live_list.view.tv_title

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/1 15:46
 */
class LiveListAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val mContext = context
  private var mList = mutableListOf<AVObject>()
  private var mListener: ((View, Int) -> Unit)? = null

  fun setData(list: MutableList<AVObject>) {
    if (mList.isEmpty()) {
      mList = list
      notifyItemRangeInserted(0, list.size)
    } else {
      val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return mList[oldItemPosition].getString("objectId") ==
              list[newItemPosition].getString("objectId")
        }

        override fun getOldListSize() = mList.size

        override fun getNewListSize() = list.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          val old = mList[oldItemPosition]
          val new = list[newItemPosition]
          return old.getString("room_id") == new.getString("room_id")
              && old.getString("user_id") == new.getString("user_id")
        }
      })
      mList = list
      diffResult.dispatchUpdatesTo(this)
    }
  }

  override fun getItemCount() = mList.size

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
    val avObject = mList[position]
    with(holder as LiveListViewHolder) {
      val userName = if (avObject.getString("user_name").isNullOrEmpty()) {
        avObject.getString("user_id")
      } else {
        avObject.getString("user_name")
      }
      itemView.tv_name.text = userName

      val liveTitle = avObject.getString("live_title")
      if (liveTitle.isNullOrEmpty()) {
        val title = "$userName 的直播"
        itemView.tv_title.text = title
      } else {
        itemView.tv_title.text = liveTitle
      }

      val url = avObject.getString("live_cover")
      if (url.isNullOrEmpty()) {
        ImgUtil.load(mContext, R.drawable.default_cover, itemView.iv_cover)
      } else {
        ImgUtil.load(mContext, url, itemView.iv_cover)
      }

      val avatar = avObject.getString("user_avatar")
      if (avatar.isNullOrEmpty()) {
        ImgUtil.loadRound(mContext, R.drawable.default_avatar, itemView.iv_avatar)
      } else {
        ImgUtil.loadRound(mContext, avatar, itemView.iv_avatar)
      }

      val watchers = avObject.getInt("watcher_num")
      val watchText = "$watchers 人正在看"
      itemView.tv_num.text = watchText
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
    return LiveListViewHolder(
        LayoutInflater.from(mContext).inflate(R.layout.item_live_list, parent, false), mListener)
  }

  fun setOnItemClickListener(listener: ((View, Int) -> Unit)?) {
    mListener = listener
  }

  inner class LiveListViewHolder(itemView: View,
      listener: ((View, Int) -> Unit)?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val mListener = listener

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
      if (mListener != null && view != null) {
        mListener.invoke(view, layoutPosition)
      }
    }
  }
}