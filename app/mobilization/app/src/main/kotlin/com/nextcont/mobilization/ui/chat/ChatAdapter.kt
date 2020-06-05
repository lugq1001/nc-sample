package com.nextcont.mobilization.ui.chat

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.chat.*
import com.nextcont.mobilization.service.ImageProvider
import com.nextcont.mobilization.util.DrawableUtils
import io.reactivex.rxjava3.subjects.PublishSubject

internal class ChatAdapter : BaseMultiItemQuickAdapter<VMMessage, ChatAdapter.ChatViewHolder>(mutableListOf()) {

    init {
        addItemType(0, R.layout.chat_item_chat_right)
        addItemType(1, R.layout.chat_item_chat_left)
    }

    val imageLoadedSubject = PublishSubject.create<Unit>()

    override fun convert(holder: ChatViewHolder, item: VMMessage) {
        val sid = VMContact.self.sid
        val type = item.itemType
        ImageProvider.loadAvatar(context, item.sender.avatar, holder.iAvatarImage, R.mipmap.nc_img_chat_avatar, R.dimen.chat_avatar_corner)

        // TODO 头像点击
        //holder.addOnClickListener(R.id.iAvatarWrapper)
        holder.iWarningImage?.setImageDrawable(getWarningIcon(context))
        // TODO 重发点击
        //holder.addOnClickListener(R.id.iWarningWrapper)

        holder.iContentTextWrapper.visibility = GONE
        holder.iContentImageWrapper.visibility = GONE
        holder.iContentVideoWrapper.visibility = GONE
        holder.iContentVoiceWrapper.visibility = GONE
        holder.iBurnTextWrapper.visibility = GONE
        holder.iLoadingProgress?.visibility = GONE

        val time = item.displayTime
        if (time == 0L) {
            holder.iTimeText.visibility = GONE
        } else {
            holder.iTimeText.visibility = VISIBLE
            holder.iTimeText.text = item.formatTime
        }

        if (item.burn) {
            holder.iBurnTextWrapper.visibility = VISIBLE
            if (item.read) {
                holder.iLockIcon.visibility = GONE
                holder.iFireIcon.visibility = GONE
                holder.iBurnText.text = "该消息已阅"
                holder.iBurnText.setTextColor(ContextCompat.getColor(context, R.color.danger))
            } else {
                holder.iLockIcon.visibility = VISIBLE
                holder.iFireIcon.visibility = VISIBLE
                holder.iBurnText.text = "私密消息 阅后即焚"
                holder.iBurnText.setTextColor(ContextCompat.getColor(context, R.color.textLight))
            }
        } else {
            holder.iBurnTextWrapper.visibility = GONE
            val content = item.content
            when (content.type) {
                VMMessageContent.Type.Text -> {
                    val textContent = item.content as VMMessageContentText
                    holder.iContentTextWrapper.visibility = VISIBLE
                    holder.iContextText.text = textContent.plainText
                }
                VMMessageContent.Type.Image -> {
                    val imageContent = item.content as VMMessageContentImage
                    holder.iContentImageWrapper.visibility = VISIBLE

                    holder.iContextImagePlaceholder.setImageDrawable(getImagePlaceholder(context))
                    holder.iContextImagePlaceholder.visibility = VISIBLE
                    ImageProvider.loadImageWithResult(context, imageContent.getThumbnailUrl, holder.iContextImage, completion = {
                        holder.iContextImagePlaceholder.visibility = GONE
                        imageLoadedSubject.onNext(Unit)
                    })

                }
                VMMessageContent.Type.Video -> {
                    holder.iContentVideoWrapper.visibility = VISIBLE
                    holder.iContextVideoIcon.setImageDrawable(getVideoPlaceholder(context))
                }
                VMMessageContent.Type.Voice -> {
                    val voiceContent = item.content as VMMessageContentVoice
                    holder.iContentVoiceWrapper.visibility = VISIBLE
                    holder.iVoiceLengthText.text = voiceContent.durationPlaceholder
                    holder.iDurationText.text = voiceContent.displayDuration
                    holder.iVoiceBadge?.visibility = if (voiceContent.played) GONE else VISIBLE
                    holder.iVoiceProgress?.visibility = if (voiceContent.loading) VISIBLE else GONE

                    when (holder.itemViewType) {
                        0 -> {
                            holder.iVolumeImage.setImageDrawable(getVolumeIconRight(context).last())
                        }
                        1 -> {
                            holder.iVolumeImage.setImageDrawable(getVolumeIconLeft(context).last())
                        }
                    }
                }
            }
        }



        when (item.sendStatus) {
            VMMessage.SendState.Success -> {
                holder.iWarningImage?.visibility = INVISIBLE
                holder.iLoadingProgress?.visibility = INVISIBLE
            }
            VMMessage.SendState.Failure -> {
                holder.iWarningImage?.visibility = VISIBLE
                holder.iLoadingProgress?.visibility = INVISIBLE
            }
            VMMessage.SendState.Processing -> {
                holder.iWarningImage?.visibility = INVISIBLE
                holder.iLoadingProgress?.visibility = VISIBLE
            }
        }


    }


