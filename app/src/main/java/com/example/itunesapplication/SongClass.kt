package com.example.itunesapplication

class SongClass(trackName: String, trackTimeMillis: Int, trackNumber: Int) {

    var trackName: String // название трека
    var trackTimeMillis: Int // длительность трек
    var trackNumber: Int // номер трека в альбоме для формирования правильной последовательности песен в альбоме

    init { // инициализация
        this.trackName = trackName
        this.trackTimeMillis = trackTimeMillis
        this.trackNumber = trackNumber
    }
}