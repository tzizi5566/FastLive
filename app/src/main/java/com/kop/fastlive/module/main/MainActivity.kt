package com.kop.fastlive.module.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R
import com.kop.fastlive.module.editprofile.EditProfileFragment
import com.kop.fastlive.module.livelist.LiveListFragment
import kotlinx.android.synthetic.main.activity_main.tabhost
import kotlinx.android.synthetic.main.view_indicator.view.tab_icon

class MainActivity : PermissionCheckActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setupTab()
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
        .setIndicator(getIndicatorView(R.drawable.tab_publish_live))
    tabhost.addTab(live, LiveListFragment::class.java, null)

    val profile = tabhost
        .newTabSpec(EditProfileFragment::class.java.simpleName)
        .setIndicator(getIndicatorView(R.drawable.tab_profile))
    tabhost.addTab(profile, EditProfileFragment::class.java, null)
  }

  private fun getIndicatorView(resId: Int): View {
    val view = LayoutInflater.from(this).inflate(R.layout.view_indicator, null, false)
    view.tab_icon.setImageResource(resId)
    return view
  }
}
