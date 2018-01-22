package com.kop.fastlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kop.fastlive.R
import com.kop.fastlive.model.GiftInfo
import com.kop.fastlive.model.GiftType
import com.kop.fastlive.utils.ImgUtil
import kotlinx.android.synthetic.main.view_gift_item.view.iv_gift
import kotlinx.android.synthetic.main.view_gift_item.view.iv_gift_select
import kotlinx.android.synthetic.main.view_gift_item.view.tv_exp
import kotlinx.android.synthetic.main.view_gift_item.view.tv_gift_name


/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/9 15:32
 */
class GiftGridView : RecyclerView {

  interface OnGiftItemClickListener {
    fun onClick(giftInfo: GiftInfo?)
  }

  private var mOnGiftItemClickListener: OnGiftItemClickListener? = null

  fun setOnGiftItemClickListener(l: OnGiftItemClickListener) {
    mOnGiftItemClickListener = l
  }

  private var mGiftList = mutableListOf<GiftInfo>()
  private lateinit var mAdapter: GridAdapter
  private var mSelectGiftInfo: GiftInfo? = null

  constructor(context: Context?) : super(context) {
    initAdapter()
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    initAdapter()
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,
      defStyle) {
    initAdapter()
  }

  private fun initAdapter() {
    overScrollMode = View.OVER_SCROLL_NEVER
    layoutManager = GridLayoutManager(context, 4)
    mAdapter = GridAdapter()
    adapter = mAdapter
  }

  fun setGiftList(giftList: MutableList<GiftInfo>) {
    mGiftList.clear()
    mGiftList.addAll(giftList)
    mAdapter.notifyItemRangeChanged(0, giftList.size)
  }

  fun setSelectGiftInfo(selectGiftInfo: GiftInfo?) {
    this.mSelectGiftInfo = selectGiftInfo
  }

  inner class GridAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
      return GridViewHolder(
          LayoutInflater.from(parent?.context).inflate(R.layout.view_gift_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
      val giftInfo = mGiftList[position]
      with(holder as GridViewHolder) {
        ImgUtil.load(context, giftInfo.giftResId, itemView.iv_gift)
        if (giftInfo != GiftInfo.Gift_Empty) {
          itemView.tv_exp.text = "${giftInfo.expValue}经验值"
          itemView.tv_gift_name.text = giftInfo.name
          if (giftInfo == mSelectGiftInfo) {
            itemView.iv_gift_select.setImageResource(R.drawable.gift_selected)
          } else {
            if (giftInfo.type === GiftType.ContinueGift) {
              itemView.iv_gift_select.setImageResource(R.drawable.gift_repeat)
            } else if (giftInfo.type === GiftType.FullScreenGift) {
              itemView.iv_gift_select.setImageResource(R.drawable.gift_none)
            }
          }
        } else {
          itemView.tv_exp.text = ""
          itemView.tv_gift_name.text = ""
          itemView.iv_gift_select.setImageResource(R.drawable.gift_none)
        }
      }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
      if (payloads == null || payloads.isEmpty()) {
        onBindViewHolder(holder, position)
      } else {
        val giftInfo = mGiftList[position]
        with(holder as GridViewHolder) {
          if (giftInfo != GiftInfo.Gift_Empty) {
            if (giftInfo == mSelectGiftInfo) {
              itemView.iv_gift_select.setImageResource(R.drawable.gift_selected)
            } else {
              if (giftInfo.type === GiftType.ContinueGift) {
                itemView.iv_gift_select.setImageResource(R.drawable.gift_repeat)
              } else if (giftInfo.type === GiftType.FullScreenGift) {
                itemView.iv_gift_select.setImageResource(R.drawable.gift_none)
              }
            }
          } else {
            itemView.iv_gift_select.setImageResource(R.drawable.gift_none)
          }
        }
      }
    }

    override fun getItemCount() = mGiftList.size

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView), View.OnClickListener {

      init {
        itemView.setOnClickListener(this)
      }

      override fun onClick(view: View?) {
        if (mOnGiftItemClickListener != null && view != null) {
          val currentGiftInfo = mGiftList[layoutPosition]
          if (currentGiftInfo === GiftInfo.Gift_Empty) {
            return
          }
          if (currentGiftInfo == mSelectGiftInfo) {
            mOnGiftItemClickListener?.onClick(null)
          } else {
            mOnGiftItemClickListener?.onClick(currentGiftInfo)
          }
        }
      }
    }
  }
}