package com.kop.fastlive.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import kotlinx.android.synthetic.main.view_danmu.view.danmu_view_0
import kotlinx.android.synthetic.main.view_danmu.view.danmu_view_1
import kotlinx.android.synthetic.main.view_danmu.view.danmu_view_2
import kotlinx.android.synthetic.main.view_danmu.view.danmu_view_3
import java.util.LinkedList

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/6 14:26
 */
class DanmuView : LinearLayoutCompat, DanmuItemView.OnAvaliableListener {

  private var mList = LinkedList<ChatMsgInfo>()

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
    LayoutInflater.from(context).inflate(R.layout.view_danmu, this, true)

    danmu_view_0.visibility = View.INVISIBLE
    danmu_view_1.visibility = View.INVISIBLE
    danmu_view_2.visibility = View.INVISIBLE
    danmu_view_3.visibility = View.INVISIBLE

    danmu_view_0.setOnAvaliableListener(this)
    danmu_view_1.setOnAvaliableListener(this)
    danmu_view_2.setOnAvaliableListener(this)
    danmu_view_3.setOnAvaliableListener(this)
  }

  fun addMsgInfos(danmuInfo: ChatMsgInfo) {
    synchronized(this) {
      val avaliableItemView = getAvaliableItemView()
      avaliableItemView?.showMsg(danmuInfo) ?: mList.add(danmuInfo)
    }
  }

  private fun getAvaliableItemView(): DanmuItemView? {
    //获取可用的item view
    if (danmu_view_0.visibility != View.VISIBLE) {
      return danmu_view_0
    }
    if (danmu_view_1.visibility != View.VISIBLE) {
      return danmu_view_1
    }
    if (danmu_view_2.visibility != View.VISIBLE) {
      return danmu_view_2
    }
    if (danmu_view_3.visibility != View.VISIBLE) {
      return danmu_view_3
    }
    return null
  }

  override fun onAvaliable() {
    var chatMsgInfo: ChatMsgInfo? = null
    synchronized(this) {
      chatMsgInfo = mList.poll()
    }
    if (chatMsgInfo != null) {
      addMsgInfos(chatMsgInfo!!)
    }
  }
}