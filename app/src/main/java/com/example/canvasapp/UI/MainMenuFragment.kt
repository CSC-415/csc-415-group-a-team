package com.example.canvasapp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.canvasapp.R
import com.example.canvasapp.databinding.MainMenuViewBinding
import com.example.canvasapp.ui.CanvasMainFragment

class MainMenuFragment : Fragment() {
    private var _binding: MainMenuViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainMenuViewBinding.inflate(inflater, container, false)

        binding.NewCanvasButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(false)
                replace(
                    R.id.fragment_container_view,
                    CanvasMainFragment()
                )
                addToBackStack(null)
            }
        }

        binding.GalleryButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(false)
                replace(
                    R.id.fragment_container_view,
                    GalleryListFragment()
                )
                addToBackStack(null)
            }
        }
        return binding.root
    }
}