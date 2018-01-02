package com.kop.fastlive.module.watcher

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kop.fastlive.R
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import kotlinx.android.synthetic.main.activity_watcher_live.live_view

class WatcherLiveActivity : AppCompatActivity() {

  private var mRoomId: Int = -1
  private var mHostId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_watcher_live)

    live_view.setAutoOrientation(false)
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    joinRoom()
  }

  private fun joinRoom() {
    mRoomId = intent.getStringExtra("roomId").toInt()
    mHostId = intent.getStringExtra("hostId")

    //加入房间配置项
    val memberOption = ILVLiveRoomOption(mHostId)
        .autoCamera(false) //是否自动打开摄像头
        .controlRole("Guest") //角色设置
        .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
        .autoMic(false)//是否自动打开mic
        .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM or
            AVRoomMulti.AUTH_BITS_RECV_AUDIO or
            AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO or
            AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置

    //加入房间
    ILVLiveManager.getInstance().joinRoom(mRoomId, memberOption, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any) {

      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
        quitRoom()
        finish()
      }
    })
  }

  private fun quitRoom() {
    ILVLiveManager.getInstance().quitRoom(object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any?) {
        Toast.makeText(applicationContext, "退出成功！", Toast.LENGTH_SHORT).show()
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
