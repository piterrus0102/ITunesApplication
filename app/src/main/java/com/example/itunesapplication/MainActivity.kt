package com.example.itunesapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.itunesapplication.HttpManager.Companion.connectionSuccess
import com.example.itunesapplication.HttpManager.Companion.stopPreloaderFlag
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
    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable
    lateinit var searchTextField: TextView
    lateinit var searchConstraintLayout: ConstraintLayout
    lateinit var blackScreen: View
    lateinit var preloader: ProgressBar
    var requestString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        searchTextField = findViewById(R.id.searchTextField)
        searchConstraintLayout = findViewById(R.id.searchConstraintLayout)
        button.setOnClickListener {
            PreloaderClass.instance.startPreloader(this, searchConstraintLayout)
            searchTextField.isEnabled = false
            button.isClickable = false
            createRequest()
            HttpManager(this@MainActivity).makeAlbumRequest(requestString!!)
            mHandler = Handler()
            mRunnable = Runnable {
                mHandler.postDelayed(mRunnable,100)
                if (stopPreloaderFlag == true) {
                    PreloaderClass.instance.stopPreloader(this, searchConstraintLayout)
                    searchTextField.isEnabled = true
                    button.isClickable = true
                    stopPreloaderFlag = false
                    mHandler.removeCallbacks(mRunnable)
                }
                if (connectionSuccess == true) {
                    connectionSuccess = false
                    searchTextField.isEnabled = true
                    button.isClickable = true
                    mHandler.removeCallbacks(mRunnable)
                    startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                }
            }
            mHandler.post(mRunnable)
        }
    }

    /*fun startPreloader(){
        blackScreen = View(this)
        val blackScreenLayoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        blackScreen.setLayoutParams(blackScreenLayoutParams)
        blackScreen.backgroundColor = Color.BLACK
        blackScreen.alpha = .75F
        searchConstraintLayout.addView(blackScreen)
        preloader = ProgressBar(this)
        val imageViewLayoutParams = ViewGroup.LayoutParams(wrapContent, wrapContent)
        preloader.setLayoutParams(imageViewLayoutParams)
        val displayMetrics = windowManager.defaultDisplay
        val size = Point()
        displayMetrics?.getSize(size)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            preloader.progressDrawable = resources.getDrawable(R.drawable.preloader, this.theme)
        }
        else{
            preloader.progressDrawable = resources.getDrawable(R.drawable.preloader)
        }
        preloader.x = (size.x / 2.5).toFloat()
        preloader.y = (size.y / 2.5).toFloat()
        searchConstraintLayout.addView(preloader)
    }

    fun stopPreloader(){
        searchConstraintLayout.removeView(preloader)
        searchConstraintLayout.removeView(blackScreen)
    }*/

    fun createRequest(){
        requestString = searchTextField.text.toString()
        requestString = requestString!!.replace(' ','+')
    }

    /*fun isNetworkAvailableAndConnected(): Boolean {
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

    fun makeRequest(){
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
                                stopPreloader()
                                Toast.makeText(this@MainActivity, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.v("ResponseCode", response!!.code().toString())
                    if(response.code() != 200){
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.w("JSON", json)
                        if(JSONObject(json).getInt("resultCount") == 0){
                            runOnUiThread{
                                stopPreloader()
                                Toast.makeText(this@MainActivity, "Исполнитель не найден", Toast.LENGTH_SHORT).show()
                            }
                        }
                        for(i in 0..JSONObject(json).getInt("resultCount")-1){
                            GlobalScope.launch {
                                val albumName = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(0)
                                    .get("collectionName").toString()
                                val jpg = JSONObject(json)
                                    .getJSONArray("results")
                                    .getJSONObject(0)
                                    .get("artworkUrl100").toString()
                                URLjpg = URL(jpg)
                                bitmap = BitmapFactory.decodeStream(URLjpg.openConnection().getInputStream())
                                val newArtist = AlbumClass(bitmap,albumName)
                                RequestFactory.instance.setOfAlbums().add(newArtist)
                                Log.v("SizeArray", RequestFactory.instance.setOfAlbums().size.toString())
                                if(RequestFactory.instance.setOfAlbums().size == JSONObject(json).getInt("resultCount")){
                                    runOnUiThread {
                                        stopPreloader()
                                    }
                                    startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                                }
                            }
                        }
                    }
                }
            })
        }catch(e:IllegalArgumentException){
            return
        }
    }*/
}
