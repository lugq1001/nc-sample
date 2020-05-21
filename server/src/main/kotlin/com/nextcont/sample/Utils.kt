package com.nextcont.sample

import java.math.BigInteger
import java.security.MessageDigest

val String.md5: String
get() {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}