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
import com.example.canvasapp.R
import com.example.canvasapp.databinding.MainMenuViewBinding

class MainMenuFragment : Fragment() {
    private var _binding: MainMenuViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainMenuViewBinding.inflate(inflater, container, false)


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
            val themes = arrayOf("Dark", "Light", "Deep Sea", "Rainforest")
            alertDialog.setSingleChoiceItems(themes, checkedItem[0]) { dialog, which ->
                checkedItem[0] = which
                when (themes[which]){
                    "Dark" -> binding.mainMenuLayout.setBackgroundColor(Color.parseColor("#333333"))
                    "Light" -> binding.mainMenuLayout.setBackgroundColor(Color.parseColor("#f2f5ff"))
                    "Deep Sea" -> binding.mainMenuLayout.setBackgroundColor(Color.parseColor("#2d4491"))
                    "Rainforest" -> binding.mainMenuLayout.setBackgroundColor(Color.parseColor("#18471d"))
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