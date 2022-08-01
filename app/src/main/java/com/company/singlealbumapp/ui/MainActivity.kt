package com.company.singlealbumapp.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.company.singlealbumapp.BuildConfig.BASE_URL
import com.company.singlealbumapp.adapter.OnInteractionListener
import com.company.singlealbumapp.adapter.TrackAdapter
import com.company.singlealbumapp.databinding.ActivityMainBinding
import com.company.singlealbumapp.dto.Track
import com.company.singlealbumapp.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModels()
    private val mediaObserver = MediaLifecycleObserver()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isButtonPlayFirstClick = true
        var currentTrack = 0

        val tracks = mutableListOf<Track>()

        binding.seekBar.progress = 0

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayClick(track: Track) {
                if (isButtonPlayFirstClick || currentTrack != track.id) {
                    mediaObserver.reset()
                    mediaObserver.apply {
                        player?.setDataSource("$BASE_URL${track.file}")
                    }
                    mediaObserver.play()

                    isButtonPlayFirstClick = false
                } else {
                    mediaObserver.resume()
                }

                currentTrack = track.id

                viewModel.playTrack(track)

                binding.seekBar.max.let {
                    mediaObserver.player?.duration
                }

                    val totalSecs = mediaObserver.player?.duration?.div(1000)
                    val minutes = (totalSecs?.rem(3600))?.div(60)
                    val seconds = totalSecs?.rem(60)
                    binding.tvTrackDuration.text = String.format("%02d:%02d", minutes, seconds)

                    lifecycleScope.launch {
                        try {
                            var currentPosition = 0
                            val total =
                                mediaObserver.player?.duration
                            binding.seekBar.max = total ?: 0
                            while (mediaObserver.player != null && currentPosition < (total ?: 0)) {
                                delay(1000)

                                val totalSecs = mediaObserver.player?.currentPosition?.div(1000)
                                val minutes = (totalSecs?.rem(3600))?.div(60)
                                val seconds = totalSecs?.rem(60)
                                binding.tvTrackProgress.text =
                                    String.format("%02d:%02d", minutes, seconds)

                                currentPosition = mediaObserver.player?.currentPosition ?: 0

                                binding.seekBar.progress = currentPosition
                            }
                        } catch (e: InterruptedException) {
                            return@launch
                        } catch (e: Exception) {
                            return@launch
                        }
                    }

                mediaObserver.player?.setOnCompletionListener {
                    mediaObserver.stop()
                    if (track.id == tracks.size) {
                        binding.rvTracks.smoothScrollToPosition(0)
                        onPlayClick(Track(tracks[0].id, tracks[0].file))
                    } else {
                        onPlayClick(Track(track.id + 1, tracks[track.id].file))
                    }
                }
            }

            override fun onPauseClick(track: Track) {
                if (mediaObserver.player?.isPlaying == true) {
                    mediaObserver.pause()
                    viewModel.pauseTrack(track)
                }
            }
        })

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaObserver.player?.seekTo(progress)
                }
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