package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.utils.ImgUtil
import kotlinx.android.synthetic.main.item_chat_msg_list.view.iv_sender_avatar
import kotlinx.android.synthetic.main.item_chat_msg_list.view.tv_chat_content

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/4 13:56
 */
class ChatMsgAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val mContext = context
  private var mList = mutableListOf<ChatMsgInfo>()
  private var mListener: ((View, Int) -> Unit)? = null
  private var mPositionStart = 0

  fun setData(msgInfo: ChatMsgInfo) {
    mPositionStart = mList.size
    mList.add(msgInfo)
    notifyItemRangeChanged(mPositionStart, mList.size)
  }

  fun setData(list: MutableList<ChatMsgInfo>) {
    mPositionStart = mList.size
    mList.addAll(list)
    notifyItemRangeInserted(mPositionStart, mList.size)
  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
    return ChatMsgViewHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.item_chat_msg_list, parent, false), mListener)
  }

  override fun getItemCount() = mList.size

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
    val chatMsgInfo = mList[position]
    with(holder as ChatMsgViewHolder) {
      val avatarUrl = chatMsgInfo.avatar
      if (avatarUrl.isEmpty()) {
        ImgUtil.loadRound(mContext, R.drawable.default_avatar, itemView.iv_sender_avatar)
      } else {
        ImgUtil.loadRound(mContext, avatarUrl, itemView.iv_sender_avatar)
      }
      itemView.tv_chat_content.text = chatMsgInfo.text
    }
  }

  fun setOnItemClickListener(listener: ((View, Int) -> Unit)?) {
    mListener = listener
  }

  inner class ChatMsgViewHolder(itemView: View,
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