    inner class ChatViewHolder(view: View) : BaseViewHolder(view) {
        val iTimeText: TextView = view.findViewById(R.id.iTimeText)
        val iAvatarWrapper: CardView = view.findViewById(R.id.iAvatarWrapper)
        val iAvatarImage: ImageView = view.findViewById(R.id.iAvatarImage)

        val iWarningImage: ImageView? = view.findViewById(R.id.iWarningImage)

        val iLoadingProgress: ProgressBar? = view.findViewById(R.id.iLoadingProgress)

        /*私密文本*/
        val iBurnTextWrapper: LinearLayout = view.findViewById(R.id.iBurnTextWrapper)
        val iBurnText: TextView = view.findViewById(R.id.iBurnText)
        val iLockIcon: ImageView = view.findViewById(R.id.iLockIcon)
        val iFireIcon: ImageView = view.findViewById(R.id.iFireIcon)

        /*文本*/
        val iContentTextWrapper: LinearLayout = view.findViewById(R.id.iContentTextWrapper)
        val iContextText: TextView = view.findViewById(R.id.iContextText)

        /*图片*/
        val iContentImageWrapper: RelativeLayout = view.findViewById(R.id.iContentImageWrapper)
        val iContextImagePlaceholder: ImageView = view.findViewById(R.id.iContextImagePlaceholder)
        val iContextImage: ImageView = view.findViewById(R.id.iContextImage)

        /*视频*/
        val iContentVideoWrapper: RelativeLayout = view.findViewById(R.id.iContentVideoWrapper)
        val iContextVideoIcon: ImageView = view.findViewById(R.id.iContextVideoIcon)

        /*语音*/
        val iContentVoiceWrapper: LinearLayout = view.findViewById(R.id.iContentVoiceWrapper)
        val iVolumeImage: ImageView = view.findViewById(R.id.iVolumeImage)
        val iVoiceLengthText: TextView = view.findViewById(R.id.iVoiceLengthText)
        val iVoiceWrapper: LinearLayout = view.findViewById(R.id.iVoiceWrapper)

        val iVoiceBadge: View? = view.findViewById(R.id.iVoiceBadge)
        val iDurationText: TextView = view.findViewById(R.id.iDurationText)
        val iVoiceProgress: ProgressBar? = view.findViewById(R.id.iVoiceProgress)
    }

    companion object {

        private var imagePlaceholder: Drawable? = null
        fun getImagePlaceholder(context: Context): Drawable {
            if (imagePlaceholder == null) {
                imagePlaceholder = DrawableUtils.tintDrawable(context, R.mipmap.nc_ic_chat_content_image, R.color.split)
            }
            return imagePlaceholder!!
        }

        private var videoPlaceholder: Drawable? = null
        fun getVideoPlaceholder(context: Context): Drawable {
            if (videoPlaceholder == null) {
                videoPlaceholder = DrawableUtils.tintDrawable(context, R.mipmap.nc_ic_chat_content_video, R.color.nc_common_icon)
            }
            return videoPlaceholder!!
        }

        private var warningIcon: Drawable? = null
        fun getWarningIcon(context: Context): Drawable {
            if (warningIcon == null) {
                warningIcon = DrawableUtils.tintDrawable(context, R.mipmap.nc_ic_chat_warning, R.color.danger)
            }
            return warningIcon!!
        }

        private var leftVolumeIcons: List<Drawable>? = null
        fun getVolumeIconLeft(context: Context): List<Drawable> {
            if (leftVolumeIcons.isNullOrEmpty()) {
                leftVolumeIcons = listOf(
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_l_1)!!,
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_l_2)!!,
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_l_3)!!
                )
            }
            return leftVolumeIcons!!
        }

        private var rightVolumeIcons: List<Drawable>? = null
        fun getVolumeIconRight(context: Context): List<Drawable> {
            if (rightVolumeIcons.isNullOrEmpty()) {
                rightVolumeIcons = listOf(
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_r_1)!!,
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_r_2)!!,
                        context.getDrawable(R.mipmap.nc_ic_chat_audio_r_3)!!
                )
            }
            return rightVolumeIcons!!
        }
    }
}