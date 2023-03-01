package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.FragmentGalleryItemDetailBinding

class GalleryDetailFragment: Fragment() {
    private var _binding: FragmentGalleryItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryItemDetailBinding.inflate(inflater, container, false)

        if (arguments != null) {
            val name = requireArguments().getString("name")
            val image = requireArguments().getString("image")
            val description = requireArguments().getString("description")
            val date = requireArguments().getString("editDate")

            Glide.with(this).load(image).into(binding.FragGalleryImage)
            binding.FragGalleryName.text = name
            binding.FragGalleryDescription.text = description
            binding.FragGalleryDate.text = date
        }
        return binding.root
    }
}