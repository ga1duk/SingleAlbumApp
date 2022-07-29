package com.company.singlealbumapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.company.singlealbumapp.BuildConfig.BASE_URL
import com.company.singlealbumapp.adapter.OnInteractionListener
import com.company.singlealbumapp.adapter.TrackAdapter
import com.company.singlealbumapp.api.MediaApiService
import com.company.singlealbumapp.databinding.ActivityMainBinding
import com.company.singlealbumapp.dto.Track
import com.company.singlealbumapp.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModels()
    private val mediaObserver = MediaLifecycleObserver()

    @Inject
    lateinit var apiService: MediaApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isButtonPlayFirstClick = true
        var currentTrack = 0

        val tracks = mutableListOf<Track>()

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayClick(track: Track) {
                viewModel.playTrack(track)
                if (isButtonPlayFirstClick || currentTrack != track.id) {
                    mediaObserver.player?.reset()
                    mediaObserver.apply {
                        player?.setDataSource("$BASE_URL${track.file}")
                    }
                    mediaObserver.player?.prepareAsync()
                    mediaObserver.player.apply {
                        this?.setOnPreparedListener {
                            start()
                        }
                    }
                    isButtonPlayFirstClick = false
                } else {
                    mediaObserver.player?.start()
                }

                currentTrack = track.id

                mediaObserver.player?.setOnCompletionListener {
                    viewModel.pauseTrack(track)
                    if (track.id == 16) {
                        binding.rvTracks.smoothScrollToPosition(0)
                        onPlayClick(Track(track.id - 15, track.file))
                    } else {
                        onPlayClick(Track(track.id + 1, track.file))
                    }
                    it.release()
                }
            }

            override fun onPauseClick(track: Track) {
                viewModel.pauseTrack(track)
                mediaObserver.player?.pause()
            }
        })

        lifecycle.addObserver(mediaObserver)

        binding.rvTracks.adapter = adapter

        viewModel.albumData.observe(this)
        { album ->
            with(binding) {
                tvAlbumName.text = album.title
                tvAuthorName.text = album.artist
                tvAlbumYear.text = album.published
                tvAlbumGenre.text = album.genre
            }
            for (track in album.tracks) {
                tracks.add(Track(track.id, track.file, track.isPlaying))
            }
            adapter.submitList(tracks)
        }

        viewModel.trackData.observe(this)
        {
            for (track in tracks) {
                if (it.id == track.id) {
                    track.isPlaying = it.isPlaying
                } else {
                    track.isPlaying = false
                }
            }
            adapter.submitList(tracks)
            adapter.notifyDataSetChanged()
        }
    }
}