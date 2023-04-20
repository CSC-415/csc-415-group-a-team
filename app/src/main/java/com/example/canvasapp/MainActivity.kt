package com.example.canvasapp

import SharedViewModel
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.example.canvasapp.ui.MainMenuFragment
import com.example.canvasapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel.backgroundColor.value = Color.parseColor("#2d4491")

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container_view, MainMenuFragment())
        }
    }
}