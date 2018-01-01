package com.kop.fastlive.module.livelist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
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
import kotlinx.android.synthetic.main.fragment_live_list.rv_view
import kotlinx.android.synthetic.main.fragment_live_list.sr_layout


/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/26 16:53
 */
class LiveListFragment : Fragment() {

  private var mList = listOf<AVObject>()
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
          val diff = DiffUtil.calculateDiff(DiffCallBack(mList, p0!!))
          diff.dispatchUpdatesTo(mAdapter)
          mList = p0
          mAdapter?.setData(mList)
        } else {
          Toast.makeText(activity, "获取数据失败 ${p1.message}", Toast.LENGTH_SHORT).show()
        }
        sr_layout.isRefreshing = false
      }
    })
  }

  private fun setAdapter() {
    mAdapter = LiveListAdapter(activity, mList)
    rv_view.layoutManager = LinearLayoutManager(activity)
    rv_view.adapter = mAdapter
    mAdapter?.setOnItemClickListener { view, i ->
      Toast.makeText(activity, "$i", Toast.LENGTH_SHORT).show()
    }
  }

  private fun registerListener() {
    sr_layout.setColorSchemeResources(
        android.R.color.holo_blue_bright,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light
    )

    sr_layout.setProgressViewOffset(true, -20, 80)

    sr_layout.setOnRefreshListener({
      getLiveListData()
    })
  }
}