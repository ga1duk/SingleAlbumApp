package com.company.singlealbumapp.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.company.singlealbumapp.BuildConfig.BASE_URL
import com.company.singlealbumapp.R
import com.company.singlealbumapp.adapter.OnInteractionListener
import com.company.singlealbumapp.adapter.TrackAdapter
import com.company.singlealbumapp.databinding.ActivityMainBinding
import com.company.singlealbumapp.dto.Track
import com.company.singlealbumapp.viewmodel.MediaViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModels()

    var currentTrack: Track? = null

    private lateinit var binding: ActivityMainBinding
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isButtonPlayFirstClick = true
        var currentTrackId = 0

        val tracks = mutableListOf<Track>()

        binding.seekBar.progress = 0

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayClick(track: Track) {
                currentTrack = track
                if (isButtonPlayFirstClick || currentTrackId != track.id) {
                    mediaObserver.reset()
                        mediaObserver.apply {
                            player?.setDataSource("$BASE_URL${track.file}")
                        }
                    mediaObserver.play()

                    isButtonPlayFirstClick = false
                } else {
                    mediaObserver.resume()
                }

                currentTrackId = track.id

                viewModel.playTrack(track)

                binding.seekBar.max.let {
                    mediaObserver.player?.duration
                }

//                Форматируем длину трека в секундах в формат мм:сс и присваиваем это значение для tvTrackDuration
                convertAndSetTrackDuration(binding)

                lifecycleScope.launch {
                    try {
                        var currentPosition = 0
                        val total =
                            mediaObserver.player?.duration
                        binding.seekBar.max = total ?: 0
                        while (mediaObserver.player != null && currentPosition < (total ?: 0)) {
                            delay(1000)

//                            Форматируем текущую позицию проигрывания трека в формат мм:сс и присваиваем это значение для tvTrackProgress
                            convertAndSetTrackCurrentPosition(binding)

                            currentPosition = mediaObserver.player?.currentPosition ?: 0

//                            изменяем положение бегунка в seekBar'e, в соответствии с позицией проигрывания трека
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

//        Вешаем listener на SeekBar для перемещения бегунка в то место, до которого его подвинул пользователь (перематываем композицию вперёд/назад)
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
                tvAlbumTitle.text = album.title
                tvAuthorName.text = album.artist
                tvAlbumYear.text = album.published
                tvAlbumGenre.text = album.genre
            }
            for (track in album.tracks) {
                tracks.add(Track(track.id, track.file, track.isPlaying, album.title))
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

        viewModel.dataState.observe(this) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") { viewModel.loadAlbum() }
                    .show()
            }
        }
    }

    private fun convertAndSetTrackDuration(binding: ActivityMainBinding) {
        val totalDuration = mediaObserver.player?.duration?.div(1000)
        val durationInMins = (totalDuration?.rem(3600))?.div(60)
        val durationInSecs = totalDuration?.rem(60)
        binding.tvTrackDuration.text = String.format("%02d:%02d", durationInMins, durationInSecs)
    }

    private fun convertAndSetTrackCurrentPosition(binding: ActivityMainBinding) {
        val currentPosition = mediaObserver.player?.currentPosition?.div(1000)
        val positionInMins = (currentPosition?.rem(3600))?.div(60)
        val positionInSecs = currentPosition?.rem(60)
        binding.tvTrackProgress.text = String.format("%02d:%02d", positionInMins, positionInSecs)
    }

    override fun onPause() {
        super.onPause()
        currentTrack?.let { viewModel.pauseTrack(it) }
    }
}