package com.nextcont.mobilization.util

import android.content.Context
import android.os.Build
import java.math.BigInteger
import java.security.MessageDigest

object AppUtil {

    @Suppress("DEPRECATION")
    fun appVersion(context: Context): String {
        val packageManager = context.packageManager
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return "${packInfo.versionName}.${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packInfo.longVersionCode else packInfo.versionCode}"
    }

}

val String.md5: String
    get() {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }