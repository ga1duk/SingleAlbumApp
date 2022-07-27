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
        var currentTrack = 0L

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

                    player.setOnCompletionListener {
                        it.release()
                    }
                    player.start()
                    println(player.duration / 1000)
                }

            override fun onPauseClick(track: Track) {
                player.pause()
            }
        })

        lifecycle.addObserver(mediaObserver)

        binding.rvTracks.adapter = adapter

        val tracks = mutableListOf<Track>()

        viewModel.data.observe(this) { album ->
            with(binding) {
                tvAlbumName.text = album.title
                tvAuthorName.text = album.artist
                tvAlbumYear.text = album.published
                tvAlbumGenre.text = album.genre
            }
            for (track in album.tracks) {
                tracks.add(Track(track.id, track.file))
            }
            adapter.submitList(tracks)
        }
    }
}