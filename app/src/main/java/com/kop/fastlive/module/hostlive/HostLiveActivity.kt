package com.kop.fastlive.module.hostlive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.avos.avoscloud.AVCloudQueryResult
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.CloudQueryCallback
import com.kop.fastlive.R
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.ILiveConstants
import com.tencent.ilivesdk.core.ILiveLoginManager
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import kotlinx.android.synthetic.main.activity_host_live.live_view


class HostLiveActivity : AppCompatActivity() {

  private var mRoomId: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_host_live)

    live_view.setAutoOrientation(false)
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    createRoom()
  }

  private fun createRoom() {
    mRoomId = intent.getStringExtra("roomId").toInt()

    //创建房间配置项
    val hostOption = ILVLiveRoomOption(ILiveLoginManager.getInstance().myUserId)
        .controlRole("LiveMaster")//角色设置
        .autoFocus(true)
        .autoMic(true)
        .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
        .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
        .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)//是否开始半自动接收

    //创建房间
    ILVLiveManager.getInstance().createRoom(mRoomId, hostOption, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any) {
        Toast.makeText(this@HostLiveActivity, "create room  ok", Toast.LENGTH_SHORT).show()
      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
        quitRoom()
        finish()
      }
    })
  }

  private fun quitRoom() {
    val objectId = intent.getStringExtra("objectId")

    ILVLiveManager.getInstance().quitRoom(object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any?) {
        AVQuery.doCloudQueryInBackground(
            "delete from RoomInfo where objectId='$objectId'",
            object : CloudQueryCallback<AVCloudQueryResult>() {
              override fun done(p0: AVCloudQueryResult?, p1: AVException?) {
                if (p1 == null) {
                  Toast.makeText(applicationContext, "退出成功！", Toast.LENGTH_SHORT).show()
                }
              }
            })
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {

      }
    })
  }

  override fun onResume() {
    super.onResume()
    ILVLiveManager.getInstance().onResume()
  }

  override fun onPause() {
    super.onPause()
    ILVLiveManager.getInstance().onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    quitRoom()
    ILVLiveManager.getInstance().onDestory()
  }
}
