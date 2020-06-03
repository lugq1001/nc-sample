package com.nextcont.mobilization.store.entity

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * 联系人入库实体类
 *
 */
open class ENContact : RealmObject() {

    @PrimaryKey
    var id: String = ""

    /*服务器唯一id*/
    @Index
    var sid: String = ""
    /*昵称*/
    var nickname: String = ""

}
