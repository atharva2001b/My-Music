package com.example.mymusic.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mymusic.R
import com.example.mymusic.others.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mymusic.others.Constants.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListner: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(context,NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID, DescribtionAdapter(mediaController))
            .setNotificationListener(notificationListner)
            .setSmallIconResourceId(R.drawable.ic_music)
            .build()
    }


    fun showNotificatoin(player: Player){
        notificationManager.setPlayer(player)
    }

    inner class DescribtionAdapter(
        private val mediacontroller: MediaControllerCompat
    ): PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediacontroller.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediacontroller.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return mediacontroller.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediacontroller.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })

            return null
        }


    }



}