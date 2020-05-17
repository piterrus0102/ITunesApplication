package com.example.itunesapplication

import java.util.*

class RequestFactory {
    private val setOfAlbums: HashSet<AlbumClass> = HashSet<AlbumClass>()

    private val setOfSongs: HashSet<SongClass> = HashSet<SongClass>()

    companion object {
        val instance = RequestFactory()
    }

    fun setOfAlbums(): HashSet<AlbumClass> {
        return setOfAlbums
    }

    fun setOfSongs(): HashSet<SongClass> {
        return setOfSongs
    }
}