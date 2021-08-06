package com.rnesewa

import com.facebook.react.bridge.*
import org.json.JSONArray
import org.json.JSONObject


class ResponseHelper {
  private var response = Arguments.createMap()

  fun cleanResponse() {
    response = Arguments.createMap()
  }

  fun getResponse(): WritableMap = response

  private fun convertJSONObject(jsonObject: JSONObject): WritableMap {
    val map = WritableNativeMap()
    val iterator = jsonObject.keys()
    while (iterator.hasNext()) {
      val key = iterator.next()

      when (val value = jsonObject.get(key)) {
        is JSONObject -> map.putMap(key, convertJSONObject(value))
        is JSONArray -> map.putArray(key, convertJSONArray(value))

        is Boolean -> map.putBoolean(key, value)
        is Int -> map.putInt(key, value)
        is Double -> map.putDouble(key, value)
        is String -> map.putString(key, value)
        else -> map.putString(key, value.toString())
      }
    }
    return map
  }

  private fun convertJSONArray(jsonArray: JSONArray): WritableNativeArray {
    val map = WritableNativeArray()

    for (index in 0 until jsonArray.length()) {

      when (val value = jsonArray[index]) {
        is JSONObject -> map.pushMap(convertJSONObject(value))
        is JSONArray -> map.pushArray(convertJSONArray(value))
        is Boolean -> map.pushBoolean(value)
        is Int -> map.pushInt(value)
        is Double -> map.pushDouble(value)
        is String -> map.pushString(value)
        else -> map.pushString(value.toString())
      }
    }


    return map
  }

  fun putJsonObject(key: String, jsonObject: JSONObject) {
    response.putMap(key, convertJSONObject(jsonObject))
  }

  fun putString(key: String, value: String) {
    response.putString(key, value)
  }

  fun putInt(key: String, value: Int) {
    response.putInt(key, value)
  }

  fun putBoolean(key: String, value: Boolean) {
    response.putBoolean(key, value)
  }

  fun putDouble(key: String, value: Double) {
    response.putDouble(key, value)
  }

  fun invokeCancel(callback: Callback?) {
    cleanResponse()
    response.putBoolean("didCancel", true)
    invokeResponse(callback)
  }

  fun invokeUnknown(callback: Callback?) {
    cleanResponse()
    response.putBoolean("hasError", true)
    response.putString("errorMessage", "Could not process the payment due to an unknown error")
    invokeResponse(callback)
  }

  fun invokeError(callback: Callback?, error: String) {
    cleanResponse()
    response.putBoolean("hasError", true)
    response.putString("errorMessage", error)
    invokeResponse(callback)
  }

  fun invokeResponse(callback: Callback?) {
    callback?.invoke(response)
  }
}
