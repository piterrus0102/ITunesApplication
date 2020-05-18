package com.example.itunesapplication

import android.content.Context
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

class HttpManager(var context: Context) { // Контекст передается только для работы с Toast

    companion object{
        var stopPreloaderFlag = false // данная переменная служит флагом для вызова функции stopPreloader класса PreloaderClass
        var connectionSuccess = false // данная переменная служит чекпоинтом для успешного окончания загрузки данных по Response-коду 200
    }

    fun isNetworkAvailableAndConnected(): Boolean { //функция проверки наличия интернет-соединения на мобильном устройстве.
        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8") // выполнение указанной строковой команды в отдельном процессе Android
            val exitValue = ipProcess.waitFor() // ожидание завершения процесса и присвоения кода завершения переменной exitValue
            return (exitValue == 0) // 0 означает что операция успешно завершена, а значит будет возвращено true

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false // возвращает false в случае неудачного завершения выполнения команды в строке 35
    }

    fun makeAlbumRequest(requestString: String){
        RequestFactory.instance.setOfAlbums().clear()
        val url = URL("https://itunes.apple.com/search?term=$requestString&entity=album&limit=200") // делается запрос requestString - содержащий имя исполнителя с типом результата "album" и лимитом в 200 альбомов (200 выбрано т.к. ни у какого исполнителя нет 200 альбомов записанных - недостижимый предел)

        val okHttpClient = OkHttpClient() // инициализация OkHttpClient.
        try {
            val request: Request = Request.Builder().url(url).build() // построение запроса

            okHttpClient.newCall(request).enqueue(object : Callback { // обработка колбэка
                override fun onFailure(call: Call?, e: IOException?) { // неудачная обработка, чаще всего связанная с отсутствием интернета
                    if(!isNetworkAvailableAndConnected()) { // если отсутствие интернет-соединения
                        doAsync {
                            uiThread {
                                stopPreloaderFlag = true // флаг остановки preloader
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    return // любая другая неудача, не связанная с интернет-соединением
                }

                override fun onResponse(call: Call?, response: Response?) {
                    if(response!!.code() != 200){ // обработка результата response-кода, если он отличается от 200. Методом проб и ошибок так и не добился никакого другого response-кода. Данное условие сделано "на всякий случай"
                        stopPreloaderFlag = true
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string() // запись в переменную тела ответа
                        if(JSONObject(json!!).getInt("resultCount") == 0){ // условие когда искомый исполнитель не найден
                            doAsync {
                                uiThread {
                                    stopPreloaderFlag = true
                                    Toast.makeText(context, "Исполнитель не найден", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        for(i in 0 until JSONObject(json).getInt("resultCount")){
                            GlobalScope.launch { //используется корутина для получения изображения по ссылке из поля "artworkUrl100" и последующей его загрузки
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
                                val releaseDate = convertDate(releaseDateFromJSON) // Конвертация даты к dd.MM.yyyy
                                val newAlbum = AlbumClass(albumId,bitmap,albumName,trackCount,country,releaseDate) // создается объект пользовательского типа AlbumClass
                                RequestFactory.instance.setOfAlbums().add(newAlbum) // созданный объект помещается в массив
                                if(i == JSONObject(json).getInt("resultCount")-1){ // проверка окончания загрузки контента и "зеленый свет" переходу к следующему активити путем установки флага connectionSuccess в значение true
                                    stopPreloaderFlag = true
                                    connectionSuccess = true
                                }
                            }
                        }
                    }
                }
            })
        }catch(e:IllegalArgumentException){  // ошибка которая может возникнуть в процессе выполнения корутины
            return
        }
    }

    fun makeDetailedAlbumRequest(requestString: String){
        RequestFactory.instance.setOfSongs().clear()
        val url = URL("https://itunes.apple.com/lookup?$requestString") // запрос на получение песен в конкретном альбоме. Передается id альбома с entity=song

        val okHttpClient = OkHttpClient()
        try {
            val request: Request = Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) { // проверка наличия интернет-соединения
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
                    if(response!!.code() != 200){
                        stopPreloaderFlag = true
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        for(i in 0 until JSONObject(json!!).getInt("resultCount")){
                            if(JSONObject(json).getJSONArray("results").getJSONObject(i).get("wrapperType") == "track"){ // данное условие нужно потому, что первым объектом массива results в возвращаемом json идет не track а collectionName в поле wrapperType. Соотвественно, необходимо его из цикла исключить
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
                                    val newSong = SongClass(trackName, trackTimeMillis, trackNumber) // создание объекта пользовательского типа SongClass
                                    RequestFactory.instance.setOfSongs().add(newSong)
                                    if(i == JSONObject(json).getInt("resultCount")-1){ // условие успешного окончания цикла
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
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // исходный шаблон входящей строки
        val date = df.parse(dateFromJSON)
        df.applyPattern("dd.MM.yyyy") // конечный шаблон входящей строки
        return df.format(date!!)
    }
}