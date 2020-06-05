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

    var avatar: String = ""

    var role: String = ""

    val displayName: String
        get() {
            return if (nickname.trim().isEmpty()) MobApp.Shared.getString(R.string.contact_name_unknown) else nickname
        }

    override fun toString(): String {
        return "$sid $nickname"
    }

    companion object {

        val self: VMContact
        get() = testData(true)[0]

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

        fun testData(first: Boolean): MutableList<VMContact> {
            val list = listOf(
                listOf("徐思兵（ 我 ）", "男 四级战备", "http://img4.imgtn.bdimg.com/it/u=2321923534,225715896&fm=15&gp=0.jpg"),
                listOf("章雨兰", "女 三级战备", "http://img5.imgtn.bdimg.com/it/u=1598793832,660528799&fm=26&gp=0.jpg"),
                listOf("于翠杭", "女 四级战备", "http://img.mp.itc.cn/q_mini,c_zoom,w_640/upload/20170815/e3c33767d4ed4ca7886d5b32516bf94d_th.jpg"),
                listOf("刘雨婷", "女 二级战备", "http://img4.imgtn.bdimg.com/it/u=683716437,2612927275&fm=26&gp=0.jpg"),
                listOf("张晟睿", "男 四级战备", "http://img.jf258.com/i/2a817897252x380772316b27.jpg")

            )

            val list2 = listOf(
                listOf("冯旭尧", "男 一级战备", "http://img2.imgtn.bdimg.com/it/u=3111794050,3652343654&fm=26&gp=0.jpg"),
                listOf("郭嘉敏", "女 四级战备", "http://img5.imgtn.bdimg.com/it/u=2020504318,3921969137&fm=26&gp=0.jpg"),
                listOf("李文博", "男 二级战备", "http://img4.imgtn.bdimg.com/it/u=2863991827,4043414749&fm=15&gp=0.jpg"),
                listOf("施雅静", "女 三级战备", "http://img1.imgtn.bdimg.com/it/u=680617811,3983549844&fm=26&gp=0.jpg"),
                listOf("王俊楠", "男三级战备", "http://img1.imgtn.bdimg.com/it/u=1275170196,2111470645&fm=26&gp=0.jpg")
            )

            val contacts = mutableListOf<VMContact>()
            val arr = if (first) list else list2
            for (i in arr.indices) {
                val contact = VMContact()
                contact.localId = "${first}:$i"
                contact.sid = contact.localId
                contact.nickname = arr[i][0]
                contact.role = arr[i][1]
                contact.avatar = arr[i][2]
                contacts.add(contact)
            }

            return contacts
        }
    }


    /**
     * 保存、更新消息至DB
     */
    fun saveOrUpdate(realm: Realm) {
        saveOrUpdate(realm, listOf(this))
    }

}