package com.example.itunesapplication

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class DetailedAlbumActivity : AppCompatActivity() {

    companion object{
        var bitmapOfCurrentAlbum: Bitmap? = null // переменная для сохранения изображения выбранного альбома. При переходе на данный активити всегда будет не null
        var stringNameOfAlbum: String? = null// переменная для сохранения названия выбранного альбома. При переходе на данный активити всегда будет не null
    }

    private lateinit var imageOfCurrentAlbum: ImageView
    private lateinit var nameOfCurrentAlbum: TextView
    private lateinit var detailedConstraintLayout: ConstraintLayout
    private lateinit var tableOfSongs: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_album)

        imageOfCurrentAlbum = findViewById(R.id.imageOfCurrentAlbum)
        nameOfCurrentAlbum = findViewById(R.id.nameOfCurrentAlbum)
        detailedConstraintLayout = findViewById(R.id.detailedConstraintLayout)
        tableOfSongs = findViewById(R.id.tableOfSongs)
        loadAlbum() // функция загрузки обложки и названия альбома в заголовок активити
        loadSongs()
    }

    private fun loadAlbum(){

        val width = bitmapOfCurrentAlbum!!.width //получение ширины изображения альбома
        val height = bitmapOfCurrentAlbum!!.height //получение высоты изображения альбома
        val scaleWidth = 300F / width //инициализация размерного коэффициента для ширины
        val scaleHeight = 300F / height //инициализация размерного коэффициента для высоты

        val matrix = Matrix()

        matrix.postScale(scaleWidth, scaleHeight) // создание матрицы выбранного размера для построения по ней обложки альбома с новыми размерами

        imageOfCurrentAlbum.setImageBitmap(Bitmap.createBitmap(bitmapOfCurrentAlbum!!, 0, 0, width, height, matrix, false))
        nameOfCurrentAlbum.text = stringNameOfAlbum
    }

    private fun loadSongs(){
        val sortedSetOfSongs = RequestFactory.instance.setOfSongs().sortedBy { it.trackNumber } // сортировка массива песен по номеру трека в альбоме перед тем, как строить таблицу со значениями
        for(i in sortedSetOfSongs){ //создаем ряды в таблице для всех объектов, содержащихся в setOfSongs
            val tableRow = TableRow(this) // создание нового ряда в таблице tableOfSongs
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)
            val innerConstraintLayout = ConstraintLayout(this) // создание ConstraintLayout для удобства верстки элементов внутри tableRow
            detailedConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener { // получение ширины родительского layout через viewTreeObserver т.к. функция вызывается из метода onCreate и ширина родительского слоя на момент испонения метода onCreate неизвестна. viewTreeObserver обращается к дереву элементов и таким образом можно достать ширину слоя
                val innerConstraintLayoutLP = TableRow.LayoutParams(detailedConstraintLayout.width, 50) // устанавливаем ширину слоя по ширине родительского и высоту в 50 единиц
                innerConstraintLayout.layoutParams = innerConstraintLayoutLP
            }
            val trackName = TextView(this) // программно создаем textView содержащий название трека
            trackName.text = i.trackNumber.toString() + ". " + i.trackName // из объекта достаем соответствующие поля и помещаем их в TextView
            trackName.setTextColor(Color.BLACK)
            val textOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            trackName.layoutParams = textOfAlbumLP
            trackName.textSize = 14F
            trackName.gravity = Gravity.CENTER_VERTICAL

            val trackTimeMillis = TextView(this) // создание TextView с длительностью трека
            val seconds = (i.trackTimeMillis / 1000) % 60 // т.к. в json в запросе песен их длительность приходит в миллисекундах, следующие две переменные созданы для получения длительности трека в секундах и минутах
            val minutes = ((i.trackTimeMillis / (1000*60)) % 60);
            var secondsToString = seconds.toString()
            if(seconds<10){
                secondsToString = "0$secondsToString" // если секунды трека меньше десяти то после парсера у секунд отсутствует разряд десятков. В данном случае будет дописываться "0".
            }
            trackTimeMillis.text = minutes.toString() + ":" + secondsToString
            trackTimeMillis.setTextColor(Color.BLACK)
            val trackTimeMillisLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            trackTimeMillis.layoutParams = trackTimeMillisLP
            detailedConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener { // для получения смещения TextView содержащий длительность трека с отступом в 100 единиц от правого края. Это обеспечивает сохранность позиции при повороте экрана и пересоздании активити
                val layerWidth = detailedConstraintLayout.width
                trackTimeMillis.x = layerWidth.toFloat() - 100
            }
            trackTimeMillis.textSize = 14F
            trackTimeMillis.gravity = Gravity.CENTER_VERTICAL

            innerConstraintLayout.addView(trackName) // добавляем во внутренний layout TextView с названием песни
            innerConstraintLayout.addView(trackTimeMillis)// добавляем во внутренний layout TextView с длительностью песни
            tableRow.addView(innerConstraintLayout)// добавляем в ряд внутренний layout

            tableOfSongs.addView(tableRow) // добавляем ряд в таблицу
        }
    }
}
