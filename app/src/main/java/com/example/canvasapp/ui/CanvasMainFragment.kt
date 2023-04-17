package com.example.canvasapp.ui

import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.FragmentCanvasMainViewBinding
import com.example.canvasapp.views.DrawView

class CanvasMainFragment : Fragment() {
    companion object{
        var path = Path()
        var paintBrush = Paint()
    }

    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!

    //temp image urls for demonstration
    private val brush = "https://toppng.com/uploads/preview/paint-brush-clip-art-png-11553987172jeybqknc0s.png"
    private val fill = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRalQBVMJn5Q3IpfVgaymKNevK5zNxEgbUc9w&usqp=CAU"
    private val picker = "https://www.pngitem.com/pimgs/m/69-695490_dropper-dropper-png-transparent-png.png"
    private val color = "https://i.stack.imgur.com/SBvcU.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasMainViewBinding.inflate(inflater, container, false)
        Glide.with(this).load(brush).into(binding.canvasMainBrushTool)
        Glide.with(this).load(fill).into(binding.canvasMainFillTool)
        Glide.with(this).load(picker).into(binding.canvasMainPickerTool)
        Glide.with(this).load(color).into(binding.canvasMainColorTool)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the slider using ViewBinding
        binding.canvasMainSizeSlider.addOnChangeListener { _, value, _ ->
            DrawView.brushSize = value
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}