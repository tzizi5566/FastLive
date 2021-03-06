package com.kop.fastlive.widget

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import kotlinx.android.synthetic.main.view_chat_msg_list.view.rv_view

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/4 13:47
 */
class ChatMsgRecyclerView : RelativeLayout {

  private var mAdapter: ChatMsgAdapter? = null

  constructor(context: Context?) : super(context) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  private fun init() {
    LayoutInflater.from(context).inflate(R.layout.view_chat_msg_list, this, true)
    initAdapter()
  }

  private fun initAdapter() {
    mAdapter = ChatMsgAdapter(context)
    rv_view.layoutManager = LinearLayoutManager(context)
    rv_view.adapter = mAdapter
    mAdapter?.setOnItemClickListener { chatMsgInfo, _ ->
      val ids = listOf(chatMsgInfo.userId)
      TIMFriendshipManager.getInstance().getUsersProfile(ids,
          object : TIMValueCallBack<List<TIMUserProfile>> {
            override fun onSuccess(timUserProfiles: List<TIMUserProfile>) {
              with(context as Activity) {
                val userInfoDialog = UserInfoDialog(this, timUserProfiles[0])
                userInfoDialog.show()
              }
            }

            override fun onError(i: Int, s: String) {

            }
          })
    }
  }

  fun addMsgInfos(infos: ChatMsgInfo) {
    mAdapter!!.setData(infos)
    rv_view.smoothScrollToPosition(mAdapter!!.itemCount)
  }

  fun addMsgInfos(infos: MutableList<ChatMsgInfo>?) {
    if (infos != null) {
      mAdapter!!.setData(infos)
      rv_view.smoothScrollToPosition(mAdapter!!.itemCount)
    }
  }
}