package com.example.canvasapp.ui

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
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
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

    //temp image urls for demonstration
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
        Glide.with(this).load(undo).into(binding.undoButton)
        Glide.with(this).load(redo).into(binding.redoButton)


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
        binding.canvasMainEraserTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.ERASER
            buttonBackgroundReset()
        }

        //Brush button (sets to last color)
        binding.canvasMainBrushTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.BRUSH
            buttonBackgroundReset()
        }

        //Color wheel
        binding.canvasMainColorTool.setOnClickListener {
            openColorPickerDialogue()
            buttonBackgroundReset()
        }

        //Color picker
        binding.canvasMainPickerTool.setOnClickListener {
            DrawView.currentTool = DrawView.Companion.Tool.PICKER
            buttonBackgroundReset()
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
            Log.d("main frag", bundle.toString())
            val fragment = NameDialogFragment()
            fragment.arguments = bundle
            fragment.show(childFragmentManager, "tag")
        }

        binding.undoButton.setOnClickListener {
            val drawView = binding.canvasGalleryImage.findViewById<DrawView>(R.id.draw_view)
            drawView.undo()
            updateUndoRedoButtons()
        }

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
        val blendColor = ColorUtils.blendARGB(Color.parseColor("#404040"), DrawView.currentBrush,0.5F)
        binding.canvasMainBrushTool.setBackgroundColor(blendColor)
        binding.canvasMainColorTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))
        binding.canvasMainEraserTool.setBackgroundColor(Color.parseColor(buttonDefaultBackground))

        when (DrawView.currentTool) {
            DrawView.Companion.Tool.BRUSH ->
                binding.canvasMainBrushTool.setBackgroundColor(DrawView.currentBrush)
            DrawView.Companion.Tool.ERASER ->
                binding.canvasMainEraserTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.Companion.Tool.PICKER ->
                binding.canvasMainPickerTool.setBackgroundColor(Color.parseColor(buttonClickedBackground))
            DrawView.Companion.Tool.FILLCAN ->
                TODO()
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