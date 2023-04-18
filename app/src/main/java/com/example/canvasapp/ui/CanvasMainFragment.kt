package com.example.canvasapp.ui

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.FragmentCanvasMainViewBinding
import com.example.canvasapp.views.DrawView
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.canvasapp.R
import java.io.File
import java.io.FileOutputStream
import java.util.*


class CanvasMainFragment : Fragment() {
    companion object {
        var path = Path()
        var paintBrush = Paint()
        var lastColor = Color.BLACK
    }

    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!

    //temp image urls for demonstration
    private val brush =
        "https://toppng.com/uploads/preview/paint-brush-clip-art-png-11553987172jeybqknc0s.png"
    private val eraser =
        "https://edfoundationlake.com/wp-content/uploads/2020/05/95094a48409b03fadc24b76e229d4180_soft-pink-beveled-eraser-blick-pink-eraser-clipart_600-456_large.jpeg"
    private val picker =
        "https://www.pngitem.com/pimgs/m/69-695490_dropper-dropper-png-transparent-png.png"
    private val color = "https://i.stack.imgur.com/SBvcU.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasMainViewBinding.inflate(inflater, container, false)
        Glide.with(this).load(brush).into(binding.canvasMainBrushTool)
        Glide.with(this).load(eraser).into(binding.canvasMainEraserTool)
        Glide.with(this).load(picker).into(binding.canvasMainPickerTool)
        Glide.with(this).load(color).into(binding.canvasMainColorTool)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null && it.data!!.data != null) {
                        val drawView =
                            binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
                        val bmp = drawView.save()
                        val uri: Uri = it.data!!.data!!
                        val op = requireActivity().contentResolver.openOutputStream(uri)
                        bmp?.compress(Bitmap.CompressFormat.PNG, 100, op)
                    }
                }
            }

        // Set up the slider using ViewBinding
        binding.canvasMainSizeSlider.addOnChangeListener { _, value, _ ->
            DrawView.brushSize = value
        }

        // Opacity Slider
        binding.canvasMainOpacitySlider.addOnChangeListener { _, value, _ ->
            DrawView.brushOpacity = value.toInt()
        }

        //Eraser button
        binding.canvasMainEraserTool.setOnClickListener {
            lastColor = DrawView.currentBrush
            DrawView.currentBrush = Color.WHITE
        }

        //Brush button (sets to last color)
        binding.canvasMainBrushTool.setOnClickListener {
            DrawView.currentBrush = lastColor
        }

        //Color Picker (temp set to red)
        binding.canvasMainColorTool.setOnClickListener {
            DrawView.currentBrush = Color.RED
        }

        //Color Tool (temp set to blue)
        binding.canvasMainPickerTool.setOnClickListener {
            DrawView.currentBrush = Color.CYAN
        }

        //Save button
        binding.saveButton.setOnClickListener {
            val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
            val bmp = drawView.save()
            var file = context?.getDir("Images", Context.MODE_PRIVATE)
            val time = Date()
            val formatted = formatDate(time)
            file = File(file, formatted)
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Log.d("Save", "image save")
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createFile(fileName: String, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        launcher.launch(intent)
    }

    private fun formatDate(date: Date): String {
        val input = SimpleDateFormat("ddmmyyyyHHmmss")
        val formatted = input.format(date)
        return formatted.toString()
    }

}