package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.FragmentCanvasMainViewBinding

class CanvasMainFragment : Fragment() {
    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!
    //temp image url for demonstration
    private val image = "https://th.bing.com/th/id/OIP.GAvKZNNy_8tdt9sZgZwQjQHaJQ?w=186&h=233&c=7&r=0&o=5&dpr=1.7&pid=1.7"

    //very basic but just inflates a blank view for now

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasMainViewBinding.inflate(inflater, container, false)

        Glide.with(this).load(image).into(binding.canvasGalleryImage)

        return binding.root
    }
}