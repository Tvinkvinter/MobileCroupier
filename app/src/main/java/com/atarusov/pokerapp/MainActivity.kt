package com.atarusov.pokerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.atarusov.pokerapp.databinding.ActivityMainBinding
import com.atarusov.pokerapp.screens.ConfigureScreenFragment
import com.atarusov.pokerapp.screens.GameScreenFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}