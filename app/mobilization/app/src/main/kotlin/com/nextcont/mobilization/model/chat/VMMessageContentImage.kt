package com.nextcont.mobilization.model.chat

import com.nextcont.mobilization.store.entity.ENImageContent
import io.realm.Realm
import java.io.File

class VMMessageContentImage : VMMessageContent() {

    override val type = Type.Image

    /**
     * 下载地址
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
     * 本地图片地址
     */
    var localPath = ""

    /**
     * 预览图地址、用于聊天界面，图片地址根据优先级显示
     */
    val getThumbnailUrl: String
        get() {
            if (localPath.isNotEmpty()) {
                return localPath
            }
            if (thumbnailUrl.isNotEmpty()) {
                return thumbnailUrl
            }
            if (imageUrl.isNotEmpty()) {
                return imageUrl
            }
            return remoteUrl
        }

    /**
     * 下载地址
     */
    val getDownloadUrl: String
        get() {
            if (imageUrl.isNotEmpty()) {
                return imageUrl
            }
            if (thumbnailUrl.isNotEmpty()) {
                return thumbnailUrl
            }
            return remoteUrl
        }

    companion object {

        fun getExtension(pathOrUrl: String): String {
            return if (pathOrUrl.endsWith("gif", true)) "gif" else "jpg"
        }

        fun create(targetId: String): VMMessageContentImage {
            val content = VMMessageContentImage()
            content.targetId = targetId
            return content
        }

        fun find(realm: Realm, targetId: String): VMMessageContentImage? {
            val query = realm.where(ENImageContent::class.java)
            query.equalTo("targetId", targetId)
            query.findFirst()?.let { en ->
                val image = VMMessageContentImage()
                image.targetId = en.targetId
                image.imageUrl = en.imageUrl
                image.thumbnailUrl = en.thumbnailUrl
                image.remoteUrl = en.remoteUrl
                image.localPath = ""
                val localPath = en.localPath
                // 检验localPath是否有效
                if (localPath.isNotEmpty()) {
                    if (!File(localPath).exists()) {
                        en.localPath = ""
                        realm.insertOrUpdate(en)
                    } else {
                        image.localPath = localPath

                    }
                }
                return image
            }
            return null
        }
    }

    fun saveOrUpdate(realm: Realm) {
        val query = realm.where(ENImageContent::class.java)
        query.equalTo("targetId", targetId)
        var en = query.findFirst()
        if (en == null) {
            en = ENImageContent()
            en.targetId = targetId
        }
        if (!imageUrl.isEmpty()) {
            en.imageUrl = imageUrl
        }
        if (!thumbnailUrl.isEmpty()) {
            en.thumbnailUrl = thumbnailUrl
        }
        if (!remoteUrl.isEmpty()) {
            en.remoteUrl = remoteUrl
        }
        if (!localPath.isEmpty()) {
            en.localPath = localPath
        }
        realm.insertOrUpdate(en)
    }

}