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
        val instance = PreloaderClass()
    }

    fun startPreloader(context: Context, constraintLayout: ConstraintLayout){
        blackScreen = View(context)
        val blackScreenLayoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        blackScreen.setLayoutParams(blackScreenLayoutParams)
        blackScreen.backgroundColor = Color.BLACK
        blackScreen.alpha = .75F
        constraintLayout?.addView(blackScreen)
        preloader = ProgressBar(context)
        val imageViewLayoutParams = ViewGroup.LayoutParams(wrapContent, wrapContent)
        preloader.setLayoutParams(imageViewLayoutParams)
        val displayMetrics = context?.windowManager?.defaultDisplay
        val size = Point()
        displayMetrics?.getSize(size)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            preloader.progressDrawable = context?.resources?.getDrawable(R.drawable.preloader, context?.theme)
        }
        else{
            preloader.progressDrawable = context?.resources?.getDrawable(R.drawable.preloader)
        }
        preloader.x = (size.x / 2.5).toFloat()
        preloader.y = (size.y / 2.5).toFloat()
        constraintLayout?.addView(preloader)
    }

    fun stopPreloader(context: Context, constraintLayout: ConstraintLayout){
        constraintLayout.removeView(preloader)
        constraintLayout.removeView(blackScreen)
    }
}