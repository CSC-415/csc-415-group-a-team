package com.example.canvasapp.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.FragmentGalleryItemDetailBinding
import com.example.canvasapp.viewModel.GalleryItemViewModel

class GalleryDetailFragment : Fragment() {
    private var _binding: FragmentGalleryItemDetailBinding? = null
    private val binding get() = _binding!!

    private val galleryItemViewModel: GalleryItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryItemDetailBinding.inflate(inflater, container, false)

        if (arguments != null) {
            val galleryItem = galleryItemViewModel.fetchById(requireArguments().getInt(BUNDLE_ID))

            Glide.with(this).load(galleryItem.image).into(binding.FragGalleryImage)
            binding.FragGalleryName.text = galleryItem.name
            binding.FragGalleryDate.text = galleryItem.editDate
        }
        return binding.root
    }

    companion object {
        private const val BUNDLE_ID = "character_id"

        fun newInstance(id: Int) = GalleryDetailFragment().apply {
            arguments = bundleOf(BUNDLE_ID to id)
        }
    }
}