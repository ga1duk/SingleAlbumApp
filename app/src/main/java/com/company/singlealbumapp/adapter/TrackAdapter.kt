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
            tvTrackName.text = track.file

            tvAlbumName.text = track.albumName

            btnPlay.visibility = if (!track.isPlaying) View.VISIBLE else View.INVISIBLE
            btnPause.visibility = if (track.isPlaying) View.VISIBLE else View.INVISIBLE

            btnPlay.setOnClickListener {
                listener.onPlayClick(track)
            }
            btnPause.setOnClickListener {
                listener.onPauseClick(track)
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