package com.cardinalblue.luyolung.audioplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cardinalblue.luyolung.audioplayer.ui.AudioPlayerActivity
import com.cardinalblue.luyolung.audioplayer.ui.FileManagerActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val disposableBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpUserAction()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableBag.clear()
    }

    private fun setUpUserAction() {
        RxView.clicks(audioPlayer)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent(this, AudioPlayerActivity::class.java)
                this.startActivity(intent)
            }.addTo(disposableBag)

        RxView.clicks(fileManager)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent(this, FileManagerActivity::class.java)
                this.startActivity(intent)
            }.addTo(disposableBag)
    }
}
