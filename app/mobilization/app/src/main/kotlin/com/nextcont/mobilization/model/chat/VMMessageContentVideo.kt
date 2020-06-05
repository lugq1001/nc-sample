package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.store.entity.ENVideoContent
import com.squareup.moshi.JsonClass
import io.realm.Realm

@JsonClass(generateAdapter = true)
class VMMessageContentVideo : VMMessageContent() {

    override val type = Type.Video

    /**
     * Google返回的下载地址，需要换取服务端cdn地址
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
     * 远程服务器地址
     */
    var remoteUrl = ""

    /**
     * 本地文件地址
     */
    var localPath = ""

    companion object {

        fun create(targetId: String): VMMessageContentVideo {
            val content = VMMessageContentVideo()
            content.targetId = targetId
            return content
        }

        fun find(realm: Realm, targetId: String): VMMessageContentVideo? {
            val query = realm.where(ENVideoContent::class.java)
            query.equalTo("targetId", targetId)
            query.findFirst()?.let {
                val video = VMMessageContentVideo()
                video.targetId = it.targetId
                video.downloadUrl = it.downloadUrl
                video.imageUrl = it.imageUrl
                video.thumbnailUrl = it.thumbnailUrl
                video.remoteUrl = it.remoteUrl
                video.localPath = it.localPath
                return video
            }
            return null
        }
    }

    fun saveOrUpdate(realm: Realm) {
        val query = realm.where(ENVideoContent::class.java)
        query.equalTo("targetId", targetId)
        var en = query.findFirst()
        if (en == null) {
            en = ENVideoContent()
            en.targetId = targetId
        }
        if (downloadUrl.isNotEmpty()) {
            en.downloadUrl = downloadUrl
        }
        if (imageUrl.isNotEmpty()) {
            en.imageUrl = imageUrl
        }
        if (thumbnailUrl.isNotEmpty()) {
            en.thumbnailUrl = thumbnailUrl
        }
        if (remoteUrl.isNotEmpty()) {
            en.remoteUrl = remoteUrl
        }
        if (localPath.isNotEmpty()) {
            en.localPath = localPath
        }
        realm.insertOrUpdate(en)
    }

}