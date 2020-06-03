package com.nextcont.mobilization.store.entity

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

open class ENConversation : RealmObject() {

    @PrimaryKey
    var id: String = ""

    /*服务器唯一id*/
    @Index
    var sid: String = ""

    var name: String = ""

    var chatType: Int = 0

    var participants: RealmList<String> = RealmList()


}