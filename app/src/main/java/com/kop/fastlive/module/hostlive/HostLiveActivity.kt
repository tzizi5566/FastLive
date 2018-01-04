package com.kop.fastlive.module.hostlive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.avos.avoscloud.AVCloudQueryResult
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.CloudQueryCallback
import com.blankj.utilcode.util.KeyboardUtils
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.widget.BottomControlView
import com.kop.fastlive.widget.ChatView
import com.tencent.TIMMessage
import com.tencent.TIMUserProfile
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.ILiveConstants
import com.tencent.ilivesdk.core.ILiveLoginManager
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import com.tencent.livesdk.ILVText
import kotlinx.android.synthetic.main.activity_host_live.bottom_control_view
import kotlinx.android.synthetic.main.activity_host_live.chat_view
import kotlinx.android.synthetic.main.activity_host_live.live_view
import kotlinx.android.synthetic.main.activity_host_live.msg_list


class HostLiveActivity : AppCompatActivity(),
    BottomControlView.OnControlListener,
    ChatView.OnChatSendListener,
    KeyboardUtils.OnSoftInputChangedListener,
    ILVLiveConfig.ILVLiveMsgListener {

  private var mRoomId: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_host_live)

    registerListener()
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    createRoom()
  }

  private fun createRoom() {
    mRoomId = intent.getStringExtra("roomId").toInt()

    //创建房间配置项
    val hostOption = ILVLiveRoomOption(ILiveLoginManager.getInstance().myUserId)
        .controlRole("LiveMaster")//角色设置
        .autoCamera(true)
        .autoFocus(true)
        .autoMic(true)
        .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
        .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
        .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)//是否开始半自动接收

    //创建房间
    ILVLiveManager.getInstance().createRoom(mRoomId, hostOption, object : ILiveCallBack<Any> {
      override fun onSuccess(data: Any) {

      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
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

  private fun registerListener() {
    bottom_control_view.setOnControlListener(this)
    chat_view.setOnChatSendListener(this)
    KeyboardUtils.registerSoftInputChangedListener(this, this)
    (application as MyApplication).getLiveConfig().liveMsgListener = this
  }

  private fun sendMsg(msg: String) {
    val ilvText = ILVText<ILVCustomCmd>(
        ILVText.ILVTextType.eGroupMsg,
        ILiveRoomManager.getInstance().imGroupId,
        msg)
    ILVLiveManager.getInstance().sendText(ilvText, object : ILiveCallBack<TIMMessage>{
      override fun onSuccess(data: TIMMessage?) {
        val userProfile = (application as MyApplication).getUserProfile()
        userProfile?.let {
          val msgInfo = ChatMsgInfo(userProfile.identifier, userProfile.faceUrl, msg)
          msg_list.addMsgInfos(msgInfo)
        }
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@HostLiveActivity, "发送消息失败：$errCode !", Toast.LENGTH_SHORT).show()
      }
    })
  }

  override fun onSoftInputChanged(height: Int) {
    if (height < 0) {
      bottom_control_view.visibility = View.VISIBLE
      chat_view.visibility = View.GONE
    }
  }

  override fun onChatClick() {
    bottom_control_view.visibility = View.GONE
    chat_view.visibility = View.VISIBLE
    chat_view.setFocusable(this)
  }

  override fun onCloseClick() {
    finish()
  }

  override fun onChatSend(msg: String) {
    sendMsg(msg)
  }

  override fun onNewOtherMsg(message: TIMMessage?) {

  }

  override fun onNewCustomMsg(cmd: ILVCustomCmd?, id: String?, userProfile: TIMUserProfile?) {

  }

  override fun onNewTextMsg(text: ILVText<out ILVText<*>>?, SenderId: String?,
      userProfile: TIMUserProfile?) {
    userProfile?.let {
      val msgInfo = ChatMsgInfo(userProfile.identifier, userProfile.faceUrl, text?.getText() ?: "")
      msg_list.addMsgInfos(msgInfo)
    }
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
