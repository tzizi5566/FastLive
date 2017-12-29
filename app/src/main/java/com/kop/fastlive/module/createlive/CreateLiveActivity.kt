package com.kop.fastlive.module.createlive

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R
import com.kop.fastlive.choosePicWithPermissionCheck
import com.kop.fastlive.utils.ImgUtil
import com.kop.fastlive.utils.callblack.CallbackManager
import com.kop.fastlive.utils.callblack.CallbackType
import com.kop.fastlive.utils.callblack.IGlobalCallback
import com.kop.fastlive.utils.picchoose.PicChooserType
import kotlinx.android.synthetic.main.activity_create_live.fl_set_cover
import kotlinx.android.synthetic.main.activity_create_live.iv_cover
import kotlinx.android.synthetic.main.activity_create_live.tv_create
import kotlinx.android.synthetic.main.activity_create_live.tv_pic_tip

class CreateLiveActivity : PermissionCheckActivity(), OnClickListener {

  private var mCoverUrl: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_live)

    registerListener()
  }

  override fun setPicType(): PicChooserType {
    return PicChooserType.COVER
  }

  private fun registerListener() {
    fl_set_cover.setOnClickListener(this)
    tv_create.setOnClickListener(this)
    CallbackManager
        .getInstance()
        .addCallback(CallbackType.CHOOSE_PIC_COVER, object : IGlobalCallback<Any> {
          override fun executeCallback(args: Any) {
            updateCover(args.toString())
          }
        })
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.fl_set_cover -> {
        choosePicWithPermissionCheck()
      }

      R.id.tv_create -> {

      }
    }
  }

  private fun updateCover(url: String) {
    mCoverUrl = url
    ImgUtil.load(this, url, iv_cover)
    tv_pic_tip.visibility = View.GONE
  }
}
