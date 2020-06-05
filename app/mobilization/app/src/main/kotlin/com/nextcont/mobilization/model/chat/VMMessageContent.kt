package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.store.entity.ENImageContent
import com.nextcont.mobilization.store.entity.ENTextContent
import com.nextcont.mobilization.store.entity.ENVideoContent
import com.nextcont.mobilization.store.entity.ENVoiceContent
import com.squareup.moshi.JsonClass
import io.realm.Realm

abstract class VMMessageContent {

    abstract val type: Type

    /**
     * 对应VMMessage localId
     */
    var targetId: String = ""

    enum class Type(val code: Int) {

        Text(0),
        Image(1),
        Video(2),
        Voice(3);

        companion object {
            fun from(code: Int): Type {
                Type.values().forEach {
                    if (it.code == code) {
                        return it
                    }
                }
                return Text
            }
        }
    }

    companion object {

        fun delete(realm: Realm, contentType: Int, targetId: String) {
            when (contentType) {
                Type.Image.code -> {
                    realm.where(ENImageContent::class.java).equalTo("targetId", targetId).findAll().deleteAllFromRealm()
                }
                Type.Video.code -> {
                    realm.where(ENVideoContent::class.java).equalTo("targetId", targetId).findAll().deleteAllFromRealm()
                }
                Type.Text.code -> {
                    realm.where(ENTextContent::class.java).equalTo("targetId", targetId).findAll().deleteAllFromRealm()
                }
                Type.Voice.code -> {
                    realm.where(ENVoiceContent::class.java).equalTo("targetId", targetId).findAll().deleteAllFromRealm()
                }
            }
        }
    }

}