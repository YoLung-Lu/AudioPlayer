package com.cardinalblue.luyolung.audioplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cardinalblue.luyolung.audioplayer.R
import com.cardinalblue.luyolung.audioplayer.model.MyAudio
import kotlinx.android.synthetic.main.item_audio.view.*


class AudioListAdapter(private val audioData: List<MyAudio>) :
    RecyclerView.Adapter<AudioListAdapter.MyViewHolder>() {

    class MyViewHolder(val myItemView: View) : RecyclerView.ViewHolder(myItemView)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.myItemView.title.text = audioData[position].title
        holder.myItemView.album.text = audioData[position].album
        holder.myItemView.artist.text = audioData[position].artist
//        holder.myItemView.artist.text = audioData[position].uri.toString()
    }

    override fun getItemCount() = audioData.size

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val myItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio, parent, false)

        return MyViewHolder(myItemView)
    }
}