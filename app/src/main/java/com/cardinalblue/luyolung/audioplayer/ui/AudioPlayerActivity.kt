package com.cardinalblue.luyolung.audioplayer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import android.widget.Toast
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import com.cardinalblue.luyolung.audioplayer.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_audio_player.*
import java.util.concurrent.TimeUnit

// Basic audio player example from:
// https://www.tutlane.com/tutorial/android/android-audio-media-player-with-examples

class AudioPlayerActivity : AppCompatActivity() {

    private val disposableBag = CompositeDisposable()

    private var mPlayer: MediaPlayer? = null

    private var oTime = 0
    private var sTime = 0
    private var eTime = 0
    private val fTime = 5000
    private val bTime = 5000
    private val hdlr = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        mPlayer = MediaPlayer.create(this, R.raw.mp3_example)

        sBar.isClickable = false
        btnPause.isEnabled = false

        RxView.clicks(btnPlay)
            .subscribe {
                Toast.makeText(this@AudioPlayerActivity, "Playing Audio", Toast.LENGTH_SHORT).show()
                mPlayer!!.start()
                eTime = mPlayer!!.duration
                sTime = mPlayer!!.currentPosition
                if (oTime == 0) {
                    sBar.max = eTime
                    oTime = 1
                }
                txtSongTime!!.setText(
                    String.format(
                        "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(eTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(eTime.toLong())
                        )
                    )
                )
                txtStartTime!!.setText(
                    String.format(
                        "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(sTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                sTime.toLong()
                            )
                        )
                    )
                )
                sBar.progress = sTime
                hdlr.postDelayed(UpdateSongTime, 100)
                showPauseButton()
            }.addTo(disposableBag)


        RxView.clicks(btnPause)
            .subscribe {
                mPlayer!!.pause()
                showPlayButton()

                Toast.makeText(applicationContext, "Pausing Audio", Toast.LENGTH_SHORT).show()
            }.addTo(disposableBag)

        RxView.clicks(btnForward)
            .subscribe {
                if (sTime + fTime <= eTime) {
                    sTime = sTime + fTime
                    mPlayer!!.seekTo(sTime)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Cannot jump forward 5 seconds",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (!btnPlay.isEnabled) {
                    btnPlay.isEnabled = true
                }
            }.addTo(disposableBag)


        RxView.clicks(btnBackward)
            .subscribe {
                if (sTime - bTime > 0) {
                    sTime = sTime - bTime
                    mPlayer!!.seekTo(sTime)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Cannot jump backward 5 seconds",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (!btnPlay.isEnabled) {
                    btnPlay.isEnabled = true
                }
            }.addTo(disposableBag)

    }

    private val UpdateSongTime = object : Runnable {
        override fun run() {
            sTime = mPlayer!!.currentPosition
            txtStartTime!!.setText(
                String.format(
                    "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(sTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            sTime.toLong()
                        )
                    )
                )
            )
            sBar.progress = sTime
            hdlr.postDelayed(this, 100)
        }
    }

    private fun showPlayButton() {
        btnPlay.isEnabled = true
        btnPlay.isVisible = true

        btnPause.isEnabled = false
        btnPause.isVisible = false
    }

    private fun showPauseButton() {
        btnPlay.isEnabled = false
        btnPlay.isVisible = false

        btnPause.isEnabled = true
        btnPause.isVisible = true
    }
}


var View.isVisible: Boolean
    set(value) {
        visibility = if(value){
            View.VISIBLE
        }else{
            View.GONE
        }
    }
    get() = visibility == View.VISIBLE