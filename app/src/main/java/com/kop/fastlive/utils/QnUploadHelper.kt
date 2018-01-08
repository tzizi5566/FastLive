package com.kop.fastlive.utils

import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.utils.UrlSafeBase64
import org.json.JSONObject
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 功    能: //TODO
 * 创 建 人: KOP
 * 创建日期: 2017/12/25 15:34
 */
class QnUploadHelper {

  companion object {
    private const val MAC_NAME = "HmacSHA1"
    private const val ENCODING = "UTF-8"

    //七牛后台的key
    private lateinit var AccessKey: String
    //七牛后台的secret
    private lateinit var SecretKey: String

    private lateinit var Domain: String
    private lateinit var BucketName: String

    private lateinit var configuration: Configuration

    private val delayTimes = 3029414400L //有效时间

    fun init(accessKey: String, secretKey: String, domain: String, bucketName: String) {
      AccessKey = accessKey
      SecretKey = secretKey
      Domain = domain
      BucketName = bucketName

      configuration = Configuration.Builder().build()
    }

    /**
     * 上传
     *
     * //     * @param bucketName bucketName的名字
     * @param path   上传文件的路径地址
     */
    fun uploadPic(path: String, keys: String, callBack: UploadCallBack) {
      try {
        // 1:第一种方式 构造上传策略
        val json = JSONObject()
        json.put("deadline", delayTimes)
        json.put("scope", BucketName)
        val encodedPutPolicy = UrlSafeBase64.encodeToString(json.toString().toByteArray())
        val sign = HmacSHA1Encrypt(encodedPutPolicy, SecretKey!!)
        val encodedSign = UrlSafeBase64.encodeToString(sign)
        val uploadToken = ("$AccessKey:$encodedSign:$encodedPutPolicy")
        val uploadManager = UploadManager(configuration)

        uploadManager.put(path, keys, uploadToken, { key, info, response ->
          if (info.isOK) {
            val picUrl = Domain + keys
            callBack.success(picUrl)
          } else {
            callBack.fail(key, info)
          }
        }, null)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    private fun HmacSHA1Encrypt(encryptText: String, encryptKey: String): ByteArray {
      val data = encryptKey.toByteArray(charset(ENCODING))
      // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
      val secretKey = SecretKeySpec(data, MAC_NAME)
      // 生成一个指定 Mac 算法 的 Mac 对象
      val mac = Mac.getInstance(MAC_NAME)
      // 用给定密钥初始化 Mac 对象
      mac.init(secretKey)
      val text = encryptText.toByteArray(charset(ENCODING))
      // 完成 Mac 操作
      return mac.doFinal(text)
    }

    interface UploadCallBack {
      fun success(url: String)

      fun fail(key: String, info: ResponseInfo)
    }
  }
}