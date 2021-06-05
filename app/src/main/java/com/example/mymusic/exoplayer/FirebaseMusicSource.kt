package com.example.mymusic.exoplayer

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.example.mymusic.R
import com.example.mymusic.data.remote.MusicDatabase
import com.example.mymusic.exoplayer.State.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaExtractor
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {

    private var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO){
        state = STATE_INITILIAZING
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.map { song ->
        MediaMetadataCompat.Builder()
            .putString(METADATA_KEY_ARTIST, song.album)
            .putString(METADATA_KEY_MEDIA_ID, song.song_id)
            .putString(METADATA_KEY_MEDIA_URI, song.url)
            .putString(METADATA_KEY_TITLE, song.title)
            .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.album)
            .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.album)
            .build()
        }
        state = STATE_INITILIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song->
            val mediaitem: MediaItem = MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaitem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setMediaId(song.description.mediaId)
            .setSubtitle(song.description.subtitle)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }

    private val onReadyListeners = mutableListOf<(Boolean)-> Unit>()

    private var state: State = STATE_CREATED
    set(value) {
        if(value == STATE_INITILIZED || value == STATE_ERROR){
            synchronized(onReadyListeners){
                field = value
                onReadyListeners.forEach { listeners->
                    listeners(state == STATE_INITILIZED)
                }
            }
        }else{
            field = value
        }
    }

    fun whenReady(action: (Boolean) -> Unit): Boolean{
        if(state == STATE_CREATED || state == STATE_INITILIAZING){
            onReadyListeners += action
            return false
        }else{
            action(state == STATE_INITILIZED)
            return true
        }
    }

}

enum class State {
    STATE_CREATED,
    STATE_INITILIAZING,
    STATE_INITILIZED,
    STATE_ERROR
}