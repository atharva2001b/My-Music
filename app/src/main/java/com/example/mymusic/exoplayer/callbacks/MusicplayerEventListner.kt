package com.example.mymusic.exoplayer.callbacks

import android.widget.Toast
import com.example.mymusic.exoplayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicplayerEventListner(
    private val musicService: MusicService
): Player.Listener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Unknown error occured!!", Toast.LENGTH_LONG).show()
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)

        if(state == Player.STATE_READY && !musicService.exoplayer.playWhenReady){
            musicService.stopForeground(false)
            TODO("check if second condition of 'if' matters")
        }

    }
}