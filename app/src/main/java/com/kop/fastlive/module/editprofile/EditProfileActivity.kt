package com.kop.fastlive.module.editprofile

import android.os.Bundle
import com.blankj.utilcode.util.FragmentUtils
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R
import com.kop.fastlive.utils.picchoose.PicChooserType

class EditProfileActivity : PermissionCheckActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_profile)

    val fragment = FragmentUtils.findFragment(
        supportFragmentManager,
        EditProfileFragment::class.java)

    if (fragment == null) {
      FragmentUtils.add(
          supportFragmentManager,
          EditProfileFragment.newInstance(),
          R.id.fl)
    }
  }

  override fun setPicType(): PicChooserType {
    return PicChooserType.AVATAR
  }
}
