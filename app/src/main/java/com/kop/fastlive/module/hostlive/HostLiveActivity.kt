package com.kop.fastlive.module.hostlive

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.avos.avoscloud.AVCloudQueryResult
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.CloudQueryCallback
import com.google.gson.Gson
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.model.ChatType
import com.kop.fastlive.model.GiftCmdInfo
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.model.GiftType
import com.kop.fastlive.utils.keyboard.KeyboardHeightObserver
import com.kop.fastlive.utils.keyboard.KeyboardHeightProvider
import com.kop.fastlive.widget.BottomControlView
import com.kop.fastlive.widget.ChatView
import com.tencent.TIMMessage
import com.tencent.TIMUserProfile
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.ILiveConstants
import com.tencent.ilivesdk.core.ILiveLoginManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import com.tencent.livesdk.ILVText
import kotlinx.android.synthetic.main.activity_host_live.bottom_control_view
import kotlinx.android.synthetic.main.activity_host_live.chat_view
import kotlinx.android.synthetic.main.activity_host_live.cl_view
import kotlinx.android.synthetic.main.activity_host_live.danmu_view
import kotlinx.android.synthetic.main.activity_host_live.gift_full_view
import kotlinx.android.synthetic.main.activity_host_live.gift_view
import kotlinx.android.synthetic.main.activity_host_live.keyboard
import kotlinx.android.synthetic.main.activity_host_live.live_view
import kotlinx.android.synthetic.main.activity_host_live.msg_list


class HostLiveActivity : AppCompatActivity(),
    BottomControlView.OnControlListener,
    ChatView.OnChatSendListener,
    KeyboardHeightObserver,
    ILVLiveConfig.ILVLiveMsgListener {

  private var mRoomId: Int = -1
  private var mKeyboardHeightProvider: KeyboardHeightProvider? = null

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
    (application as MyApplication).getLiveConfig().liveMsgListener = this
    mKeyboardHeightProvider = KeyboardHeightProvider(this)
    cl_view.post({ mKeyboardHeightProvider?.start() })
  }

  private fun sendMsg(cmd: ILVCustomCmd) {
    ILVLiveManager.getInstance().sendCustomCmd(cmd, object : ILiveCallBack<TIMMessage> {
      override fun onSuccess(data: TIMMessage?) {
        val userProfile = (application as MyApplication).getUserProfile()
        val msgInfo = ChatMsgInfo(
            userProfile.identifier,
            userProfile.faceUrl,
            cmd.param ?: "",
            userProfile.nickName)

        if (cmd.cmd == ChatType.CMD_CHAT_MSG_LIST) {
          msg_list.addMsgInfos(msgInfo)
        } else if (cmd.cmd == ChatType.CMD_CHAT_MSG_DANMU) {
          msg_list.addMsgInfos(msgInfo)
          danmu_view.addMsgInfos(msgInfo)
        }
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {
        Toast.makeText(this@HostLiveActivity, "发送消息失败：$errCode !", Toast.LENGTH_SHORT).show()
      }
    })
  }

//  private fun sendGiftMsg(cmd: ILVCustomCmd) {
//    ILVLiveManager.getInstance().sendCustomCmd(cmd, object : ILiveCallBack<TIMMessage> {
//      override fun onSuccess(data: TIMMessage?) {
//        val userProfile = (application as MyApplication).getUserProfile()
//        val gson = Gson()
//        val giftCmdInfo = gson.fromJson(cmd.param, GiftCmdInfo::class.java) ?: return
//        val giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId!!)
//        gift_view.showGift(giftInfo, giftCmdInfo.repeatId, userProfile)
//      }
//
//      override fun onError(module: String?, errCode: Int, errMsg: String?) {
//
//      }
//    })
//  }

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
      chat_view.setFocusable(this, false)
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
    chat_view.setFocusable(this, true)
  }

  override fun onCloseClick() {
    finish()
  }

  override fun onGiftClick() {
//    bottom_control_view.alpha = 0F//此处设置visibility会出现礼物动画不显示的bug
//    val giftSelectDialog = GiftSelectDialog(this)
//    giftSelectDialog.show()
//    giftSelectDialog.dialog.setOnCancelListener {
//      bottom_control_view.alpha = 1F
//    }
//    giftSelectDialog.setGiftSendListener(object : OnGiftSendListener {
//      override fun onGiftSendClick(customCmd: ILVCustomCmd) {
//        sendGiftMsg(customCmd)
//      }
//    })
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

      when {
        cmd?.cmd == ChatType.CMD_CHAT_MSG_LIST -> msg_list.addMsgInfos(msgInfo)

        cmd?.cmd == ChatType.CMD_CHAT_MSG_DANMU -> {
          msg_list.addMsgInfos(msgInfo)
          danmu_view.addMsgInfos(msgInfo)
        }

        cmd?.cmd == ChatType.CMD_CHAT_GIFT -> {
          val gson = Gson()
          val giftCmdInfo = gson.fromJson(cmd.param, GiftCmdInfo::class.java) ?: return
          val giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId!!)

          if (giftInfo?.type == GiftType.ContinueGift) {
            gift_view.showGift(giftInfo, giftCmdInfo.repeatId, userProfile)
          } else if (giftInfo?.type == GiftType.FullScreenGift) {
            gift_full_view.showGift(giftInfo, userProfile)
          }
        }
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
