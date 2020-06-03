package com.nextcont.mobilization.store.entity

import io.realm.RealmObject
import io.realm.annotations.Index

/**
 * Created by luguangqing on 2018/2/15.
 * 消息入库实体类
 */

open class ENTextContent : RealmObject() {

    @Index
    var targetId: String = ""

    var plainText: String = ""

}
