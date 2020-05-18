package com.example.itunesapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.itunesapplication.HttpManager.Companion.connectionSuccess
import com.example.itunesapplication.HttpManager.Companion.stopPreloaderFlag

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var searchTextField: TextView
    private lateinit var searchConstraintLayout: ConstraintLayout
    private var requestString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchButton = findViewById(R.id.searchButton)
        searchTextField = findViewById(R.id.searchTextField)
        searchConstraintLayout = findViewById(R.id.searchConstraintLayout)
        searchButton.setOnClickListener {//обработчик нажатия на кнопку поиска
            PreloaderClass.instance.startPreloader(this, searchConstraintLayout) // запускается прелоадер, означающий начало работы над запросом
            searchTextField.isEnabled = false // отключение возможности редактирования поля поиска для исключения ввода
            searchButton.isClickable = false // отключение возможности нажатия на кнопку поиска во избежание неправильной работы с запросом и записью в массив альбомов
            requestString = searchTextField.text.toString() // считываем текст из поля поиска
            requestString = requestString!!.replace(' ','+') // заменяем пробелы на плюсы для передачи строки в формируемый get-запрос
            HttpManager(this@MainActivity).makeAlbumRequest(requestString!!) // вызываем конструктор HttpManager'a и вызываем функцию запроса альбомов исполнителя. Все HTTP-запросы вынесены в отдельный функциональный класс
            mHandler = Handler() // создаем слушатель события
            mRunnable = Runnable { // создаем отдельный поток для прослушивания события (содержит описание работы)
                mHandler.postDelayed(mRunnable,100) // каждые 100мс проверяем изменилось ли значение какого-то флага.
                if (stopPreloaderFlag) { // изменение этого флага ведет к остановке работы прелоадера и разблокирования ранее заблокированных элементов. Этот флаг изменяется в результате получения ЛЮБОГО ответа в ходе работы запроса
                    PreloaderClass.instance.stopPreloader(searchConstraintLayout) //остановка прелоадера
                    searchTextField.isEnabled = true
                    searchButton.isClickable = true
                    stopPreloaderFlag = false // возврат значения в изначальное состояние для возможности последующей работы по предложенной схеме
                    mHandler.removeCallbacks(mRunnable) // остановка слушателя (он больше не нужен)
                }
                if (connectionSuccess) { // данный флаг меняется только в случае успешного завершения запроса и заполнения setOfAlbums из файла RequestFactory
                    connectionSuccess = false // возврат значения в изначальное состояние для возможности последующей работы по предложенной схеме
                    searchTextField.isEnabled = true
                    searchButton.isClickable = true
                    mHandler.removeCallbacks(mRunnable)// остановка слушателя (он больше не нужен)
                    startActivity(Intent(this@MainActivity, ResultActivity::class.java)) // переход на активити, содержащий полученные альбомы (совершается только в случае успеха)
                }
            }
            mHandler.post(mRunnable) // запуск работы слушателя
        }
    }

}
