package com.example.itunesapplication

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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

        val width = bitmapOfCurrentAlbum!!.getWidth()
        val height = bitmapOfCurrentAlbum!!.getHeight()
        val scaleWidth = 300F / width
        val scaleHeight = 300F / height

        val matrix = Matrix()

        matrix.postScale(scaleWidth, scaleHeight)

        imageOfCurrentAlbum.setImageBitmap(Bitmap.createBitmap(bitmapOfCurrentAlbum!!, 0, 0, width, height, matrix, false))
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
            val textOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
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
