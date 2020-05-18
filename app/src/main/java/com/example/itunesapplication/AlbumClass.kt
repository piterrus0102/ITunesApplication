package com.example.itunesapplication

import android.graphics.Bitmap

class AlbumClass(id: Int,
                 albumImage: Bitmap,
                 albumName: String,
                 trackCount: Int,
                 country: String,
                 releaseDate:String) {

    var id: Int // id альбома
    var albumImage: Bitmap // обложка альбома
    var albumName: String // название альбома
    var trackCount: Int // количество треков в альбоме
    var country: String // страна
    var releaseDate: String // дата выхода альбома

    init {
        this.id = id
        this.albumImage = albumImage
        this.albumName = albumName
        this.trackCount = trackCount
        this.country = country
        this.releaseDate = releaseDate
    }
}