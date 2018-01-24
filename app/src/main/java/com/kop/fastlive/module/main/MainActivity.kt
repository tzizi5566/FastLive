package com.kop.fastlive.module.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.avos.avoscloud.SaveCallback
import com.blankj.utilcode.util.SPUtils
import com.gyf.barlibrary.ImmersionBar
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R
import com.kop.fastlive.module.createlive.CreateLiveActivity
import com.kop.fastlive.module.editprofile.EditProfileFragment
import com.kop.fastlive.module.livelist.LiveListFragment
import com.kop.fastlive.utils.picchoose.PicChooserType
import com.tencent.TIMFriendshipManager
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import kotlinx.android.synthetic.main.activity_main.iv_create_live
import kotlinx.android.synthetic.main.activity_main.tabhost
import kotlinx.android.synthetic.main.view_indicator.view.tab_icon

class MainActivity : PermissionCheckActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ImmersionBar.with(this)
        .fitsSystemWindows(true)
        .statusBarColor(R.color.colorPrimaryDark)
        .init()

    setupTab()

    getSelfProfile()
  }

  override fun setPicType(): PicChooserType {
    return PicChooserType.AVATAR
  }

  private fun setupTab() {
    tabhost.setup(this, supportFragmentManager, android.R.id.tabcontent)
    tabhost.tabWidget.setDividerDrawable(android.R.color.transparent)

    val liveList = tabhost
        .newTabSpec(LiveListFragment::class.java.simpleName)
        .setIndicator(getIndicatorView(R.drawable.tab_livelist))
    tabhost.addTab(liveList, LiveListFragment::class.java, null)

    val live = tabhost
        .newTabSpec(LiveListFragment::class.java.simpleName)
        .setIndicator(getIndicatorView(0))
    tabhost.addTab(live, LiveListFragment::class.java, null)

    val profile = tabhost
        .newTabSpec(EditProfileFragment::class.java.simpleName)
        .setIndicator(getIndicatorView(R.drawable.tab_profile))
    tabhost.addTab(profile, EditProfileFragment::class.java, null)

    iv_create_live.setOnClickListener({
      //跳转到创建直播的页面。
      startActivity(Intent(this@MainActivity, CreateLiveActivity::class.java))
    })
  }

  private fun getIndicatorView(resId: Int): View {
    val view = LayoutInflater.from(this).inflate(R.layout.view_indicator, null, false)
    view.tab_icon.setImageResource(resId)
    return view
  }

  private fun getSelfProfile() {
    TIMFriendshipManager.getInstance().getSelfProfile(object : TIMValueCallBack<TIMUserProfile> {
      override fun onSuccess(p0: TIMUserProfile?) {
        p0?.let {
          val userProfile: TIMUserProfile = p0
          val avQuery = AVQuery<AVObject>("UserInfo")
          avQuery.whereContains("user_id", userProfile.identifier)
          avQuery.findInBackground(object : FindCallback<AVObject>() {
            override fun done(p0: MutableList<AVObject>?, p1: AVException?) {
              if (p0 != null && p0.isEmpty()) {
                val avObject = AVObject("UserInfo")
                avObject.put("user_id", userProfile.identifier)
                avObject.put("user_exp", 0)
                avObject.put("user_level", 0)
                avObject.put("send_nums", 0)
                avObject.put("get_nums", 0)
                avObject.saveInBackground(object : SaveCallback() {
                  override fun done(p0: AVException?) {
                    val objectId = avObject.objectId
                    SPUtils.getInstance().put("objectId", objectId)
                  }
                })
              }
            }
          })
        }
      }

      override fun onError(p0: Int, p1: String?) {
        Toast.makeText(this@MainActivity, "获取个人信息失败！$p1", Toast.LENGTH_SHORT).show()
      }
    })
  }
}
