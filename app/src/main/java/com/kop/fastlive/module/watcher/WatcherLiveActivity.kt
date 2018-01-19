package com.kop.fastlive.module.watcher

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.GetCallback
import com.avos.avoscloud.SaveCallback
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.kop.fastlive.MyApplication
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatMsgInfo
import com.kop.fastlive.model.ChatType
import com.kop.fastlive.model.GiftCmdInfo
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.model.GiftType
import com.kop.fastlive.module.editprofile.CustomProfile
import com.kop.fastlive.utils.NumUtil
import com.kop.fastlive.utils.keyboard.KeyboardHeightObserver
import com.kop.fastlive.utils.keyboard.KeyboardHeightProvider
import com.kop.fastlive.widget.BottomControlView
import com.kop.fastlive.widget.ChatView
import com.kop.fastlive.widget.GiftSelectDialog
import com.kop.fastlive.widget.GiftSelectDialog.OnGiftSendListener
import com.tencent.TIMCallBack
import com.tencent.TIMFriendshipManager
import com.tencent.TIMMessage
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import com.tencent.livesdk.ILVText
import com.tencent.livesdk.ILVText.ILVTextType
import kotlinx.android.synthetic.main.activity_watcher_live.bottom_control_view
import kotlinx.android.synthetic.main.activity_watcher_live.chat_view
import kotlinx.android.synthetic.main.activity_watcher_live.cl_view
import kotlinx.android.synthetic.main.activity_watcher_live.danmu_view
import kotlinx.android.synthetic.main.activity_watcher_live.gift_full_view
import kotlinx.android.synthetic.main.activity_watcher_live.gift_view
import kotlinx.android.synthetic.main.activity_watcher_live.heart_layout
import kotlinx.android.synthetic.main.activity_watcher_live.keyboard
import kotlinx.android.synthetic.main.activity_watcher_live.live_view
import kotlinx.android.synthetic.main.activity_watcher_live.msg_list
import java.util.Timer
import kotlin.concurrent.timerTask

