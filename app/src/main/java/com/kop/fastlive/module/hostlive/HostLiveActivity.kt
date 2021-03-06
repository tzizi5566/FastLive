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
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.CloudQueryCallback
import com.avos.avoscloud.GetCallback
import com.avos.avoscloud.SaveCallback
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
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
import com.kop.fastlive.widget.HostControlDialog
import com.kop.fastlive.widget.HostControlDialog.OnControlClickListener
import com.tencent.TIMCallBack
import com.tencent.TIMFriendshipManager
import com.tencent.TIMMessage
import com.tencent.TIMUserProfile
import com.tencent.TIMValueCallBack
import com.tencent.av.sdk.AVRoomMulti
import com.tencent.ilivesdk.ILiveCallBack
import com.tencent.ilivesdk.ILiveConstants
import com.tencent.ilivesdk.core.ILiveLoginManager
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVLiveConfig
import com.tencent.livesdk.ILVLiveConstants
import com.tencent.livesdk.ILVLiveManager
import com.tencent.livesdk.ILVLiveRoomOption
import com.tencent.livesdk.ILVText
import com.tencent.livesdk.ILVText.ILVTextType
import kotlinx.android.synthetic.main.activity_host_live.bottom_control_view
import kotlinx.android.synthetic.main.activity_host_live.chat_view
import kotlinx.android.synthetic.main.activity_host_live.cl_view
import kotlinx.android.synthetic.main.activity_host_live.danmu_view
import kotlinx.android.synthetic.main.activity_host_live.gift_full_view
import kotlinx.android.synthetic.main.activity_host_live.gift_view
import kotlinx.android.synthetic.main.activity_host_live.heart_layout
import kotlinx.android.synthetic.main.activity_host_live.keyboard
import kotlinx.android.synthetic.main.activity_host_live.live_view
import kotlinx.android.synthetic.main.activity_host_live.msg_list
import kotlinx.android.synthetic.main.activity_host_live.title_view
import kotlinx.android.synthetic.main.activity_host_live.vip_enter
import java.util.Timer
import kotlin.concurrent.timerTask


class HostLiveActivity : AppCompatActivity(),
    BottomControlView.OnControlListener,
    ChatView.OnChatSendListener,
    KeyboardHeightObserver,
    ILVLiveConfig.ILVLiveMsgListener {

  private var mRoomId: Int = -1
  private var mKeyboardHeightProvider: KeyboardHeightProvider? = null
  private val mHeartTimer = Timer()
  private lateinit var mUserObjectId: String
  private lateinit var mHostOperateStatus: HostOperateStatus

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_host_live)

    ImmersionBar.with(this)
        .titleBar(title_view, false)
        .transparentStatusBar()
        .init()

