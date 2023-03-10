package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.canvasapp.databinding.MainMenuViewBinding

class MainMenuFragment : Fragment() {
    private var _binding: MainMenuViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainMenuViewBinding.inflate(inflater, container, false)
        return binding.root
    }
}
