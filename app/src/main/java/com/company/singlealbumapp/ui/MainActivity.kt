package com.company.singlealbumapp.ui

import android.media.MediaPlayer
import android.net.Uri
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

        var player = MediaPlayer()
        var isButtonPlayFirstClick = true
        var currentTrack = 0

        val tracks = mutableListOf<Track>()

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayClick(track: Track) {
                if (isButtonPlayFirstClick || currentTrack != track.id) {
                    player.stop()
                    isButtonPlayFirstClick = false
                    player = MediaPlayer.create(
                        this@MainActivity,
                        Uri.parse("$BASE_URL${track.file}")
                    )
                    currentTrack = track.id
                }

                player.start()

                viewModel.playTrack(track)

                player.setOnCompletionListener {
                    player.stop()
                    viewModel.pauseTrack(track)
                    if (track.id == 16) {
                        binding.rvTracks.smoothScrollToPosition(0)
                        onPlayClick(Track(1, track.file))
                    } else {
                        onPlayClick(Track(track.id + 1, track.file))
                    }
                    it.release()
                }
            }

            override fun onPauseClick(track: Track) {
                if (currentTrack == track.id) {
                    player.pause()
                }
                viewModel.pauseTrack(track)
            }
        })

        lifecycle.addObserver(mediaObserver)

        binding.rvTracks.adapter = adapter

        viewModel.albumData.observe(this) { album ->
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

        viewModel.trackData.observe(this) {
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