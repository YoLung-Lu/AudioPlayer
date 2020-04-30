package com.cardinalblue.luyolung.audioplayer.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cardinalblue.luyolung.audioplayer.R
import com.cardinalblue.luyolung.audioplayer.db.AudioRepository
import com.cardinalblue.luyolung.audioplayer.model.MyAudio
import com.cardinalblue.luyolung.audioplayer.ui.adapter.AudioListAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment: Fragment() {

    private val disposableBag = CompositeDisposable()
    lateinit var rootView: View
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private lateinit var repository: AudioRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_item_list, container, false)!!

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = AudioRepository(activity!!.contentResolver)
        repository.loadAudio()

        setupView()
    }

    private fun setupView() {
        viewAdapter = AudioListAdapter(loadData())

        textContent.text = repository.audioList.size.toString()

        itemListRecycler.apply {
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(context)

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    private fun loadData(): List<MyAudio> {
        val data = mutableListOf<MyAudio>(
            MyAudio(Uri.EMPTY, "data", "title", "album", "artist"),
            MyAudio(Uri.EMPTY, "data2", "title2", "album2", "artist2")
        )

        data.addAll(repository.audioList)

        return data
    }

    override fun onStop() {
        super.onStop()
        disposableBag.clear()
    }
}