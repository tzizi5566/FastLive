package com.kop.fastlive.module.watcher

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.model.Constants
import com.kop.fastlive.utils.keyboard.KeyboardHeightObserver
import com.kop.fastlive.utils.keyboard.KeyboardHeightProvider
import com.kop.fastlive.widget.BottomControlView
import com.kop.fastlive.widget.ChatView
import com.tencent.TIMMessage
import com.tencent.TIMUserProfile
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import com.tencent.livesdk.ILVText
import kotlinx.android.synthetic.main.activity_watcher_live.bottom_control_view
import kotlinx.android.synthetic.main.activity_watcher_live.chat_view
import kotlinx.android.synthetic.main.activity_watcher_live.cl_view
import kotlinx.android.synthetic.main.activity_watcher_live.danmu_view
import kotlinx.android.synthetic.main.activity_watcher_live.keyboard
import kotlinx.android.synthetic.main.activity_watcher_live.live_view
import kotlinx.android.synthetic.main.activity_watcher_live.msg_list

class WatcherLiveActivity : AppCompatActivity(),
    BottomControlView.OnControlListener,
    ChatView.OnChatSendListener,
    KeyboardHeightObserver,
    ILVLiveConfig.ILVLiveMsgListener {

  private var mRoomId: Int = -1
  private var mHostId: String? = null
  private var mKeyboardHeightProvider: KeyboardHeightProvider? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_watcher_live)

    registerListener()
    live_view.setAutoOrientation(false)
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    joinRoom()
  }

  private fun registerListener() {
    bottom_control_view.setOnControlListener(this)
    chat_view.setOnChatSendListener(this)
    (application as MyApplication).getLiveConfig().liveMsgListener = this
    mKeyboardHeightProvider = KeyboardHeightProvider(this)
    cl_view.post({ mKeyboardHeightProvider?.start() })
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

  private fun sendMsg(cmd: ILVCustomCmd) {
    ILVLiveManager.getInstance().sendCustomCmd(cmd, object : ILiveCallBack<TIMMessage> {
      override fun onSuccess(data: TIMMessage?) {
        val userProfile = (application as MyApplication).getUserProfile()
        userProfile?.let {
          val msgInfo = ChatMsgInfo(
              userProfile.identifier,
              userProfile.faceUrl,
              cmd.param ?: "",
              userProfile.nickName)

          if (cmd.cmd == Constants.CMD_CHAT_MSG_LIST) {
            msg_list.addMsgInfos(msgInfo)
          } else if (cmd.cmd == Constants.CMD_CHAT_MSG_DANMU) {
            msg_list.addMsgInfos(msgInfo)
            danmu_view.addMsgInfos(msgInfo)
          }
        }
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@WatcherLiveActivity, "发送消息失败：$errCode !", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun translationView(height: Int) {
    val chatViewAnimator = ObjectAnimator.ofFloat(chat_view, View.TRANSLATION_Y, -height.toFloat())
    val msgListAnimator = ObjectAnimator.ofFloat(msg_list, View.TRANSLATION_Y, -height.toFloat())

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(chatViewAnimator, msgListAnimator)
    animatorSet.duration = 300
    animatorSet.start()
  }

  override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
    if (height == 0) {
      bottom_control_view.visibility = View.VISIBLE
      chat_view.visibility = View.GONE
    }
    translationView(height)
    val params = keyboard.layoutParams as ConstraintLayout.LayoutParams
    params.height = height
  }

  override fun onChatClick() {
    bottom_control_view.visibility = View.GONE
    chat_view.visibility = View.VISIBLE
    chat_view.setFocusable(this)
  }

  override fun onCloseClick() {
    finish()
  }

  override fun onChatSend(cmd: ILVCustomCmd) {
    sendMsg(cmd)
  }

  override fun onNewOtherMsg(message: TIMMessage?) {

  }

  override fun onNewCustomMsg(cmd: ILVCustomCmd?, id: String?, userProfile: TIMUserProfile?) {
    userProfile?.let {
      val msgInfo = ChatMsgInfo(
          userProfile.identifier,
          userProfile.faceUrl,
          cmd?.param ?: "",
          userProfile.nickName)

      if (cmd?.cmd == Constants.CMD_CHAT_MSG_LIST) {
        msg_list.addMsgInfos(msgInfo)
      } else if (cmd?.cmd == Constants.CMD_CHAT_MSG_DANMU) {
        msg_list.addMsgInfos(msgInfo)
        danmu_view.addMsgInfos(msgInfo)
      }
    }
  }

  override fun onNewTextMsg(text: ILVText<out ILVText<*>>?, SenderId: String?,
      userProfile: TIMUserProfile?) {

  }

  override fun onResume() {
    super.onResume()
    mKeyboardHeightProvider?.setKeyboardHeightObserver(this)
    ILVLiveManager.getInstance().onResume()
  }

  override fun onPause() {
    super.onPause()
    mKeyboardHeightProvider?.setKeyboardHeightObserver(null)
    ILVLiveManager.getInstance().onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    mKeyboardHeightProvider?.close()
    quitRoom()
    ILVLiveManager.getInstance().onDestory()
  }
}
