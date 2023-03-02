package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.canvasapp.databinding.FragmentCanvasMainViewBinding

class CanvasMainFragment : Fragment() {
    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!

    //very basic but just inflates a blank view for now

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCanvasMainViewBinding.inflate(inflater, container, false)

        return binding.root
    }
}