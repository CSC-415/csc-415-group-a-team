package com.example.canvasapp.ui

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.canvasapp.MainActivity
import com.example.canvasapp.R
import com.example.canvasapp.databinding.MainMenuViewBinding
import SharedViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainMenuFragment : Fragment() {
    private var _binding: MainMenuViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainMenuViewBinding.inflate(inflater, container, false)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.backgroundColor.observe(viewLifecycleOwner, Observer { color ->
            binding.mainMenuLayout.setBackgroundColor(color)
        })

        binding.openCanvasButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(false)
                replace(
                    R.id.fragment_container_view, CanvasMainFragment()
                )
                addToBackStack(null)
            }
        }

        binding.galleryButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(false)
                replace(
                    R.id.fragment_container_view, GalleryListFragment()
                )
                addToBackStack(null)
            }
        }

        val checkedItem = intArrayOf(-1)
        binding.themesButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Choose a Theme")
            val themes = arrayOf("Dark", "Light", "Deep Sea", "Rainforest", "Tangerine")
            alertDialog.setSingleChoiceItems(themes, checkedItem[0]) { dialog, which ->
                checkedItem[0] = which
                when (themes[which]) {
                    "Dark" -> sharedViewModel.backgroundColor.value = Color.parseColor("#333333")
                    "Light" -> sharedViewModel.backgroundColor.value = Color.parseColor("#f2f5ff")
                    "Deep Sea" -> sharedViewModel.backgroundColor.value = Color.parseColor("#2d4491")
                    "Rainforest" -> sharedViewModel.backgroundColor.value = Color.parseColor("#18471d")
                    "Tangerine" -> sharedViewModel.backgroundColor.value = Color.parseColor("#ff6800")
                }
                dialog.dismiss()

            }

            alertDialog.setNegativeButton("Cancel") { _, _ -> }

            val customAlertDialog = alertDialog.create()
            customAlertDialog.show()
        }
        return binding.root
    }
}