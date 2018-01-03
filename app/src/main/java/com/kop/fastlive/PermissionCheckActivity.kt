package com.kop.fastlive

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kop.fastlive.utils.picchoose.PicChooserHelper
import com.kop.fastlive.utils.callblack.CallbackManager
import com.kop.fastlive.utils.callblack.CallbackType
import com.kop.fastlive.utils.picchoose.PicChooserType
import com.kop.fastlive.utils.picchoose.PicChooserType.AVATAR
import com.kop.fastlive.utils.picchoose.PicChooserType.COVER
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/28 16:12
 */
@SuppressLint("Registered")
@RuntimePermissions
abstract class PermissionCheckActivity : AppCompatActivity() {

  private var mPicType: PicChooserType? = null
  private var mPicChooseHelp: PicChooserHelper? = null

  abstract fun setPicType(): PicChooserType

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mPicType = setPicType()
    mPicChooseHelp = PicChooserHelper(this, mPicType!!)
  }

  @NeedsPermission(
      Manifest.permission.CAMERA,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
  fun choosePic() {
    mPicChooseHelp?.setOnChooseResultListener(object : PicChooserHelper.OnChooseResultListener {
      override fun onSuccess(url: String) {
        if (mPicType == AVATAR) {
          val callback = CallbackManager.getInstance().getCallback(CallbackType.CHOOSE_PIC_AVATAR)
          callback?.executeCallback(url)
        } else if (mPicType == COVER) {
          val callback = CallbackManager.getInstance().getCallback(CallbackType.CHOOSE_PIC_COVER)
          callback?.executeCallback(url)
        }
      }

      override fun onFail(msg: String) {
        Toast.makeText(this@PermissionCheckActivity, "选择失败：$msg", Toast.LENGTH_SHORT).show()
      }

    })
    mPicChooseHelp?.showDialog()
  }

  @NeedsPermission(
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO)
  fun createRoom() {
    val callback = CallbackManager.getInstance().getCallback(CallbackType.CREATE_ROOM)
    callback?.executeCallback("")
  }

  @OnShowRationale(
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
  fun onCameraRationale(request: PermissionRequest) {
    showRationaleDialog(request)
  }

  @OnPermissionDenied(
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
  fun onCameraDenied() {
    Toast.makeText(this, "权限不被允许！", Toast.LENGTH_LONG).show()
  }

  @OnNeverAskAgain(
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
  fun onCameraNever() {
    Toast.makeText(this, "权限被永久拒绝！", Toast.LENGTH_LONG).show()
  }

  private fun showRationaleDialog(request: PermissionRequest) {
    AlertDialog.Builder(this)
        .setPositiveButton("同意使用"
        ) { _, _ -> request.proceed() }
        .setNegativeButton("拒绝使用"
        ) { _, _ -> request.cancel() }
        .setCancelable(false)
        .setMessage("APP需要相应权限才能正常使用！")
        .show()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
      grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // NOTE: delegate the permission handling to generated function
    onRequestPermissionsResult(requestCode, grantResults)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (mPicChooseHelp != null) {
      mPicChooseHelp?.onActivityResult(requestCode, resultCode, data)
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }
}