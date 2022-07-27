package com.company.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.singlealbumapp.databinding.CardTrackBinding
import com.company.singlealbumapp.dto.Track

interface OnInteractionListener {
    fun onPlayClick(track: Track) {}
    fun onPauseClick(track: Track) {}
}

class TrackAdapter(private val listener: OnInteractionListener) :
    ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = CardTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}


class TrackViewHolder(
    private val binding: CardTrackBinding,
    private val listener: OnInteractionListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        with(binding) {
            btnPause.visibility = View.INVISIBLE
            btnPlay.visibility = View.VISIBLE
            tvTrackName.text = track.file
            btnPlay.setOnClickListener {
                listener.onPlayClick(track)
                btnPlay.visibility = View.INVISIBLE
                btnPause.visibility = View.VISIBLE
            }
            btnPause.setOnClickListener {
                listener.onPauseClick(track)
                btnPlay.visibility = View.VISIBLE
                btnPause.visibility = View.INVISIBLE
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}