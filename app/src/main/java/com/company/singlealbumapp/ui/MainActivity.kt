package com.company.singlealbumapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.company.singlealbumapp.adapter.OnInteractionListener
import com.company.singlealbumapp.adapter.TrackAdapter
import com.company.singlealbumapp.api.MediaApiService
import com.company.singlealbumapp.databinding.ActivityMainBinding
import com.company.singlealbumapp.dto.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: MediaApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onClick(track: Track) {
                Toast.makeText(this@MainActivity, track.file, Toast.LENGTH_LONG).show()
            }
        })

        binding.rvTracks.adapter = adapter

        val tracks = mutableListOf<Track>()

        lifecycleScope.launch {
            try {
                val body = apiService.getAlbum()
                with(binding) {
                    tvAlbumName.text = body.title
                    tvAuthorName.text = body.artist
                    tvAlbumYear.text = body.published
                    tvAlbumGenre.text = body.genre
                }
                for (track in body.tracks) {
                    tracks.add(Track(track.id, track.file))
                }
                adapter.submitList(tracks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}