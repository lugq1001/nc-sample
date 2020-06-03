package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.store.entity.ENTextContent
import io.realm.Realm

class VMMessageContentText: VMMessageContent() {

    override val type = Type.Text

    /**
     * 明文
     */
    var plainText = ""

    companion object {

        fun create(targetId: String): VMMessageContentText {
            val content = VMMessageContentText()
            content.targetId = targetId
            return content
        }

        fun find(realm: Realm, targetId: String): VMMessageContentText? {
            val query = realm.where(ENTextContent::class.java)
            query.equalTo("targetId", targetId)
            query.findFirst()?.let {
                val text = VMMessageContentText()
                text.targetId = it.targetId
                text.plainText = it.plainText
                return text
            }
            return null
        }
    }

    fun saveOrUpdate(realm: Realm) {
        val query = realm.where(ENTextContent::class.java)
        query.equalTo("targetId", targetId)
        var en = query.findFirst()
        if (en == null) {
            en = ENTextContent()
            en.targetId = targetId
        }
        en.plainText = plainText
        realm.insertOrUpdate(en)
    }

}