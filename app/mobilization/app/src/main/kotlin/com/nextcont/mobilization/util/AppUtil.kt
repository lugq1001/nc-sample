package com.nextcont.mobilization.util

import android.content.Context
import android.os.Build

object AppUtil {

    @Suppress("DEPRECATION")
    fun appVersion(context: Context): String {
        val packageManager = context.packageManager
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return "${packInfo.versionName}.${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packInfo.longVersionCode else packInfo.versionCode}"
    }

}