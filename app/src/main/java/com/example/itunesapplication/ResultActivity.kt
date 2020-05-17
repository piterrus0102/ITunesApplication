package com.example.itunesapplication

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.itunesapplication.DetailedAlbumActivity.Companion.bitmapOfCurrentAlbum
import com.example.itunesapplication.DetailedAlbumActivity.Companion.stringNameOfAlbum

class ResultActivity : AppCompatActivity() {

    lateinit var tableOfAlbums: TableLayout
    lateinit var albumConstraintLayout: ConstraintLayout

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        tableOfAlbums = findViewById(R.id.tableOfAlbums)
        albumConstraintLayout = findViewById(R.id.albumConstraintLayout)
        loadAlbums()

    }

    fun loadAlbums(){
        val r = RequestFactory.instance.setOfAlbums().sortedBy { it.albumName }
        for(i in r){
            val tableRow = TableRow(this)
            tableRow.isClickable = true
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)
            val imageOfAlbum = ImageView(this)
            val lp1 = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
            lp1.rightMargin = 20
            imageOfAlbum.layoutParams = lp1
            imageOfAlbum.setPadding(5,10,5,10)
            imageOfAlbum.setImageBitmap(i.albumImage)
            tableRow.addView(imageOfAlbum)
            val innerLinearLayout = LinearLayout(this)
            val innerLinearLayoutLP = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            innerLinearLayout.layoutParams = innerLinearLayoutLP
            innerLinearLayout.orientation = LinearLayout.VERTICAL
            val textOfAlbum = TextView(this)
            textOfAlbum.text = "Название альбома: " + i.albumName
            textOfAlbum.setTextColor(Color.BLACK)
            val textOfAlbumLP = TableRow.LayoutParams(tableOfAlbums.width - 100, TableRow.LayoutParams.WRAP_CONTENT)
            textOfAlbum.layoutParams = textOfAlbumLP
            textOfAlbum.textSize = 14F
            textOfAlbum.gravity = Gravity.CENTER_VERTICAL

            val dateOfAlbum = TextView(this)
            dateOfAlbum.text = "Дата выхода: " + i.releaseDate
            dateOfAlbum.setTextColor(Color.BLACK)
            val dateOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            dateOfAlbum.layoutParams = dateOfAlbumLP
            dateOfAlbum.textSize = 14F
            dateOfAlbum.gravity = Gravity.CENTER_VERTICAL

            innerLinearLayout.addView(textOfAlbum)
            innerLinearLayout.addView(dateOfAlbum)
            tableRow.addView(innerLinearLayout)

            tableRow.setOnClickListener{
                PreloaderClass.instance.startPreloader(this, albumConstraintLayout)
                bitmapOfCurrentAlbum = i.albumImage
                stringNameOfAlbum = i.albumName
                val q = i.id
                HttpManager(this).makeDetailedAlbumRequest("id=$q&entity=song")
                mHandler = Handler()
                mRunnable = Runnable {
                    mHandler.postDelayed(mRunnable,100)
                    if (HttpManager.stopPreloaderFlag == true) {
                        PreloaderClass.instance.stopPreloader(this, albumConstraintLayout)
                        HttpManager.stopPreloaderFlag = false
                        mHandler.removeCallbacks(mRunnable)
                    }
                    if (HttpManager.connectionSuccess == true) {
                        HttpManager.connectionSuccess = false
                        mHandler.removeCallbacks(mRunnable)
                        startActivity(Intent(this, DetailedAlbumActivity::class.java))
                    }
                }
                mHandler.post(mRunnable)
            }
            tableOfAlbums.addView(tableRow)
        }
    }
}
