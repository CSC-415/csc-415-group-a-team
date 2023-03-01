package com.example.canvasapp.UI

import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canvasapp.R
import com.example.canvasapp.UI.adapter.GalleryAdapter
import com.example.canvasapp.databinding.FragmentGalleryListBinding
import com.example.canvasapp.model.Gallery_item
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

class GalleryListFragment : Fragment() {
    private var _binding: FragmentGalleryListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryListBinding.inflate(inflater, container, false)
        binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        val gallery = mutableListOf<Gallery_item>()

        for (i in 1..10) {
            gallery.add(
                createGalleryItem(
                    "https://th.bing.com/th/id/OIP.GAvKZNNy_8tdt9sZgZwQjQHaJQ?w=186&h=233&c=7&r=0&o=5&dpr=1.7&pid=1.7",
                    "Item $i",
                    "A cool stick dude"
                )
            )
        }

        val adapter = GalleryAdapter(gallery) { position ->
            val galleryItem = gallery[position]

            val bundle = bundleOf(
                "name" to galleryItem.name,
                "image" to galleryItem.image,
                "description" to galleryItem.description,
                "editDate" to galleryItem.editDate
            )
            val detailFragment = GalleryDetailFragment()
            detailFragment.arguments = bundle

            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_view, detailFragment)
                addToBackStack(null)
            }
        }
        binding.galleryRecyclerView.adapter = adapter

        return binding.root

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun createGalleryItem(image: String, name: String, description: String) =
        Gallery_item(
            name = name,
            image = image,
            description = description,
            editDate = formatDate(Date())
        )

    private fun formatDate(date: Date): String {
        val input = SimpleDateFormat("dd/mm/yyyy")
        val formatted = input.format(date)
        return formatted.toString()
    }

}