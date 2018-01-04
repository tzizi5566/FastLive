package com.kop.fastlive.module.createlive

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.SaveCallback
import com.kop.fastlive.MyApplication
import com.kop.fastlive.PermissionCheckActivity
import com.kop.fastlive.R
import com.kop.fastlive.choosePicWithPermissionCheck
import com.kop.fastlive.createRoomWithPermissionCheck
import com.kop.fastlive.module.hostlive.HostLiveActivity
import com.kop.fastlive.utils.ImgUtil
import com.kop.fastlive.utils.NumUtil
import com.kop.fastlive.utils.callblack.CallbackManager
import com.kop.fastlive.utils.callblack.CallbackType
import com.kop.fastlive.utils.callblack.IGlobalCallback
import com.kop.fastlive.utils.picchoose.PicChooserType
import kotlinx.android.synthetic.main.activity_create_live.edt_title
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
    registerCreateRoom()
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
        createRoomWithPermissionCheck()
      }
    }
  }

  private fun registerCreateRoom() {
    CallbackManager.getInstance().addCallback(CallbackType.CREATE_ROOM,
        object : IGlobalCallback<Any> {
          override fun executeCallback(args: Any) {
            val selfProfile = (application as MyApplication).getUserProfile()
            selfProfile?.let {
              val roomId = NumUtil.getRandomNum()
              val userId = selfProfile.identifier
              val userAvatar = selfProfile.faceUrl
              val nickName = selfProfile.nickName
              val userName = if (TextUtils.isEmpty(nickName)) selfProfile.identifier else nickName
              val liveCover = mCoverUrl
              val liveTitle = edt_title.text.toString()

              val avObject = AVObject("RoomInfo")
              avObject.put("room_id", roomId)
              avObject.put("user_id", userId)
              avObject.put("user_avatar", userAvatar)
              avObject.put("user_name", userName)
              avObject.put("live_cover", liveCover)
              avObject.put("live_title", liveTitle)
              avObject.saveInBackground(object : SaveCallback() {
                override fun done(p0: AVException?) {
                  if (p0 == null) {
                    val intent = Intent(this@CreateLiveActivity, HostLiveActivity::class.java)
                    intent.putExtra("roomId", roomId)
                    intent.putExtra("objectId", avObject.objectId)
                    startActivity(intent)
                    Toast.makeText(this@CreateLiveActivity, "创建成功！", Toast.LENGTH_SHORT).show()
                    finish()
                  } else {
                    Toast.makeText(this@CreateLiveActivity, p0.message, Toast.LENGTH_SHORT).show()
                  }
                }
              })
            }
          }
        })
  }

  private fun updateCover(url: String) {
    mCoverUrl = url
    ImgUtil.load(this, url, iv_cover)
    tv_pic_tip.visibility = View.GONE
  }
}
