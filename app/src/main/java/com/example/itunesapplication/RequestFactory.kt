package com.example.itunesapplication

import java.util.*

class RequestFactory {
    private val setOfAlbums: HashSet<AlbumClass> = HashSet<AlbumClass>() // массив для хранения всех альбомов исполнителя

    private val setOfSongs: HashSet<SongClass> = HashSet<SongClass>() // массив для хранения песен конкретного альбома

    companion object {
        val instance = RequestFactory() // создается Синглтон для использования по всему проекту массивов альбомов и песен
    }

    fun setOfAlbums(): HashSet<AlbumClass> { // реализация инкапсуляции
        return setOfAlbums
    }

    fun setOfSongs(): HashSet<SongClass> { // реализация инкапсуляции
        return setOfSongs
    }
}