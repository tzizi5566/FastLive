package com.kop.fastlive.module.hostlive

import android.hardware.Camera
import com.tencent.ilivesdk.ILiveConstants
import com.tencent.ilivesdk.core.ILiveLoginManager
import com.tencent.ilivesdk.core.ILiveRoomManager
import java.lang.Exception

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2018/1/21 13:11
 */
class HostOperateStatus {

  private var isBeautyOn = false
  private var isVoidOn = true
  private var isFlashOn = false
  private var mCurrentCameraId = ILiveConstants.FRONT_CAMERA

  fun switchBeauty() {
    isBeautyOn = !isBeautyOn
    if (isBeautyOn) {
      ILiveRoomManager.getInstance().enableBeauty(1f)
      ILiveRoomManager.getInstance().enableWhite(1f)
    } else {
      ILiveRoomManager.getInstance().enableBeauty(0f)
      ILiveRoomManager.getInstance().enableWhite(0f)
    }
  }

  fun isBeautyOn(): Boolean {
    return isBeautyOn
  }

  fun switchVoid() {
    isVoidOn = !isVoidOn
    ILiveRoomManager.getInstance().enableMic(isVoidOn)
  }

  fun isVoidOn(): Boolean {
    return isVoidOn
  }

  fun switchCamera() {
    mCurrentCameraId = if (mCurrentCameraId == ILiveConstants.FRONT_CAMERA) {
      ILiveConstants.BACK_CAMERA
    } else {
      ILiveConstants.FRONT_CAMERA
    }
    ILiveRoomManager.getInstance().switchCamera(mCurrentCameraId)
  }

  fun switchFlash() {
    if (mCurrentCameraId == ILiveConstants.FRONT_CAMERA) {
      isFlashOn = false
      return
    }

    val camera = ILiveLoginManager.getInstance().avConext.videoCtrl.camera
    if (camera == null || camera !is Camera) {
      isFlashOn = false
      return
    }

    val parameters = camera.parameters
    if (parameters == null) {
      isFlashOn = false
      return
    }

    if (isFlashOn) {
      parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
    } else {
      parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
    }

    try {
      camera.parameters = parameters
      isFlashOn = !isFlashOn
    } catch (e: Exception) {
      isFlashOn = false
    }
  }

  fun isFlashOn(): Boolean {
    return isFlashOn
  }
}