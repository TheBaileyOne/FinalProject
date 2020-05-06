package com.example.studentlifeapp.util

import android.content.Intent
import com.google.gson.Gson
import com.google.gson.GsonBuilder

const val DEFAULT_NAME = "object"

object IntentUtil {
    @Suppress("SpellCheckingInspection")
    val gson: Gson = GsonBuilder().create()
}

/**
 * Put data as Json file for intent exra
 * @param name reference for intent extra
 */
fun Intent.putExtraJson(name: String, src: Any) {
    putExtra(name, IntentUtil.gson.toJson(src))
}

/**
 * Get the value from Json Intent extra
 */
fun <T> Intent.getJsonExtra(name: String, `class`: Class<T>): T? {
    val stringExtra = getStringExtra(name)
    if (stringExtra != null) {
        return IntentUtil.gson.fromJson<T>(stringExtra, `class`)
    }
    return null
}