class WatcherLiveActivity : AppCompatActivity(),
    BottomControlView.OnControlListener,
    ChatView.OnChatSendListener,
    KeyboardHeightObserver,
    ILVLiveConfig.ILVLiveMsgListener {

  private var mRoomId: Int = -1
  private var mHostId: String? = null
  private var mKeyboardHeightProvider: KeyboardHeightProvider? = null
  private val mHeartTimer = Timer()
  private lateinit var mUserObjectId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_watcher_live)

    bottom_control_view.setIsHost(false)

    registerListener()
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    joinRoom()
  }

  private fun registerListener() {
    bottom_control_view.setOnControlListener(this)
    chat_view.setOnChatSendListener(this)
    (application as MyApplication).getLiveConfig().liveMsgListener = this
    mKeyboardHeightProvider = KeyboardHeightProvider(this)
    cl_view.post({ mKeyboardHeightProvider?.start() })

    live_view.setOnClickListener {
      val ilvCustomCmd = ILVCustomCmd()
      ilvCustomCmd.type = ILVTextType.eGroupMsg
      ilvCustomCmd.cmd = ChatType.CMD_CHAT_GIFT
      ilvCustomCmd.destId = ILiveRoomManager.getInstance().imGroupId
      val giftCmdInfo = GiftCmdInfo(GiftInfo.Gift_Heart.giftId, "")
      val gson = Gson()
      ilvCustomCmd.param = gson.toJson(giftCmdInfo)

      sendGiftMsg(ilvCustomCmd)
    }
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
        mHeartTimer.schedule(timerTask {
          runOnUiThread {
            heart_layout.addHeart(NumUtil.getRandomColor())
          }
        }, 0, 1500)

        mUserObjectId = SPUtils.getInstance().getString("objectId")
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
        Toast.makeText(this@WatcherLiveActivity, "发送消息失败：$errCode !", Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun sendGiftMsg(cmd: ILVCustomCmd) {
    ILVLiveManager.getInstance().sendCustomCmd(cmd, object : ILiveCallBack<TIMMessage> {
      override fun onSuccess(data: TIMMessage?) {
        val userProfile = (application as MyApplication).getUserProfile()
        val gson = Gson()
        val giftCmdInfo = gson.fromJson(cmd.param, GiftCmdInfo::class.java) ?: return
        val giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId!!)

        calLevel(giftInfo)

        when {
          giftInfo?.type == GiftType.ContinueGift ->
            gift_view.showGift(giftInfo, giftCmdInfo.repeatId, userProfile)

          giftInfo?.type == GiftType.FullScreenGift ->
            gift_full_view.showGift(giftInfo, userProfile)

          giftInfo?.type == GiftType.HeartGift ->
            heart_layout.addHeart(NumUtil.getRandomColor())
        }
      }

      override fun onError(module: String?, errCode: Int, errMsg: String?) {

      }
    })
  }

  private fun calLevel(giftInfo: GiftInfo?) {
    var avData: AVObject?
    val avObject = AVObject.createWithoutData("UserInfo", mUserObjectId)
    avObject.increment("user_exp", giftInfo?.expValue)
    avObject.increment("send_nums")
    avObject.isFetchWhenSave = true
    avObject.saveInBackground(object : SaveCallback() {
      override fun done(p0: AVException?) {
        if (p0 == null) {
          val avQuery = AVQuery<AVObject>("UserInfo")
          avQuery.getInBackground(mUserObjectId, object : GetCallback<AVObject>() {
            override fun done(p0: AVObject?, p1: AVException?) {
              avData = p0
              val exp = p0?.getInt("user_exp")
              val level = exp?.div(200)?.plus(1)
              val obj = AVObject.createWithoutData("UserInfo", mUserObjectId)
              obj.put("user_level", level)
              obj.saveInBackground(object : SaveCallback() {
                override fun done(p0: AVException?) {
                  TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_LEVEL,
                      level.toString().toByteArray(), object : TIMCallBack {
                    override fun onSuccess() {
                      getSelfInfo()
                    }

                    override fun onError(p0: Int, p1: String?) {

                    }
                  })

                  TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_SEND,
                      avData?.getInt("send_nums").toString().toByteArray(), object : TIMCallBack {
                    override fun onSuccess() {
                      getSelfInfo()
                    }

                    override fun onError(p0: Int, p1: String?) {

                    }
                  })
                }
              })
            }
          })
        }
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

  private fun getSelfInfo() {
    TIMFriendshipManager.getInstance().getSelfProfile(object : TIMValueCallBack<TIMUserProfile> {
      override fun onSuccess(timUserProfile: TIMUserProfile) {
        //获取自己信息成功
        (application as MyApplication).setUserProfile(timUserProfile)
      }

      override fun onError(i: Int, s: String) {

      }
    })
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
    bottom_control_view.alpha = 0F//此处设置visibility会出现礼物动画不显示的bug
    val giftSelectDialog = GiftSelectDialog(this)
    giftSelectDialog.show()
    giftSelectDialog.dialog.setOnCancelListener {
      bottom_control_view.alpha = 1F
    }
    giftSelectDialog.setGiftSendListener(object : OnGiftSendListener {
      override fun onGiftSendClick(customCmd: ILVCustomCmd) {
        sendGiftMsg(customCmd)
      }
    })
  }

  override fun onOperateClick(view: View) {

  }

  override fun onChatSend(cmd: ILVCustomCmd) {
    sendMsg(cmd)
  }

  override fun onNewOtherMsg(message: TIMMessage?) {

  }

  override fun onNewCustomMsg(cmd: ILVCustomCmd?, id: String?, userProfile: TIMUserProfile?) {
    userProfile?.let {
      val msgInfo = ChatMsgInfo(
          it.identifier,
          it.faceUrl,
          cmd?.param ?: "",
          it.nickName)

      when {
        cmd?.cmd == ChatType.CMD_CHAT_MSG_LIST -> msg_list.addMsgInfos(msgInfo)

        cmd?.cmd == ChatType.CMD_CHAT_MSG_DANMU -> {
          msg_list.addMsgInfos(msgInfo)
          danmu_view.addMsgInfos(msgInfo)
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
