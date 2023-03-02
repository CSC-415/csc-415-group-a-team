package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.canvasapp.*
import com.example.canvasapp.databinding.FragmentCanvasViewBinding
import com.example.canvasapp.databinding.FragmentGalleryListBinding

class CanvasMainFragment : Fragment() {
    private var _binding: FragmentCanvasViewBinding? = null
    private val binding get() = _binding!!

    //very basic but just inflates a blank view for now

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCanvasViewBinding.inflate(inflater, container, false)

        return binding.root
    }
}