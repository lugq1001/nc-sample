package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.MobApp
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.User
import com.nextcont.mobilization.store.entity.ENContact
import io.realm.Realm
import java.util.*

/**
 * 联系人
 */
class VMContact private constructor() {

    var localId: String = UUID.randomUUID().toString()

    var sid: String = ""

    var nickname: String = ""

    val displayName: String
        get() {
            return if (nickname.trim().isEmpty()) MobApp.Shared.getString(R.string.contact_name_unknown) else nickname
        }

    override fun toString(): String {
        return "$sid $nickname"
    }

    companion object {

        /**
         * 查找联系人
         */
        fun findBySid(realm: Realm, sid: String): VMContact? {
            val query = realm.where(ENContact::class.java)
            query.equalTo("sid", sid)
            query.findFirst()?.let { en ->
                return convert(en)
            }
            return null
        }

        fun create(user: User): VMContact {
            val contact = VMContact()
            contact.localId = UUID.randomUUID().toString()
            contact.sid = user.id
            contact.nickname = user.fullName
            return contact
        }

        fun create(sid: String): VMContact? {
            if (sid.isEmpty()) {
                return null
            }
            val contact = VMContact()
            contact.localId = UUID.randomUUID().toString()
            contact.sid = sid
            return contact
        }

        fun saveOrUpdate(realm: Realm, contacts: List<VMContact>) {
            val ens = mutableListOf<ENContact>()
            contacts.forEach {
                val query = realm.where(ENContact::class.java)
                query.equalTo("sid", it.sid)
                var en = query.findFirst()
                if (en == null) {
                    en = ENContact()
                    en.id = it.localId
                    en.sid = it.sid
                }
                if (it.nickname.isNotEmpty()) {
                    en.nickname = it.nickname
                }
                ens.add(en)
            }
            realm.insertOrUpdate(ens)
        }

        private fun convert(en: ENContact): VMContact {
            val contact = VMContact()
            contact.localId = en.id
            contact.sid = en.sid
            contact.nickname = en.nickname
            return contact
        }
    }


    /**
     * 保存、更新消息至DB
     */
    fun saveOrUpdate(realm: Realm) {
        saveOrUpdate(realm, listOf(this))
    }

}