//    bottom_control_view.setIsHost(true)

    registerListener()
    ILVLiveManager.getInstance().setAvVideoView(live_view)
    createRoom()
    mHostOperateStatus = HostOperateStatus()
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
        title_view.setHost((application as MyApplication).getUserProfile())

        mHeartTimer.schedule(timerTask {
          runOnUiThread {
            heart_layout.addHeart(NumUtil.getRandomColor())
          }
        }, 0, 1000)

        mUserObjectId = SPUtils.getInstance().getString("objectId")
      }

      override fun onError(module: String, errCode: Int, errMsg: String) {
        finish()
      }
    })
  }

  private fun quitRoom() {
    val objectId = intent.getStringExtra("objectId")

    AVQuery.doCloudQueryInBackground(
        "delete from RoomInfo where objectId='$objectId'",
        object : CloudQueryCallback<AVCloudQueryResult>() {
          override fun done(p0: AVCloudQueryResult?, p1: AVException?) {
            if (p1 == null) {
              val customCmd = ILVCustomCmd()
              customCmd.type = ILVText.ILVTextType.eGroupMsg
              customCmd.cmd = ILVLiveConstants.ILVLIVE_CMD_LEAVE
              customCmd.destId = ILiveRoomManager.getInstance().imGroupId

              ILVLiveManager.getInstance().sendCustomCmd(customCmd,
                  object : ILiveCallBack<TIMMessage> {
                    override fun onSuccess(data: TIMMessage?) {
                      ILVLiveManager.getInstance().quitRoom(object : ILiveCallBack<Int> {
                        override fun onSuccess(data: Int?) {
                          ILVLiveManager.getInstance().onDestory()
                        }

                        override fun onError(module: String?, errCode: Int, errMsg: String?) {

                        }
                      })
                    }

                    override fun onError(module: String?, errCode: Int, errMsg: String?) {

                    }
                  })
            }
          }
        })
  }

  private fun registerListener() {
    bottom_control_view.setOnControlListener(this)
    chat_view.setOnChatSendListener(this)
    (application as MyApplication).getLiveConfig().liveMsgListener = this
    mKeyboardHeightProvider = KeyboardHeightProvider(this)
    cl_view.post({ mKeyboardHeightProvider?.start() })

    heart_layout.setOnClickListener {
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

  private fun sendGiftMsg(cmd: ILVCustomCmd) {
    ILVLiveManager.getInstance().sendCustomCmd(cmd, object : ILiveCallBack<TIMMessage> {
      override fun onSuccess(data: TIMMessage?) {
        val userProfile = (application as MyApplication).getUserProfile()
        val gson = Gson()
        val giftCmdInfo = gson.fromJson(cmd.param, GiftCmdInfo::class.java) ?: return
        val giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId!!)

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
    avObject.increment("user_exp", giftInfo?.expValue?.div(2))
    avObject.increment("get_nums")
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

                  TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_GET,
                      avData?.getInt("get_nums").toString().toByteArray(), object : TIMCallBack {
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

  private fun showHostControlDialog(view: View) {
    val hostControlDialog = HostControlDialog(this)
    hostControlDialog.setStatus(
        mHostOperateStatus.isBeautyOn(),
        mHostOperateStatus.isVoidOn(),
        mHostOperateStatus.isFlashOn())
    hostControlDialog.showAtTop(view)
    hostControlDialog.setOnControlClickListener(object : OnControlClickListener {
      override fun onBeautyClick() {
        mHostOperateStatus.switchBeauty()
      }

      override fun onFlashClick() {
        mHostOperateStatus.switchFlash()
      }

      override fun onVoiceClick() {
        mHostOperateStatus.switchVoid()
      }

      override fun onCameraClick() {
        mHostOperateStatus.switchCamera()
      }

      override fun onDialogDismiss() {
        bottom_control_view.setOperateRes(true)
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
    showHostControlDialog(view)
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

        cmd?.cmd == ChatType.CMD_CHAT_GIFT -> {
          val gson = Gson()
          val giftCmdInfo = gson.fromJson(cmd.param, GiftCmdInfo::class.java) ?: return
          val giftInfo = GiftInfo.getGiftById(giftCmdInfo.giftId!!)

          calLevel(giftInfo)

          when {
            giftInfo?.type == GiftType.ContinueGift ->
              gift_view.showGift(giftInfo, giftCmdInfo.repeatId, it)

            giftInfo?.type == GiftType.FullScreenGift ->
              gift_full_view.showGift(giftInfo, it)

            giftInfo?.type == GiftType.HeartGift ->
              heart_layout.addHeart(NumUtil.getRandomColor())
          }
        }

        cmd?.cmd == ILVLiveConstants.ILVLIVE_CMD_ENTER -> {
          title_view.addWatcher(it)
          vip_enter.showVipEnter(it)
        }

        cmd?.cmd == ILVLiveConstants.ILVLIVE_CMD_LEAVE -> {
          title_view.removeWatcher(it)
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
    mHeartTimer.cancel()
    mKeyboardHeightProvider?.close()
    quitRoom()
  }
}
