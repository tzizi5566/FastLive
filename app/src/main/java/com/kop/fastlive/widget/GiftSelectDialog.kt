package com.kop.fastlive.widget

import android.app.Activity
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
import com.kop.fastlive.widget.GiftGridView.OnGiftItemClickListener
import com.tencent.ilivesdk.core.ILiveRoomManager
import com.tencent.livesdk.ILVCustomCmd
import com.tencent.livesdk.ILVText.ILVTextType
import kotlinx.android.synthetic.main.dialog_gift_select.view.btn_send
import kotlinx.android.synthetic.main.dialog_gift_select.view.iv_indicator_one
import kotlinx.android.synthetic.main.dialog_gift_select.view.iv_indicator_two
import kotlinx.android.synthetic.main.dialog_gift_select.view.vp_view

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

  override fun onClick(v: View?) {
    if (onGiftSendListener != null) {
      val ilvCustomCmd = ILVCustomCmd()
      ilvCustomCmd.type = ILVTextType.eGroupMsg
      ilvCustomCmd.cmd = ChatType.CMD_CHAT_GIFT
      ilvCustomCmd.destId = ILiveRoomManager.getInstance().imGroupId
      val giftCmdInfo = GiftCmdInfo(mSelectGiftInfo?.giftId, "")
      val gson = Gson()
      ilvCustomCmd.param = gson.toJson(giftCmdInfo)

      onGiftSendListener?.onGiftSendClick(ilvCustomCmd)
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
          mSelectGiftInfo = giftInfo

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
}