package com.example.mymusic.data.remote

import com.example.mymusic.data.entities.Song
import com.example.mymusic.others.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val songCollection = firestoreInstance.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e: Exception){
            emptyList()
        }
    }
}