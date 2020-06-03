package com.nextcont.mobilization.service

import android.os.Environment
import com.blankj.utilcode.util.FileUtils
import timber.log.Timber
import java.io.File

object PathProvider {

    private val rootPath: String
        get() {
            return make(path())
        }

    enum class FileType {
        Voice,
        Image,
        Video;

        val folder: String
            get() {
                return when (this) {
                    Voice -> "voice"
                    Image -> "image"
                    Video -> "video"
                }
            }
    }

    fun tempFolder(type: FileType): String {
        return make("$rootPath${File.separator}${type.folder}${File.separator}tmp")
    }

    fun localFolder(type: FileType): String {
        return make("$rootPath${File.separator}${type.folder}${File.separator}local")
    }

    fun downloadFolder(type: FileType): String {
        return make("$rootPath${File.separator}${type.folder}${File.separator}download")
    }

    private fun make(path: String): String {
        val folder = File(path)
        if (!folder.exists()) {
            val result = folder.mkdirs()
            if (result) {
                Timber.i("创建目录 $path")
            } else {
                Timber.e("创建目录失败 $path")
            }
        }
        return path
    }

    fun clearTemp() {
        FileUtils.deleteAllInDir(tempFolder(FileType.Voice))
        FileUtils.deleteAllInDir(tempFolder(FileType.Image))
        FileUtils.deleteAllInDir(tempFolder(FileType.Video))
    }

    private val appFolder: String
        get() {
            val path = "${Environment.getExternalStorageDirectory().path}${File.separator}Mob"
            val folder = File(path)
            if (!folder.exists() || !folder.isDirectory) {
                val result = folder.mkdirs()
                if (result) {
                    Timber.i("创建目录 $path")
                } else {
                    Timber.e("创建目录失败 $path")
                }
            }
            return path
        }

    /**
     * 文件目录
     */
    fun path(): String {
        val folderPath = "$appFolder${File.separator}mob"
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            val result = folder.mkdirs()
            if (result) {
                Timber.i("创建目录 $folderPath")
            } else {
                Timber.e("创建目录失败 $folderPath")
            }
        }
        return folderPath
    }
}