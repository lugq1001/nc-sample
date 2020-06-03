package com.nextcont.mobilization.service

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.nextcont.mobilization.R
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*


object ImageProvider {

    private const val IMAGE_PROXY_PREFIX = "https://upload.nextcont.com/v1/proxy?url="

    private val avatarOptions = RequestOptions()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.HIGH)

    private val defaultOptions = RequestOptions()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.NORMAL)

    private val driveOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.NORMAL)

    private val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder(250).setCrossFadeEnabled(true).build()
    private val transition = DrawableTransitionOptions.with(drawableCrossFadeFactory)


    fun loadAvatar(context: Context, url: String, into: ImageView, completion: ((success: Boolean) -> Unit)? = null) {
        loadAvatar(context, url, into, R.mipmap.nc_image_avatar, completion)
    }

    fun loadAvatar(context: Context, url: String, into: ImageView, @DrawableRes placeHolder: Int, completion: ((success: Boolean) -> Unit)? = null) {
        loadAvatar(context, url, into, placeHolder, null, completion)
    }

    fun loadAvatar(context: Context, url: String, into: ImageView, @DrawableRes placeHolder: Int, @DimenRes roundedCorners: Int?, completion: ((success: Boolean) -> Unit)? = null) {
        val opt = roundedOption(context, avatarOptions.placeholder(placeHolder).error(placeHolder), roundedCorners)
        Glide.with(context).load(url).apply(opt).transition(transition).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(false)
                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(true)
                }
                return false
            }
        }).into(into)
    }

    fun loadAvatar(context: Context, into: ImageView, completion: ((success: Boolean) -> Unit)? = null) {
        loadAvatar(context, R.mipmap.nc_image_avatar, into, null, completion)
    }

    fun loadAvatar(context: Context, @DrawableRes avatarRes: Int, into: ImageView, @DimenRes roundedCorners: Int?, completion: ((success: Boolean) -> Unit)? = null) {
        val opt = roundedOption(context, avatarOptions, roundedCorners)
        Glide.with(context).load(avatarRes).apply(opt).transition(transition).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(false)
                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(true)
                }
                return false
            }
        }).into(into)
    }

    fun makeProxyUrl(url: String): String {
        if (!url.startsWith("https") || !url.startsWith("http")) {
            return "${IMAGE_PROXY_PREFIX}https:$url"
        }
        return "$IMAGE_PROXY_PREFIX$url"
    }

    private fun roundedOption(context: Context, opt: RequestOptions, @DimenRes roundedCorners: Int?): RequestOptions {
        return if (roundedCorners != null) {
            val radius = context.resources.getDimensionPixelSize(roundedCorners)
            opt.transforms(CenterCrop(), RoundedCorners(radius))
        } else {
            opt.circleCrop()
        }
    }

    fun loadImageWithResult(context: Context, url: String, into: ImageView, completion: ((success: Boolean) -> Unit)? = null) {
        Glide.with(context).load(url).apply(defaultOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(false)
                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (completion != null) {
                    completion(true)
                }
                return false
            }
        }).into(into)
    }


    fun compression(context: Context, image: String, tempFolder: String, completion: (path: String) -> Unit) {
        Luban.with(context)
                .load(image)
                .setFocusAlpha(true)
                .ignoreBy(100)
                .setTargetDir(tempFolder)
                .filter { path -> !(TextUtils.isEmpty(path) || path.toLowerCase(Locale.getDefault()).endsWith(".gif")) }
                .setCompressListener(object : OnCompressListener {

                    override fun onSuccess(file: File?) {
                        if (file == null) {
                            completion(image)
                        } else {
                            completion(file.path)
                        }
                    }

                    override fun onStart() {

                    }

                    override fun onError(e: Throwable) {
                        completion(image)
                    }
                }).launch()
    }

}