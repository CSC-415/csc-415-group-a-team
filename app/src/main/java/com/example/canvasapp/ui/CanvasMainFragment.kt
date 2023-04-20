package com.example.canvasapp.ui

import SharedViewModel
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.canvasapp.R
import com.example.canvasapp.databinding.FragmentCanvasMainViewBinding
import com.example.canvasapp.views.DrawView
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.io.ByteArrayOutputStream


class CanvasMainFragment : Fragment() {
    companion object {
        var path = Path()
        var paintBrush = Paint()
        //var lastColor = Color.BLACK
    }

    private var _binding: FragmentCanvasMainViewBinding? = null
    private val binding get() = _binding!!
    private val buttonDefaultBackground = "#FFFFFF"
    private val buttonClickedBackground = "#777777"

    private val brush =
        "https://www.pngall.com/wp-content/uploads/2016/04/Paint-Brush-Free-Download-PNG.png"
    private val eraser =
        "https://i.pinimg.com/originals/bd/b9/e7/bdb9e71fb37b7eb8e0a19a45409cba50.png"
    private val picker =
        "https://static.vecteezy.com/system/resources/previews/008/505/803/original/dropper-illustration-medical-pipette-eyedropper-png.png"
    private val color =
        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/BYR_color_wheel.svg/1024px-BYR_color_wheel.svg.png"
    private val undo =
        "https://cdn.discordapp.com/attachments/710398709598257235/1098392316827410433/undo.png"
    private val redo =
        "https://cdn.discordapp.com/attachments/710398709598257235/1098392317049716846/redo.png"
    private val fillcan =
        "https://wiki.teamfortress.com/w/images/thumb/5/5b/Backpack_Paint_Can.png/250px-Backpack_Paint_Can.png"
    private val viewtools =
        "https://static.vecteezy.com/system/resources/thumbnails/010/150/710/small/eye-icon-sign-symbol-design-free-png.png"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasMainViewBinding.inflate(inflater, container, false)
        Glide.with(this).load(brush).into(binding.brushTool)
        Glide.with(this).load(eraser).into(binding.eraserTool)
        Glide.with(this).load(picker).into(binding.pickerTool)
        Glide.with(this).load(color).into(binding.colorTool)
        Glide.with(this).load(undo).into(binding.undoButton)
        Glide.with(this).load(redo).into(binding.redoButton)
        //Glide.with(this).load(fillcan).into(binding.fillCan)
        Glide.with(this).load(viewtools).into(binding.viewButton)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.backgroundColor.observe(viewLifecycleOwner, Observer { color ->
            binding.canvasViewLayout.setBackgroundColor(color)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
        drawView.fragment = this

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

        updateUndoRedoButtons()
        buttonBackgroundReset()

        binding.canvasMainSizeSlider.addOnChangeListener { _, value, _ ->
            DrawView.brushSize = value
        }

        binding.canvasMainOpacitySlider.addOnChangeListener { _, value, _ ->
            DrawView.brushOpacity = value.toInt()
        }

        //Eraser button
        binding.eraserTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.ERASER
            buttonBackgroundReset()
        }

        //Brush button (sets to last color)
        binding.brushTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.BRUSH
            buttonBackgroundReset()
        }

        //Color wheel
        binding.colorTool.setOnClickListener {
            openColorPickerDialogue()
            buttonBackgroundReset()
        }

        //Color picker
        binding.pickerTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.PICKER
            buttonBackgroundReset()
        }

        //Fill Can
//        binding.fillCan.setOnClickListener {
//            DrawView.currentTool = DrawView.Companion.Tool.FILLCAN
//            buttonBackgroundReset()
//        }

        binding.viewButton.setOnClickListener{
            if(binding.brushTool.visibility == VISIBLE) {
                binding.viewButton.setBackgroundColor(Color.parseColor(buttonClickedBackground))
                binding.brushTool.visibility = INVISIBLE
                binding.eraserTool.visibility = INVISIBLE
                //binding.fillCan.visibility = INVISIBLE
                binding.colorTool.visibility = INVISIBLE
                binding.pickerTool.visibility = INVISIBLE
            }
            else {
                binding.viewButton.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
                binding.brushTool.visibility = VISIBLE
                binding.eraserTool.visibility = VISIBLE
                //binding.fillCan.visibility = VISIBLE
                binding.colorTool.visibility = VISIBLE
                binding.pickerTool.visibility = VISIBLE
            }
        }

        //Save button
        binding.saveButton.setOnClickListener {
            val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
            val bmp = drawView.save()
            val outputStream = ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b = outputStream.toByteArray()
            val bundle = Bundle()
            bundle.putByteArray("data", b)
            val fragment = NameDialogFragment()
            fragment.arguments = bundle
            fragment.show(childFragmentManager, "save")
        }

        //Undo Button
        binding.undoButton.setOnClickListener {
            val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
            drawView.undo()
            updateUndoRedoButtons()
        }

        //Redo Button
        binding.redoButton.setOnClickListener {
            val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
            drawView.redo()
            updateUndoRedoButtons()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun buttonBackgroundReset() {
        binding.pickerTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        val blendColor = ColorUtils.blendARGB(Color.parseColor("#404040"), DrawView.currentBrush,0.5F)
        binding.brushTool.setBackgroundColor(blendColor)
        binding.colorTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        binding.eraserTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        //binding.fillCan.setBackgroundColor(Color.parseColor(buttonDefaultBackground))

        when (DrawView.currentTool) {
            DrawView.Companion.Tool.BRUSH ->
                binding.brushTool.setBackgroundColor(DrawView.currentBrush)
            DrawView.Companion.Tool.ERASER ->
                binding.eraserTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.Companion.Tool.PICKER ->
                binding.pickerTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.Companion.Tool.FILLCAN ->
                TODO()
                //binding.fillCan.setBackgroundColor(Color.parseColor(buttonClickedBackground))
        }
    }

    fun openColorPickerDialogue() {
        val colorPickerDialogue = AmbilWarnaDialog(requireActivity(), DrawView.currentBrush,
            object : OnAmbilWarnaListener {
                override fun onCancel(dialogue: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    DrawView.currentBrush = color
                    DrawView.currentTool = DrawView.Companion.Tool.BRUSH
                    buttonBackgroundReset()
                }
            })
        colorPickerDialogue.show()
    }

    fun updateUndoRedoButtons() {
        val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
        if (drawView.canUndo()) {
            binding.undoButton.alpha = 1.0F
        } else {
            binding.undoButton.alpha = 0.25F
        }
        if (drawView.canRedo()) {
            binding.redoButton.alpha = 1.0F
        } else {
            binding.redoButton.alpha = 0.25F
        }
    }
}