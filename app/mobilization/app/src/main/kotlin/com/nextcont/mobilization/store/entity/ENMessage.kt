package com.nextcont.mobilization.store.entity

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by luguangqing on 2018/2/15.
 * 消息入库实体类
 */

open class ENMessage : RealmObject() {

    @PrimaryKey
    var id: String = ""

    /*服务器端唯一id*/
    @Index
    var sid: String = ""

    /*本地发送id*/
    @Index
    var sendLocalId: String = ""

    /*发送人id*/
    var senderId: String = ""

    /*会话id*/
    @Index
    var conversationId: String = ""

    /*消息类型*/
    var contentType: Int = 0

    var sendStatus: Int = 0

    /*已读/未读*/
    var read: Boolean = true

    /*时戳*/
    var time: Long = 0 // google数据以微秒为单位

    /*显示时间,入库,用于ui显示,0不显示*/
    var displayTime: Long = 0L

}
