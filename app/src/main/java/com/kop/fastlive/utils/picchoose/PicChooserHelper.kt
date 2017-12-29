package com.kop.fastlive.utils.picchoose

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.kop.fastlive.MyApplication
import com.kop.fastlive.utils.QnUploadHelper
import com.kop.fastlive.utils.QnUploadHelper.UploadCallBack
import com.kop.fastlive.utils.picchoose.PicChooserType.AVATAR
import com.kop.fastlive.utils.picchoose.PicChooserType.COVER
import com.kop.fastlive.widget.PicChooseDialog
import com.kop.fastlive.widget.PicChooseDialog.OnDialogClickListener
import com.qiniu.android.http.ResponseInfo
import java.io.File

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/25 13:59
 */
class PicChooserHelper(activity: Activity, picType: PicChooserType = AVATAR) {

  interface OnChooseResultListener {
    fun onSuccess(url: String)

    fun onFail(msg: String)
  }

  private var mOnChooserResultListener: OnChooseResultListener? = null

  fun setOnChooseResultListener(l: OnChooseResultListener) {
    mOnChooserResultListener = l
  }

  private val mActivity = activity
  private val CROP = 0
  private val FROM_ALBUM = 1
  private val FROM_CAMERA = 2

  private var mCameraUri: Uri? = null
  private var mCropUri: Uri? = null

  private var mPicType = picType

  fun showDialog() {
    val dialog = PicChooseDialog(mActivity)
    dialog.setOnDialogClickListener(object : OnDialogClickListener {
      override fun onCamera() {
        choosePicFromCamera()
      }

      override fun onAlbum() {
        choosePicFromAlbum()
      }
    })
    dialog.show()
  }

  private fun choosePicFromCamera() {
    mCameraUri = getCameraUri()
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val contentValues = ContentValues(1)
      contentValues.put(MediaStore.Images.Media.DATA, mCameraUri?.path)
      val uri = getImageContentUri(mCameraUri!!)
      intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
      mActivity.startActivityForResult(intent, FROM_CAMERA)
    } else {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri)
      mActivity.startActivityForResult(intent, FROM_CAMERA)
    }
  }

  private fun choosePicFromAlbum() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    mActivity.startActivityForResult(intent, FROM_ALBUM)
  }

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      FROM_ALBUM -> {
        if (resultCode == Activity.RESULT_OK) {
          val uri = data?.data
          startCrop(uri)
        }
      }

      CROP -> {
        if (resultCode == Activity.RESULT_OK) {
          upload2QiNiu(mCropUri?.path)
        }
      }

      FROM_CAMERA -> {
        if (resultCode == Activity.RESULT_OK) {
          startCrop(mCameraUri)
        }
      }
    }
  }

  private fun upload2QiNiu(path: String?) {
    val file = File(path)
    QnUploadHelper.uploadPic(path!!, file.name,
        object : UploadCallBack {
          override fun success(url: String) {
            mOnChooserResultListener?.onSuccess(url)
          }

          override fun fail(key: String, info: ResponseInfo) {
            mOnChooserResultListener?.onFail(info.error)
          }

        })
  }

  private fun startCrop(uri: Uri?) {
    mCropUri = getCropUri()
    val intent = Intent("com.android.camera.action.CROP")
    intent.putExtra("crop", true)
    intent.putExtra("return-data", false)
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

    if (mPicType == AVATAR) {
      intent.putExtra("aspectX", 300)
      intent.putExtra("aspectY", 300)
      intent.putExtra("outputX", 300)
      intent.putExtra("outputY", 300)
    } else if (mPicType == COVER) {
      intent.putExtra("aspectX", 500)
      intent.putExtra("aspectY", 300)
      intent.putExtra("outputX", 500)
      intent.putExtra("outputY", 300)
    }

    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val scheme = uri!!.scheme
      if (scheme == "content") {
        intent.setDataAndType(uri, "image/*")
      } else {
        val contentUri = getImageContentUri(uri)
        intent.setDataAndType(contentUri, "image/*")
      }
      intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri)
      mActivity.startActivityForResult(intent, CROP)
    } else {
      intent.setDataAndType(uri, "image/*")
      intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri)
      mActivity.startActivityForResult(intent, CROP)
    }
  }

  private fun getCameraUri(): Uri? {
    val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/" + mActivity.application.applicationInfo.packageName
    val dir = File(dirPath)
    if (!dir.exists() || dir.isFile) {
      dir.mkdirs()
    }
    val selfProfile = (mActivity.application as MyApplication).getSelfProfile()
    val fileName = if (selfProfile != null) {
      System.currentTimeMillis().toString() + selfProfile.identifier + ".jpg"
    } else {
      System.currentTimeMillis().toString() + ".jpg"
    }

    val jpgFile = File(dir, fileName)
    if (jpgFile.exists()) {
      jpgFile.delete()
    }
    return Uri.fromFile(jpgFile)
  }

  private fun getCropUri(): Uri? {
    val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/" + mActivity.application.applicationInfo.packageName
    val dir = File(dirPath)
    if (!dir.exists() || dir.isFile) {
      dir.mkdirs()
    }
    val selfProfile = (mActivity.application as MyApplication).getSelfProfile()
    val fileName = if (selfProfile != null) {
      System.currentTimeMillis().toString() + selfProfile.identifier + "_crop.jpg"
    } else {
      System.currentTimeMillis().toString() + "_crop.jpg"
    }

    val jpgFile = File(dir, fileName)
    if (jpgFile.exists()) {
      jpgFile.delete()
    }
    jpgFile.createNewFile()
    return Uri.fromFile(jpgFile)
  }

  /**
   * 转换 content:// uri
   */
  private fun getImageContentUri(uri: Uri): Uri? {
    val filePath = uri.path
    val cursor = mActivity.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
        arrayOf(filePath), null)
    cursor.close()

    return if (cursor != null && cursor.moveToFirst()) {
      val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
      val baseUri = Uri.parse("content://media/external/images/media")
      Uri.withAppendedPath(baseUri, "" + id)
    } else {
      val values = ContentValues()
      values.put(MediaStore.Images.Media.DATA, filePath)
      mActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
  }
}