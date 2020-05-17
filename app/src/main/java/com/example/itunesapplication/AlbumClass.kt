package com.example.itunesapplication

import android.graphics.Bitmap

class AlbumClass(id: Int,
                 albumImage: Bitmap,
                 albumName: String,
                 trackCount: Int,
                 country: String,
                 releaseDate:String) {

    var id: Int
    var albumImage: Bitmap
    var albumName: String
    var trackCount: Int
    var country: String
    var releaseDate: String

    init {
        this.id = id
        this.albumImage = albumImage
        this.albumName = albumName
        this.trackCount = trackCount
        this.country = country
        this.releaseDate = releaseDate
    }
}