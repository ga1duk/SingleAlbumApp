package com.company.singlealbumapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.company.singlealbumapp.api.MediaApi
import com.company.singlealbumapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val body = MediaApi.retrofitService.getAlbum()
                binding.tvTitle.text = body.title
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}