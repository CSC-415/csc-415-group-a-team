package com.example.canvasapp.ui


import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.canvasapp.R
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

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.backgroundColor.observe(viewLifecycleOwner, Observer { color ->
            binding.FragGalleryBackground.setBackgroundColor(color)
        })

        if (arguments != null) {
            val galleryItem = galleryItemViewModel.fetchById(requireArguments().getInt(BUNDLE_ID))

            Glide.with(this).load(galleryItem.image).into(binding.FragGalleryImage)
            binding.FragGalleryName.text = galleryItem.name
            binding.FragGalleryDate.text = galleryItem.editDate

            //share button
            binding.FragGalleryShare.setOnClickListener {
                val image = galleryItem.image
                val bundle = Bundle()
                bundle.putString("image", image)
                val fragment = ShareFragment()
                fragment.arguments = bundle
               fragment.show(childFragmentManager, "share")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val BUNDLE_ID = "gallery_id"

        fun newInstance(id: Int) = GalleryDetailFragment().apply {
            arguments = bundleOf(BUNDLE_ID to id)
        }
    }
}