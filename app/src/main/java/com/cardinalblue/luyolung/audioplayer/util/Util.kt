package com.cardinalblue.luyolung.audioplayer.util

import android.graphics.Point
import android.view.WindowManager
import android.util.DisplayMetrics

fun getScreenHeight(w: WindowManager): Int {
    val size = Point()
    w.defaultDisplay.getSize(size)
    return size.y
}

fun getNavigationBarHeight(w: WindowManager): Int {
    val metrics = DisplayMetrics()
    w.defaultDisplay.getMetrics(metrics)
    val usableHeight = metrics.heightPixels
    w.defaultDisplay.getRealMetrics(metrics)
    val realHeight = metrics.heightPixels
    return if (realHeight > usableHeight)
        realHeight - usableHeight
    else
        0
}