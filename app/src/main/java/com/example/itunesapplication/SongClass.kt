package com.example.itunesapplication

class SongClass(trackName: String, trackTimeMillis: Int, trackNumber: Int) {

    var trackName: String
    var trackTimeMillis: Int
    var  trackNumber: Int

    init {
        this.trackName = trackName
        this.trackTimeMillis = trackTimeMillis
        this.trackNumber = trackNumber
    }
}