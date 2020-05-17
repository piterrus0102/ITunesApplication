package com.example.itunesapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class DetailedAlbumActivity : AppCompatActivity() {

    companion object{
        var bitmapOfCurrentAlbum: Bitmap? = null
        var stringNameOfAlbum: String? = null
    }

    lateinit var imageOfCurrentAlbum: ImageView
    lateinit var nameOfCurrentAlbum: TextView
    lateinit var tableOfSongs: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_album)

        imageOfCurrentAlbum = findViewById(R.id.imageOfCurrentAlbum)
        nameOfCurrentAlbum = findViewById(R.id.nameOfCurrentAlbum)
        tableOfSongs = findViewById(R.id.tableOfSongs)
        loadAlbum()
        loadSongs()
    }

    fun loadAlbum(){
        val width: Int = bitmapOfCurrentAlbum!!.getWidth()
        val height: Int = bitmapOfCurrentAlbum!!.getHeight()
        Log.v("width", width.toString())
        Log.v("height", height.toString())
        val scaleWidth = 600F
        val scaleHeight = 600F
        // CREATE A MATRIX FOR THE MANIPULATION
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createScaledBitmap(bitmapOfCurrentAlbum!!, 300,300,true)
        bitmapOfCurrentAlbum!!.recycle()

        imageOfCurrentAlbum.setImageBitmap(resizedBitmap)
        /*imageOfCurrentAlbum.scaleType = ImageView.ScaleType.FIT_XY
        imageOfCurrentAlbum.layoutParams = ConstraintLayout.LayoutParams(300,300)*/
        nameOfCurrentAlbum.text = stringNameOfAlbum
    }

    fun loadSongs(){
        val r = RequestFactory.instance.setOfSongs().sortedBy { it.trackNumber }
        for(i in r){
            val tableRow = TableRow(this)
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)
            val innerLinearLayout = LinearLayout(this)
            val innerLinearLayoutLP = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            innerLinearLayout.layoutParams = innerLinearLayoutLP
            innerLinearLayout.orientation = LinearLayout.HORIZONTAL
            val trackName = TextView(this)
            trackName.text = i.trackName
            trackName.setTextColor(Color.BLACK)
            val textOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            trackName.layoutParams = textOfAlbumLP
            trackName.textSize = 14F
            trackName.gravity = Gravity.CENTER_VERTICAL

            val trackTimeMillis = TextView(this)
            trackTimeMillis.text = i.trackTimeMillis.toString()
            trackTimeMillis.setTextColor(Color.BLACK)
            val dateOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            trackTimeMillis.layoutParams = dateOfAlbumLP
            trackTimeMillis.textSize = 14F
            trackTimeMillis.gravity = Gravity.CENTER_VERTICAL

            innerLinearLayout.addView(trackName)
            innerLinearLayout.addView(trackTimeMillis)
            tableRow.addView(innerLinearLayout)

            tableOfSongs.addView(tableRow)
        }
    }
}
