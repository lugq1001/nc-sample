package com.nextcont.mobilization.store.entity

import io.realm.RealmObject
import io.realm.annotations.Index

/**
 * Created by luguangqing on 2018/2/15.
 * 消息入库实体类
 */

open class ENVoiceContent : RealmObject() {

    @Index
    var targetId: String = ""

    var code: String = ""

    var played: Boolean = true

    var downloadUrl: String = ""

    var duration: Int = 1

    /**
     * 本地文件地址
     */
    var localPath: String = ""

    var shareLink: String = ""
    
}
