package com.zak.sidilan.util

import android.content.Context
import com.orhanobut.hawk.Hawk

class HawkManager(context: Context) {

    init {
        Hawk.init(context).build()
    }

    fun saveData(key: String, value: Any) {
        Hawk.put(key, value)
    }

    fun <T> retrieveData(key: String): T? {
        return Hawk.get<T>(key)
    }

    fun deleteData(key: String) {
        Hawk.delete(key)
    }
}