package com.example.itunesapplication

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat

class HttpManager(context: Context) {

    companion object{
        var stopPreloaderFlag = false
        var connectionSuccess = false
    }

    var context: Context

    init {
        this.context = context
    }

    var finish: Boolean? = null


    fun isNetworkAvailableAndConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return (exitValue == 0)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun makeAlbumRequest(requestString: String){
        RequestFactory.instance.setOfAlbums().clear()
        val url = URL("https://itunes.apple.com/search?term=$requestString&entity=album&limit=100")

        val okHttpClient = OkHttpClient()
        try {
            val request: Request =
                Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    if(!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                stopPreloaderFlag = true
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.v("ResponseCode", response!!.code().toString())
                    if(response.code() != 200){
                        stopPreloaderFlag = true
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.w("JSON", json)
                        if(JSONObject(json).getInt("resultCount") == 0){
                            doAsync {
                                uiThread {
                                    stopPreloaderFlag = true
                                    Toast.makeText(context, "Исполнитель не найден", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        for(i in 0..JSONObject(json).getInt("resultCount")-1){
                            GlobalScope.launch {
                                val albumId = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("collectionId") as Int
                                val albumName = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("collectionName").toString()
                                val jpg = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("artworkUrl100").toString()
                                val URLjpg = URL(jpg)
                                val bitmap = BitmapFactory.decodeStream(URLjpg.openConnection().getInputStream())
                                val trackCount = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("trackCount") as Int
                                val country = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("country").toString()
                                val releaseDateFromJSON = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(i)
                                    .get("releaseDate").toString()
                                val releaseDate = convertDate(releaseDateFromJSON)
                                val newArtist = AlbumClass(albumId,bitmap,albumName,trackCount,country,releaseDate)
                                RequestFactory.instance.setOfAlbums().add(newArtist)
                                Log.v("SizeArray", RequestFactory.instance.setOfAlbums().size.toString())
                                if(RequestFactory.instance.setOfAlbums().size == JSONObject(json).getInt("resultCount")){
                                    stopPreloaderFlag = true
                                    connectionSuccess = true
                                }
                            }
                        }
                    }
                }
            })
        }catch(e:IllegalArgumentException){
            return
        }
    }

    fun makeDetailedAlbumRequest(requestString: String){
        RequestFactory.instance.setOfSongs().clear()
        val url = URL("https://itunes.apple.com/lookup?$requestString")

        val okHttpClient = OkHttpClient()
        try {
            val request: Request =
                Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    if(!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                stopPreloaderFlag = true
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.v("ResponseCode", response!!.code().toString())
                    if(response.code() != 200){
                        stopPreloaderFlag = true
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.w("JSON", json)
                        for(i in 0..JSONObject(json).getInt("resultCount")-1){
                            if(JSONObject(json).getJSONArray("results").getJSONObject(i).get("wrapperType") == "track"){
                                GlobalScope.launch {
                                    val trackName = JSONObject(json)
                                        .getJSONArray("results")
                                        .getJSONObject(i)
                                        .get("trackName").toString()
                                    Log.v("AlbumName", trackName)
                                    val trackTimeMillis = JSONObject(json)
                                        .getJSONArray("results")
                                        .getJSONObject(i)
                                        .get("trackTimeMillis") as Int
                                    val trackNumber = JSONObject(json)
                                        .getJSONArray("results")
                                        .getJSONObject(i)
                                        .get("trackNumber") as Int
                                    val newSong = SongClass(trackName, trackTimeMillis, trackNumber)
                                    RequestFactory.instance.setOfSongs().add(newSong)
                                    if(RequestFactory.instance.setOfSongs().size == JSONObject(json).getInt("resultCount")-1){
                                        stopPreloaderFlag = true
                                        connectionSuccess = true
                                    }
                                }
                            }
                        }

                    }
                }
            })
        }catch(e:IllegalArgumentException){
            return
        }
    }

    fun convertDate(dateFromJSON: String): String{
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = df.parse(dateFromJSON)
        df.applyPattern("dd.MM.yyyy")
        Log.v("date",  df.format(date))
        return df.format(date)
    }
}