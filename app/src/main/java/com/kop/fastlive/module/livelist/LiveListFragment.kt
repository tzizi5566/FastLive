package com.kop.fastlive.module.livelist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.kop.fastlive.R
import com.kop.fastlive.module.watcher.WatcherLiveActivity
import kotlinx.android.synthetic.main.fragment_live_list.rv_view
import kotlinx.android.synthetic.main.fragment_live_list.sr_layout


/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/26 16:53
 */
class LiveListFragment : Fragment() {

  private var mList = mutableListOf<AVObject>()
  private var mAdapter: LiveListAdapter? = null

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_live_list, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    registerListener()
    setAdapter()
    getLiveListData()
  }

  private fun getLiveListData() {
    val avQuery = AVQuery<AVObject>("RoomInfo")
    avQuery.limit(20)
    avQuery.findInBackground(object : FindCallback<AVObject>() {
      override fun done(p0: MutableList<AVObject>?, p1: AVException?) {
        if (p1 == null) {
          mAdapter?.setData(p0!!)
          mList = p0!!
        } else {
          Toast.makeText(activity, "获取数据失败 ${p1.message}", Toast.LENGTH_SHORT).show()
        }
        sr_layout?.isRefreshing = false
      }
    })
  }

  private fun setAdapter() {
    mAdapter = LiveListAdapter(activity)
    rv_view.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    rv_view.adapter = mAdapter
    mAdapter?.setOnItemClickListener { _, i ->
      val intent = Intent(activity, WatcherLiveActivity::class.java)
      intent.putExtra("roomId", mList[i].getString("room_id"))
      intent.putExtra("hostId", mList[i].getString("user_id"))
      startActivity(intent)
    }
  }

  private fun registerListener() {
    sr_layout.setColorSchemeResources(
        android.R.color.holo_blue_bright,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light
    )

    sr_layout.setProgressViewOffset(true, -40, 80)

    sr_layout.setOnRefreshListener({
      getLiveListData()
    })
  }
}