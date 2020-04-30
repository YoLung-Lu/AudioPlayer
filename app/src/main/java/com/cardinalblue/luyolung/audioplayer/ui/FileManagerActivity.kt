package com.cardinalblue.luyolung.audioplayer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import android.widget.Toast
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cardinalblue.luyolung.audioplayer.R
import com.cardinalblue.luyolung.audioplayer.db.AudioRepository
import com.cardinalblue.luyolung.audioplayer.util.getNavigationBarHeight
import com.cardinalblue.luyolung.audioplayer.util.getScreenHeight
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.functions.Predicate
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_audio_player.*
import java.util.concurrent.TimeUnit

class FileManagerActivity : AppCompatActivity() {

    private val disposableBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)


        observeUserAction()

    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.clear()
    }

    private fun observeUserAction() {
    }
}