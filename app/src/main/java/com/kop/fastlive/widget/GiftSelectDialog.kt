package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Message
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import com.google.gson.Gson
import com.kop.fastlive.R
import com.kop.fastlive.model.ChatType
import com.kop.fastlive.model.GiftCmdInfo
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.model.GiftType
import com.kop.fastlive.widget.GiftGridView.OnGiftItemClickListener
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVText.ILVTextType
import kotlinx.android.synthetic.main.dialog_gift_select.view.btn_send
import kotlinx.android.synthetic.main.dialog_gift_select.view.iv_indicator_one
import kotlinx.android.synthetic.main.dialog_gift_select.view.iv_indicator_two
import kotlinx.android.synthetic.main.dialog_gift_select.view.vp_view
import java.lang.ref.WeakReference

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/9 14:17
 */
class GiftSelectDialog(val activity: Activity) : TransParentDialog(
    activity), OnPageChangeListener, OnClickListener {

  private var onGiftSendListener: OnGiftSendListener? = null

  interface OnGiftSendListener {
    fun onGiftSendClick(customCmd: ILVCustomCmd)
  }

  fun setGiftSendListener(l: OnGiftSendListener) {
    onGiftSendListener = l
  }

  private var mView: View = LayoutInflater.from(activity).inflate(R.layout.dialog_gift_select, null,
      false)
  private var mGiftList = mutableListOf<GiftInfo>()
  private val mPageViews = mutableListOf<GiftGridView>()
  private lateinit var mAdapter: GiftAdapter
  private var mSelectGiftInfo: GiftInfo? = null
  private var mRepeatId = ""
  private var mLeftTime = 3
  private val mHandler = MyHandler(this)

  companion object {
    private val WHAT_UPDATE_TIME = 0
    private val WHAT_MINUTES_TIME = 1
  }

  init {
    setContentView(mView)
    setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)

    addGifts()
    initAdapter()
    registerListener()
  }

  private fun addGifts() {
    mGiftList.add(GiftInfo.Gift_BingGun)
    mGiftList.add(GiftInfo.Gift_BingJiLing)
    mGiftList.add(GiftInfo.Gift_MeiGui)
    mGiftList.add(GiftInfo.Gift_PiJiu)
    mGiftList.add(GiftInfo.Gift_HongJiu)
    mGiftList.add(GiftInfo.Gift_Hongbao)
    mGiftList.add(GiftInfo.Gift_ZuanShi)
    mGiftList.add(GiftInfo.Gift_BaoXiang)
    mGiftList.add(GiftInfo.Gift_BaoShiJie)
  }

  private fun initAdapter() {
    mAdapter = GiftAdapter()
    mView.vp_view.adapter = mAdapter
    mView.vp_view.addOnPageChangeListener(this)
  }

  private fun registerListener() {
    mView.btn_send.setOnClickListener(this)
  }

  private fun startRepeatTimer() {
    stopRepeatTimer()
    mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_TIME, 200)
  }

  private fun stopRepeatTimer() {
    mHandler.removeMessages(WHAT_UPDATE_TIME)
    mHandler.removeMessages(WHAT_MINUTES_TIME)
    mLeftTime = 3
  }

  override fun onClick(v: View?) {
    if (mRepeatId.isEmpty()) {
      mRepeatId = System.currentTimeMillis().toString()
    }

    if (onGiftSendListener != null) {
      val ilvCustomCmd = ILVCustomCmd()
      ilvCustomCmd.type = ILVTextType.eGroupMsg
      ilvCustomCmd.cmd = ChatType.CMD_CHAT_GIFT
      ilvCustomCmd.destId = ILiveRoomManager.getInstance().imGroupId
      val giftCmdInfo = GiftCmdInfo(mSelectGiftInfo?.giftId, mRepeatId)
      val gson = Gson()
      ilvCustomCmd.param = gson.toJson(giftCmdInfo)

      onGiftSendListener?.onGiftSendClick(ilvCustomCmd)

      if (mSelectGiftInfo?.type == GiftType.ContinueGift) {
        startRepeatTimer()
      }
    }
  }

  override fun onPageScrollStateChanged(state: Int) {

  }

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

  }

  override fun onPageSelected(position: Int) {
    if (position == 0) {
      mView.iv_indicator_one.setImageResource(R.drawable.ind_s)
      mView.iv_indicator_two.setImageResource(R.drawable.ind_uns)
    } else if (position == 1) {
      mView.iv_indicator_one.setImageResource(R.drawable.ind_uns)
      mView.iv_indicator_two.setImageResource(R.drawable.ind_s)
    }
  }

  override fun show() {
    val window = dialog.window
    window.setWindowAnimations(R.style.dialogAnimation)
    val lp = window.attributes
    lp.gravity = Gravity.BOTTOM
    dialog.window.attributes = lp

    super.show()
  }

  inner class GiftAdapter : PagerAdapter() {

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
      return view == `object`
    }

    override fun getCount() = 2

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
      val giftGridView = GiftGridView(activity)
      //确定当前页面所展示的gift的list
      var endIndex = (position + 1) * 8
      var emptyNum = 0
      //最后一页的边界处理
      if (endIndex > mGiftList.size) {
        emptyNum = endIndex - mGiftList.size
        endIndex = mGiftList.size
      }
      val targetInfos = mGiftList.subList(position * 8, endIndex)
      //超出边界的，用空填充。保证每个页面都有item
      for (i in 0 until emptyNum) {
        targetInfos.add(GiftInfo.Gift_Empty)
      }
      giftGridView.setGiftList(targetInfos)

      giftGridView.viewTreeObserver.addOnGlobalLayoutListener {
        val view = giftGridView.getChildAt(0)
        view?.let {
          val height = view.height
          val layoutParams = container?.layoutParams
          layoutParams?.height = height * 2
          container?.layoutParams = layoutParams
        }
      }

      container?.addView(giftGridView)
      mPageViews.add(giftGridView)

      giftGridView.setOnGiftItemClickListener(object : OnGiftItemClickListener {
        override fun onClick(giftInfo: GiftInfo?) {
          stopRepeatTimer()

          mView.btn_send.text = "发送"
          mSelectGiftInfo = giftInfo
          mRepeatId = ""

          if (giftInfo != null) {
            mView.btn_send.visibility = View.VISIBLE
          } else {
            mView.btn_send.visibility = View.INVISIBLE
          }

          for (gift in mPageViews) {
            gift.setSelectGiftInfo(giftInfo)
            gift.adapter.notifyItemRangeChanged(0, gift.adapter.itemCount, 1)
          }
        }
      })

      return giftGridView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
      super.destroyItem(container, position, `object`)
      container?.removeView(`object` as View)
      mPageViews.remove(`object`)
    }
  }

  class MyHandler(dialog: GiftSelectDialog) : Handler() {

    private var mActivity: WeakReference<GiftSelectDialog>? = null

    init {
      mActivity = WeakReference(dialog)
    }

    @SuppressLint("SetTextI18n")
    override fun handleMessage(msg: Message?) {
      super.handleMessage(msg)
      val selectDialog = mActivity?.get()
      selectDialog?.let {
        val what = msg?.what
        when {
          WHAT_UPDATE_TIME == what -> {
            selectDialog.mView.btn_send.text = "发送(${selectDialog.mLeftTime}s)"
            selectDialog.mHandler.sendEmptyMessageDelayed(WHAT_MINUTES_TIME, 300)
          }

          WHAT_MINUTES_TIME == what -> {
            selectDialog.mLeftTime--
            if (selectDialog.mLeftTime > 0) {
              selectDialog.mView.btn_send.text = "发送(${selectDialog.mLeftTime}s)"
              selectDialog.mHandler.sendEmptyMessageDelayed(WHAT_MINUTES_TIME, 300)
            } else {
              selectDialog.mView.btn_send.text = "发送"
              selectDialog.mRepeatId = ""
            }
          }

          else -> {

          }
        }
      }
    }
  }
}