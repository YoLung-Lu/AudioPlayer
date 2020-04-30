package com.cardinalblue.luyolung.audioplayer.util

import android.widget.SeekBar
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MyRxSeekBar {
    companion object {
        fun onSeekBarEvent(view: SeekBar): Observable<SeekBarEvent> {
            val subject = PublishSubject.create<SeekBarEvent>()
            val listener = object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    subject.onNext(SeekBarEvent.ProgressChanged(progress, fromUser))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    subject.onNext(SeekBarEvent.Start)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    subject.onNext(SeekBarEvent.End)
                }
            }

            view.setOnSeekBarChangeListener(listener)

            return subject.doOnDispose {
                view.setOnSeekBarChangeListener(null)
            }
        }
    }
}

sealed class SeekBarEvent {
    object Start : SeekBarEvent()
    object End : SeekBarEvent()
    class ProgressChanged(val progress: Int, val fromUser: Boolean) : SeekBarEvent()
}