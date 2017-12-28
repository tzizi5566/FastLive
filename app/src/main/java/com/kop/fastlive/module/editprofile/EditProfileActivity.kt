package com.kop.fastlive.module.editprofile

import android.os.Bundle
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R

class EditProfileActivity : PermissionCheckActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_profile)

    val fragment = supportFragmentManager.findFragmentByTag(
        EditProfileFragment::class.java.simpleName)
    if (fragment == null) {
      supportFragmentManager
          .beginTransaction()
          .add(R.id.fl, EditProfileFragment.newInstance(),
              EditProfileFragment::class.java.simpleName)
          .commit()
    }
  }
}
