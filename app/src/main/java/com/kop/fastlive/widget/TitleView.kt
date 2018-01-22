package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kop.fastlive.R
import com.kop.fastlive.utils.ImgUtil
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import kotlinx.android.synthetic.main.item_title_watcher.view.iv_user_avatar
import kotlinx.android.synthetic.main.view_title.view.iv_host_avatar
import kotlinx.android.synthetic.main.view_title.view.rv_watch_list
import kotlinx.android.synthetic.main.view_title.view.tv_watchers_num

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/22 14:16
 */
@SuppressLint("SetTextI18n")
class TitleView : LinearLayoutCompat {

  private lateinit var mAdapter: WatchAdapter
  private var mWatcherNum = 0
  private lateinit var mUserProfile: TIMUserProfile

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
    LayoutInflater.from(context).inflate(R.layout.view_title, this, true)

    rv_watch_list.setHasFixedSize(true)
    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    rv_watch_list.layoutManager = layoutManager
    mAdapter = WatchAdapter(context)
    rv_watch_list.adapter = mAdapter

    iv_host_avatar.setOnClickListener {
      val ids = listOf(mUserProfile.identifier)
      TIMFriendshipManager.getInstance().getUsersProfile(ids,
          object : TIMValueCallBack<List<TIMUserProfile>> {
            override fun onSuccess(timUserProfiles: List<TIMUserProfile>) {
              with(context as Activity) {
                val userInfoDialog = UserInfoDialog(this, timUserProfiles[0])
                userInfoDialog.show()
              }
            }

            override fun onError(i: Int, s: String) {
              Toast.makeText(this@TitleView.context, "请求用户信息失败", Toast.LENGTH_SHORT).show()
            }
          })
    }
  }

  fun setHost(userProfile: TIMUserProfile?) {
    userProfile?.let {
      mUserProfile = it
      val avatar = it.faceUrl
      if (avatar.isNullOrEmpty()) {
        ImgUtil.loadRound(context, R.drawable.default_avatar, iv_host_avatar)
      } else {
        ImgUtil.loadRound(context, avatar, iv_host_avatar)
      }
    }
  }

  fun addWatcher(userProfile: TIMUserProfile?) {
    userProfile?.let {
      mAdapter.addWatcher(it)
      mWatcherNum++
      tv_watchers_num.text = "观众:$mWatcherNum"
    }
  }

  fun removeWatcher(userProfile: TIMUserProfile?) {
    userProfile?.let {
      mAdapter.removeWatcher(it)
      mWatcherNum--
      tv_watchers_num.text = "观众:$mWatcherNum"
    }
  }

  class WatchAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<TIMUserProfile>()
    private var mListener: ((TIMUserProfile, Int) -> Unit)? = null
    private var mPositionStart = 0

    fun addWatcher(userProfile: TIMUserProfile) {
      mPositionStart = mList.size
      mList.add(userProfile)
      notifyItemRangeChanged(mPositionStart, mList.size)
    }

    fun removeWatcher(userProfile: TIMUserProfile) {
      val removeList = mList.filter { it.identifier == userProfile.identifier }
      mList.removeAll(removeList)
      notifyItemRangeChanged(0, mList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
      return WatchViewHolder(
          LayoutInflater.from(context).inflate(R.layout.item_title_watcher, parent, false),
          mListener)
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
      val profile = mList[position]
      with(holder as WatchViewHolder) {
        val avatar = profile.faceUrl
        if (avatar.isNullOrEmpty()) {
          ImgUtil.loadRound(context, R.drawable.default_avatar, itemView.iv_user_avatar)
        } else {
          ImgUtil.loadRound(context, avatar, itemView.iv_user_avatar)
        }
      }
    }

    fun setOnItemClickListener(listener: ((TIMUserProfile, Int) -> Unit)?) {
      mListener = listener
    }

    inner class WatchViewHolder(itemView: View,
        listener: ((TIMUserProfile, Int) -> Unit)?) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

      private val mListener = listener

      init {
        itemView.setOnClickListener(this)
      }

      override fun onClick(view: View?) {
        if (mListener != null && view != null) {
          mListener.invoke(mList[layoutPosition], layoutPosition)
        }
      }
    }
  }
}