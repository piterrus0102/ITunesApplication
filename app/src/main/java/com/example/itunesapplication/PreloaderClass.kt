package com.example.itunesapplication

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.windowManager
import org.jetbrains.anko.wrapContent

class PreloaderClass() {

    lateinit var blackScreen: View
    lateinit var preloader: ProgressBar

    companion object {
        val instance = PreloaderClass() // создание Синглтона для использования по всему проекту (нужен в MainActivity и ResultActivity для того, чтобы показывать пользователю, что идет процесс обработки запроса
    }

    fun startPreloader(context: Context, constraintLayout: ConstraintLayout){
        blackScreen = View(context) // программно создается темный фон, накладываемый поверх всех элементов активити
        val blackScreenLayoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        blackScreen.setLayoutParams(blackScreenLayoutParams)
        blackScreen.backgroundColor = Color.BLACK
        blackScreen.alpha = .75F // для того, чтобы за этим view было видно, что пользователь находится на том же экране
        constraintLayout.addView(blackScreen) // добавление к вызывающему layout созданного темного экрана
        preloader = ProgressBar(context) // программно создается progressBar
        val imageViewLayoutParams = ViewGroup.LayoutParams(wrapContent, wrapContent)
        preloader.setLayoutParams(imageViewLayoutParams)
        val displayMetrics = context.windowManager.defaultDisplay // запрос размеров экрана для оцентровки ProgressBar по осям
        val size = Point()
        displayMetrics?.getSize(size)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // для версии Android 5.0 и больше Drawable для ProgressBar вызывается иначе, чем ранее
            preloader.progressDrawable = context.resources.getDrawable(R.drawable.preloader, context.theme)
        }
        else{ // для версии Android ниже 5.0
            preloader.progressDrawable = context.resources.getDrawable(R.drawable.preloader)
        }
        preloader.x = (size.x / 2.5).toFloat() // центрование по оси х
        preloader.y = (size.y / 2.5).toFloat() //центрование по оси у
        constraintLayout.addView(preloader) // добавление progressBar к вызывающему layout
    }

    fun stopPreloader(constraintLayout: ConstraintLayout){ // в данную функцию context не передается за его ненадобностью в данной функции
        constraintLayout.removeView(preloader) // удаление с вызывающего layout progressBar'a
        constraintLayout.removeView(blackScreen) // удаление темного экрана с вызывающего layout
    }
}