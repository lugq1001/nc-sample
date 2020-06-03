package com.nextcont.mobilization.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.lang.Exception


object DrawableUtils {

    /***
     * 生成icon
     */
    fun createDrawable(context: Context, @DrawableRes image: Int, @ColorRes color: Int, @DimenRes size: Int): Drawable {
        val iconSize = context.resources.getDimension(size).toInt()
        val drawable = resizeDrawable(context, image, iconSize, iconSize)
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        val colors = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        DrawableCompat.setTintList(wrappedDrawable, colors)
        return wrappedDrawable
    }

    fun createDrawable(context: Context, @DrawableRes image: Int, colorHex: String, @DimenRes size: Int): Drawable {
        val iconSize = context.resources.getDimension(size).toInt()
        val drawable = resizeDrawable(context, image, iconSize, iconSize)
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        try {
            val c = Color.parseColor(colorHex)
            val colors = ColorStateList.valueOf(c)
            DrawableCompat.setTintList(wrappedDrawable, colors)
        } catch (e: Exception){

        }
        return wrappedDrawable
    }

    fun tintDrawable(context: Context, @DrawableRes image: Int, @ColorRes color: Int): Drawable {
        val res = context.resources
        val drawable = res.getDrawable(image, null).mutate()
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        val colors = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        DrawableCompat.setTintList(wrappedDrawable, colors)
        return wrappedDrawable
    }

    fun tintDrawable(context: Context, @DrawableRes image: Int, colorHex: String): Drawable {
        val res = context.resources
        val drawable = res.getDrawable(image, null).mutate()
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        try {
            val c = Color.parseColor(colorHex)
            val colors = ColorStateList.valueOf(c)
            DrawableCompat.setTintList(wrappedDrawable, colors)
        } catch (e: Exception){

        }
        return wrappedDrawable
    }

    fun getDrawable(context: Context, drawableId: Int): Drawable {
        val res = context.resources
        return res.getDrawable(drawableId, null).mutate()
    }


    private fun resizeDrawable(context: Context, @DrawableRes image: Int, widthDp: Int, heightDp: Int): Drawable {
        val original = BitmapFactory.decodeResource(context.resources, image)
        //        int width = AppHelper.dip2px(c, widthDp);
        //        int height = AppHelper.dip2px(c, heightDp);
        val b = Bitmap.createScaledBitmap(original, widthDp, heightDp, false)
        return BitmapDrawable(context.resources, b)
    }

    fun resizeDrawable(context: Context, @DrawableRes image: Int, @DimenRes size: Int): Drawable {
        val iconSize = context.resources.getDimension(size).toInt()
        val original = BitmapFactory.decodeResource(context.resources, image)
        //        int width = AppHelper.dip2px(c, widthDp);
        //        int height = AppHelper.dip2px(c, heightDp);
        val b = Bitmap.createScaledBitmap(original, iconSize, iconSize, false)
        return BitmapDrawable(context.resources, b)
    }


    fun tintBitmap(context: Context, @DrawableRes image: Int, @ColorRes color: Int): Bitmap {
        val inBitmap = BitmapFactory.decodeResource(context.resources, image)
        val outBitmap = Bitmap.createBitmap(inBitmap.width, inBitmap.height, inBitmap.config)
        val canvas = Canvas(outBitmap)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(inBitmap, 0f, 0f, paint)
        return outBitmap

    }
}
