package com.example.itunesapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.*
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var firstText: TextView
    lateinit var albumImageView: ImageView
    lateinit var albumName: TextView
    lateinit var URLjpg : URL
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        albumImageView = findViewById(R.id.albumImageView)
        firstText = findViewById(R.id.textView3)
        albumName = findViewById(R.id.albumName)
        button.setOnClickListener {
            makeRequest()
        }
    }

    fun makeRequest(){
        val url = URL("https://itunes.apple.com/search?term=Jack+Johnson")

        val okHttpClient = OkHttpClient()
        try {
            val request: Request =
                    Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    return
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.v("CODEEEEE", response!!.code().toString())
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.w("JSON", json)
                        runOnUiThread {
                            albumName.text = JSONObject(json)
                                .getJSONArray("results")
                                .getJSONObject(0)
                                .get("collectionName").toString()
                            val jpg = JSONObject(json)
                                .getJSONArray("results")
                                .getJSONObject(0)
                                .get("artworkUrl100").toString()
                            URLjpg = URL(jpg)
                            GlobalScope.launch {
                                bitmap = BitmapFactory.decodeStream(URLjpg.openConnection().getInputStream())
                                runOnUiThread {
                                    albumImageView.setImageBitmap(bitmap)
                                }
                            }
                            firstText.text = "А я быстрее"
                        }
                    }
                }
            })
        }catch(e:IllegalArgumentException){
            return
        }
    }
}
