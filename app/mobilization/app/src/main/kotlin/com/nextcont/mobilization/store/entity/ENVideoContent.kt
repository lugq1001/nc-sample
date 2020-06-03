package com.nextcont.mobilization.store.entity

import io.realm.RealmObject
import io.realm.annotations.Index

/**
 * Created by luguangqing on 2018/2/15.
 * 消息入库实体类
 */

open class ENVideoContent : RealmObject() {

    @Index
    var targetId: String = ""

    /**
     * 下载地址
     */
    var downloadUrl = ""

    /**
     * 图片地址
     */
    var imageUrl = ""

    /**
     * 缩略图地址
     */
    var thumbnailUrl = ""

    /**
     * 发送时上传至服务器后的地址
     */
    var remoteUrl = ""
    /**
     * 本地文件地址
     */
    var localPath = ""

}
