package com.example.canvasapp.ui

import android.app.Activity
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
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.canvasapp.R
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener


class CanvasMainFragment : Fragment() {
    companion object {
        var path = Path()
        var paintBrush = Paint()
        var lastColor = Color.BLACK
    }

    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!
    private val buttonDefaultBackground = "#FFFFFF"
    private val buttonClickedBackground = "#777777"

    //temp image urls for demonstration
    private val brush =
        "https://www.pngall.com/wp-content/uploads/2016/04/Paint-Brush-Free-Download-PNG.png"
    private val eraser =
        "https://i.pinimg.com/originals/bd/b9/e7/bdb9e71fb37b7eb8e0a19a45409cba50.png"
    private val picker =
        "https://static.vecteezy.com/system/resources/previews/008/505/803/original/dropper-illustration-medical-pipette-eyedropper-png.png"
    private val color = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/BYR_color_wheel.svg/1024px-BYR_color_wheel.svg.png"

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
            buttonBackgroundReset()
            binding.canvasMainEraserTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.currentBrush = Color.WHITE
        }

        //Brush button (sets to last color)
        binding.canvasMainBrushTool.setOnClickListener {
            buttonBackgroundReset()
            binding.canvasMainBrushTool.setBackgroundColor(lastColor)
            DrawView.currentBrush = lastColor
        }

        //Color Picker (temp set to red)
        binding.canvasMainColorTool.setOnClickListener {
            buttonBackgroundReset()
            openColorPickerDialogue()

        }

        //Color Tool (temp set to blue)
        binding.canvasMainPickerTool.setOnClickListener {
            buttonBackgroundReset()
            binding.canvasMainPickerTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.currentBrush = Color.CYAN
        }

        //Save button
        binding.saveButton.setOnClickListener {
            createFile("sample.png", startActivityForResult)
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

    fun buttonBackgroundReset() {
        binding.canvasMainPickerTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        binding.canvasMainBrushTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        binding.canvasMainColorTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        binding.canvasMainEraserTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
    }

    fun openColorPickerDialogue() {
        val colorPickerDialogue = AmbilWarnaDialog(requireActivity(), DrawView.currentBrush,
            object : OnAmbilWarnaListener {
                override fun onCancel(dialogue: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    DrawView.currentBrush = color
                    binding.canvasMainBrushTool.setBackgroundColor(color)
                    lastColor = color
                }
            })
        colorPickerDialogue.show()
    }


}