package com.nextcont.mobilization.ui.chat

import com.nextcont.mobilization.model.chat.VMMessage
import com.nextcont.mobilization.store.Store
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.ref.WeakReference

internal class PreviewViewModel {

    private var activity: WeakReference<PreviewActivity>? = null
    var message: VMMessage? = null

    private val disposableBag = CompositeDisposable()

    fun destroy() {
        disposableBag.clear()
    }


    fun downloadImage(url: String): Single<String> {
        return Single.create { emitter ->
            // TODO 下载
//            val downloadPath = "${PathProvider.downloadFolder(PathProvider.FileType.Image)}${File.separator}${url.md5}"
//            if (FileUtils.isFileExists(downloadPath)) {
//                emitter.onSuccess(downloadPath)
//                return@create
//            }
//            val tempPath = "${PathProvider.tempFolder(PathProvider.FileType.Image)}${File.separator}${UUID.randomUUID()}"
//            disposableBag.add(NCApi.render.download(url, tempPath).subscribe({
//                //activity?.get()?.showDownloadProgress(it.percent)
//            }, {
//                emitter.onError(it)
//            }, {
//                Logger.i("下载完成")
//                FileUtils.move(tempPath, downloadPath)
//                emitter.onSuccess(downloadPath)
//            }))
        }

    }

    fun initVideoFile(message: VMMessage) {
        // TODO 视频处理
//        val video = message.content as VMMessageContentVideo
//        if (video.localPath.isNotEmpty() && FileUtils.isFileExists(video.localPath)) {
//            activity?.get()?.playVideo(video.localPath)
//            return
//        }
//        activity?.get()?.startLoading()
//        disposableBag.add(requestDownloadUrl(message).flatMap { obj ->
//            downloadVideo(obj.data.downloadUrl)
//        }.subscribe({ path ->
//            var rm: Realm? = null
//            try {
//                rm = Store.openRealm
//                rm.executeTransaction { realm ->
//                    video.localPath = path
//                    message.content = video
//                    message.saveOrUpdate(realm)
//                    activity?.get()?.playVideo(path)
//                }
//            } catch (e: Exception) {
//
//            } finally {
//                Store.closeRealm(rm)
//            }
//
//        }, {
//            activity?.get()?.showVideoError(it.localizedMessage)
//        }))
    }

//    private fun requestDownloadUrl(message: VMMessage): Single<CdnFileObj> {
//        val video = message.content as VMMessageContentVideo
//        return NCApi.render.chat.getCDNFile(video.downloadUrl)
//    }

//    private fun downloadVideo(downloadUrl: String): Single<String> {
//        val tempPath = "${PathProvider.tempFolder(PathProvider.FileType.Video)}${File.separator}${UUID.randomUUID()}.mp4"
//        val downloadPath = "${PathProvider.downloadFolder(PathProvider.FileType.Video)}${File.separator}${UUID.randomUUID()}.mp4"
//        return Single.create { emitter ->
//            disposableBag.add(NCApi.render.download(downloadUrl, tempPath).subscribe({
//                activity?.get()?.showDownloadProgress(it.percent)
//            }, {
//                emitter.onError(it)
//            }, {
//                Logger.i("下载完成")
//                FileUtils.move(tempPath, downloadPath)
//                emitter.onSuccess(downloadPath)
//            }))
//        }
//    }

}