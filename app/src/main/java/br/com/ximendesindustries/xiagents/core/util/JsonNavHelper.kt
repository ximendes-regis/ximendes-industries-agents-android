package br.com.ximendesindustries.xiagents.core.util

import android.net.Uri
import com.squareup.moshi.Moshi

object JsonNavHelper {
     val moshi = Moshi.Builder().build()

    inline fun <reified T> toRouteArg(obj: T): String {
        val adapter = moshi.adapter(T::class.java)
        val json = adapter.toJson(obj)
        return Uri.encode(json)
    }

    inline fun <reified T> fromRouteArg(encoded: String): T? {
        val adapter = moshi.adapter(T::class.java)
        return adapter.fromJson(Uri.decode(encoded))
    }
}
