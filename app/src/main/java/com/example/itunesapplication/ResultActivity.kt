package com.example.itunesapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.example.itunesapplication.DetailedAlbumActivity.Companion.bitmapOfCurrentAlbum
import com.example.itunesapplication.DetailedAlbumActivity.Companion.stringNameOfAlbum
import com.example.itunesapplication.HttpManager.Companion.connectionSuccess
import com.example.itunesapplication.HttpManager.Companion.stopPreloaderFlag
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tableOfAlbums: TableLayout
    private lateinit var albumConstraintLayout: ConstraintLayout

    private lateinit var filterTextField: TextView
    private lateinit var filterButton: Button

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    private var filterSet = HashSet<AlbumClass>() // отфильтрованный массив альбомов

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        tableOfAlbums = findViewById(R.id.tableOfAlbums)
        albumConstraintLayout = findViewById(R.id.albumConstraintLayout)
        filterTextField = findViewById(R.id.filterTextField)
        filterButton = findViewById(R.id.filterButton)
        filterButton.setOnClickListener(this)
        loadAlbums(RequestFactory.instance.setOfAlbums()) // загрузка массива альбомов через функцию loadAlbums.
    }

    private fun loadAlbums(setOfAlbums: HashSet<AlbumClass>){ // Загрузка альбомов. В качестве параметра выбран HashSet<AlbumClass> т.к. данная функция используется для загрузки и полного списка альбомов и отсортированного.
        val sortedSetOfAlbums = setOfAlbums.sortedBy { it.albumName } // одно из условий задания - отсортировать список альбомов по алфавиту
        for(i in sortedSetOfAlbums){ //создаем ряды в таблице для всех объектов, содержащихся в setOfAlbums
            val tableRow = TableRow(this) // создание нового ряда в таблице tableOfAlbums
            tableRow.isClickable = true // ряд становится кликабельным для последующего детального просмотра альбома
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)
            val imageOfAlbum = ImageView(this) // Создание ImageView для помещения в него обложки альбома
            val lp1 = TableRow.LayoutParams(300, 300)
            lp1.rightMargin = 20
            imageOfAlbum.layoutParams = lp1
            imageOfAlbum.setPadding(5,10,5,10)
            val width = i.albumImage.width //получение ширины изображения альбома
            val height = i.albumImage.height //получение высоты изображения альбома
            val scaleWidth = 300F / width //инициализация размерного коэффициента для ширины
            val scaleHeight = 300F / height //инициализация размерного коэффициента для высоты

            val matrix = Matrix()

            matrix.postScale(scaleWidth, scaleHeight) // создание матрицы выбранного размера для построения по ней обложки альбома с новыми размерами

            imageOfAlbum.setImageBitmap(Bitmap.createBitmap(i.albumImage, 0, 0, width, height, matrix, false)) // помещение измененного изображения обложки альбома в ImageView
            tableRow.addView(imageOfAlbum) // добавление обложки в ряд таблицы
            val innerLinearLayout = LinearLayout(this) // создание LinearLayout для удобства верстки элементов внутри tableRow
            val innerLinearLayoutLP = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            innerLinearLayoutLP.gravity = Gravity.CENTER_VERTICAL
            innerLinearLayout.layoutParams = innerLinearLayoutLP
            innerLinearLayout.orientation = LinearLayout.VERTICAL
            val textOfAlbum = TextView(this) // создание TextView для отображения названия альбома
            textOfAlbum.text = "Название альбома: " + i.albumName
            textOfAlbum.setTextColor(Color.BLACK)
            albumConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener { // получение ширины родительского layout через viewTreeObserver т.к. функция вызывается из метода onCreate и ширина родительского слоя на момент испонения метода onCreate неизвестна. viewTreeObserver обращается к дереву элементов и таким образом можно достать ширину слоя
                val textOfAlbumLP = TableRow.LayoutParams(innerLinearLayout.width, TableRow.LayoutParams.WRAP_CONTENT) // присваиваем TextView ширину родительского слоя
                textOfAlbum.layoutParams = textOfAlbumLP
            }
            textOfAlbum.textSize = 14F

            val dateOfAlbum = TextView(this) // создание TextView для отображения даты выхода альбома
            dateOfAlbum.text = "Дата выхода: " + i.releaseDate
            dateOfAlbum.setTextColor(Color.BLACK)
            val dateOfAlbumLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            dateOfAlbum.layoutParams = dateOfAlbumLP
            dateOfAlbum.textSize = 14F

            innerLinearLayout.addView(textOfAlbum) // добавляем TextView c названием альбома во внутренний layout
            innerLinearLayout.addView(dateOfAlbum) // добавляем TextView c датой выхода альбома во внутренний layout
            tableRow.addView(innerLinearLayout) // добавляем внутренний layout в ряд

            tableRow.setOnClickListener{// добавляем слушателя событий клика на ряд
                PreloaderClass.instance.startPreloader(this, albumConstraintLayout) // начало работы прелоадера
                bitmapOfCurrentAlbum = i.albumImage // сохранение обложки альбома в companion object DetailedAlbumActivity для последующего его отображения в DetailedAlbumActivity
                stringNameOfAlbum = i.albumName // сохранение названия альбома в companion object DetailedAlbumActivity для последующего его отображения в DetailedAlbumActivity
                val chosenAlbumId = i.id // присвоение переменной chosenAlbumId id выбранного альбома для последующей передачи в запрос
                HttpManager(this).makeDetailedAlbumRequest("id=$chosenAlbumId&entity=song") //передача строки в функцию makeDetailedAlbumRequest пользовательского класса HttpManager для формирования запроса с фильтром song
                mHandler = Handler() // создаем слушатель события
                mRunnable = Runnable { // создаем отдельный поток для прослушивания события (содержит описание работы)
                    mHandler.postDelayed(mRunnable,100) // каждые 100мс проверяем изменилось ли значение какого-то флага.
                    if (stopPreloaderFlag) {// изменение этого флага ведет к остановке работы прелоадера и разблокирования ранее заблокированных элементов. Этот флаг изменяется в результате получения ЛЮБОГО ответа в ходе работы запроса
                        PreloaderClass.instance.stopPreloader(albumConstraintLayout) //остановка прелоадера
                        stopPreloaderFlag = false // возврат значения в изначальное состояние для возможности последующей работы по предложенной схеме
                        mHandler.removeCallbacks(mRunnable) // остановка слушателя (он больше не нужен)
                    }
                    if (connectionSuccess) { // данный флаг меняется только в случае успешного завершения запроса и заполнения setOfAlbums из файла RequestFactory
                        connectionSuccess = false// возврат значения в изначальное состояние для возможности последующей работы по предложенной схеме
                        mHandler.removeCallbacks(mRunnable)// остановка слушателя (он больше не нужен)
                        startActivity(Intent(this, DetailedAlbumActivity::class.java))// переход на активити, содержащий подробную информацию об альбоме со списком песен
                    }
                }
                mHandler.post(mRunnable) // запуск работы слушателя
            }
            tableOfAlbums.addView(tableRow) // добавляем ряд в таблицу tableOfAlbums
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.filterButton -> { // если клик был совершен по элементу с id filterButton (кнопка фильтра в верхней части активити. В качестве критериев фильтра выбраны названия альбома и год выхода альбома. Фильтр можно расширить до еще каких-либо критериев, которые соответственно, ранее нужно достать из json альбомов.
                filterSet.clear() // очищается массив содержащий отфильтрованные элементы общего списка альбомов
                tableOfAlbums.removeAllViewsInLayout() // очищается таблица содержащая альбомы
                val compareString = filterTextField.text.toString() // считывается строка из поля фильтра
                if(compareString == ""){ // если строка пустая и была нажата кнопка, то будет выведен полный список альбомов
                    loadAlbums(RequestFactory.instance.setOfAlbums())
                    return
                }
                for(i in RequestFactory.instance.setOfAlbums()){ // в цикле производим поиск элементов соответствующий критерию из поля фильтра
                    val lowerString1 = i.albumName.toLowerCase(Locale.ROOT) // эта и строка ниже созданы для исключения ошибок фильтра, связанных с вводом большими или маленькими буквами
                    val lowerString2 = compareString.toLowerCase(Locale.ROOT)
                    if(lowerString1 == lowerString2){ // если название альбома совпало с названием из поля фильтра, то такой объект добавляется в массив filterSet
                        filterSet.add(i)
                    }
                    val yearOfAlbum = convertToYear(i.releaseDate) // данная функция введена для конвертирования даты выхода альбома в год выхода альбома, т.к., по моему мнению, людей мало интересует точная дата выхода, чаще всего год
                    if(yearOfAlbum == compareString){ //сравнивается введенная строка в поле фильтра с годом выпуска альбома. Если таковой найден, то он добавляется в массив filterSet
                        filterSet.add(i)
                    }
                }
                loadAlbums(filterSet) // передача filterSet в loadAlbums для формирования и отображения таблицы с отфильтрованным списком альбомов.
            }
        }
    }

    private fun convertToYear(initialDate: String): String{
        val df = SimpleDateFormat("dd.MM.yyyy") // исходный шаблон даты
        val date = df.parse(initialDate)
        df.applyPattern("yyyy") // конечный шаблон даты
        return df.format(date)
    }
}
