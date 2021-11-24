package com.rnesewa

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.esewa.android.sdk.payment.ESewaConfiguration
import com.esewa.android.sdk.payment.ESewaPayment
import com.esewa.android.sdk.payment.ESewaPaymentActivity
import com.facebook.react.bridge.*
import org.json.JSONObject


class RnEsewaModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ActivityEventListener {

  companion object {
    private val TAG = "RneSewaModule"
    private val ESEWA_PAYMENT_REQUEST = 1001
  }

  private val responseHelper: ResponseHelper = ResponseHelper()
  private var callback: Callback? = null

  init {
    reactContext.addActivityEventListener(this)
  }

  override fun getName(): String {
    return "RNEsewaSDK"
  }

  @ReactMethod
  fun initiatePayment(
    options: ReadableMap,
    callback: Callback
  ) {
    Log.i(TAG, "initiatePayment")

    if (this.callback != null) {
      responseHelper.invokeError(callback, "Previous payment is in progress")
      return
    }

    if (options.getString("clientId").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "clientId is required")
      return
    }

    if (options.getString("clientSecret").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "clientSecret is required")
      return
    }

    if (options.getString("productPrice").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "productPrice is required")
      return
    }

    if (options.getString("productName").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "productName is required")
      return
    }

    if (options.getString("productId").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "productId is required")
      return
    }
    if (options.getString("callbackUrl").isNullOrEmpty()) {
      responseHelper.invokeError(callback, "callbackUrl is required")
      return
    }

    this.callback = callback
    val eSewaConfiguration: ESewaConfiguration = ESewaConfiguration()
      .clientId(options.getString("clientId"))
      .secretKey(options.getString("clientSecret"))
      .environment(if (options.getBoolean("isDevelopment")) ESewaConfiguration.ENVIRONMENT_TEST else ESewaConfiguration.ENVIRONMENT_PRODUCTION)

    val eSewaPayment = ESewaPayment(
      options.getString("productPrice"),
      options.getString("productName"),
      options.getString("productId"),
      options.getString("callbackUrl")
    )
    val intent = Intent(reactApplicationContext, ESewaPaymentActivity::class.java)
    intent.putExtra(ESewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
    intent.putExtra(ESewaPayment.ESEWA_PAYMENT, eSewaPayment)
    try {
      reactApplicationContext.startActivityForResult(intent, ESEWA_PAYMENT_REQUEST, Bundle())
    } catch (e: ActivityNotFoundException) {
      Log.e(TAG, "authenticate(), error $e")
      responseHelper.invokeError(callback, "Cannot launch eSewa payment gateway")
      this.callback = null
    }
  }

  override fun onActivityResult(
    activity: Activity?,
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    Log.d(TAG, "onActivityResult : ${data?.toString()}")

    if (passResult(requestCode)) return

    Log.d(TAG, "onActivityResult : cleanResponse()")
    responseHelper.cleanResponse()

    when (resultCode) {
      Activity.RESULT_OK -> {
        Log.d(TAG, "onActivityResult : RESULT_OK")
        val proofOfPayment = try {
          JSONObject(data?.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE).orEmpty())
        } catch (e: Exception) {
          JSONObject()
        }
        Log.i("Proof of Payment", proofOfPayment.toString())

        responseHelper.putBoolean("completed", true)
        responseHelper.putJsonObject("proofOfPayment", proofOfPayment)
        responseHelper.invokeResponse(callback)
        callback = null
      }
      ESewaPayment.RESULT_EXTRAS_INVALID -> {
        Log.d(TAG, "onActivityResult : RESULT_EXTRAS_INVALID")
        var message = "Provided credentials is invalid"
        val rawResponse = data?.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE).orEmpty()
        val proofOfPayment = try {
          JSONObject(rawResponse).apply {
            put("errorMessage", "Provided credentials is invalid")
          }
        } catch (e: Exception) {
          message = rawResponse
          JSONObject()
        }
        Log.i("Proof of Payment", proofOfPayment.toString())

        responseHelper.putBoolean("hasError", true)
        responseHelper.putString("errorMessage", message)
        responseHelper.putJsonObject("proofOfPayment", proofOfPayment)
        responseHelper.invokeResponse(callback)
        callback = null
      }
      Activity.RESULT_CANCELED -> {
        Log.d(TAG, "onActivityResult : RESULT_CANCELED")
        responseHelper.invokeCancel(callback)
        callback = null
      }
      else -> {
        Log.d(TAG, "onActivityResult : unknown")
        responseHelper.invokeUnknown(callback)
        callback = null
      }
    }

  }

  override fun onNewIntent(intent: Intent?) {
    Log.d(TAG, "onNewIntent : ${intent?.data?.toString()}")
  }

  private fun passResult(requestCode: Int): Boolean {
    Log.d(
      TAG,
      "passResult(), requestCode: ${requestCode}, isCallbackAvailable: ${callback != null}"
    )
    return (callback == null && requestCode != ESEWA_PAYMENT_REQUEST)
  }

}
