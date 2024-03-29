package com.example.canvasapp.ui


import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.canvasapp.R
import com.example.canvasapp.ui.adapter.GalleryAdapter
import com.example.canvasapp.databinding.FragmentGalleryListBinding
import com.example.canvasapp.model.Gallery_item
import com.example.canvasapp.viewModel.GalleryItemViewModel

class GalleryListFragment : Fragment() {
    private var _binding: FragmentGalleryListBinding? = null
    private val binding get() = _binding!!


    private val galleryItemViewModel: GalleryItemViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryListBinding.inflate(inflater, container, false)
        binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.backgroundColor.observe(viewLifecycleOwner, Observer { color ->
            binding.galleryRecyclerView.setBackgroundColor(color)
        })

        val gallery = galleryItemViewModel.refreshData()

        if (gallery.isEmpty()){
            binding.emptyText.isVisible = true
            binding.galleryRecyclerView.isVisible = false
        } else {
            binding.emptyText.isVisible = false
            binding.galleryRecyclerView.isVisible = true

            val adapter = GalleryAdapter(gallery as MutableList<Gallery_item>) { position ->
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.fragment_container_view,
                        GalleryDetailFragment.newInstance(gallery[position].id)
                    )
                    addToBackStack(null)
                }
            }
            binding.galleryRecyclerView.adapter = adapter
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}