package com.example.canvasapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import com.example.canvasapp.R
import com.example.canvasapp.databinding.NameTextBinding
import java.io.File
import java.io.FileOutputStream


class NameDialogFragment : DialogFragment() {

    private var _binding: NameTextBinding? = null
    private val binding get() = _binding!!
    private var name: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NameTextBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("fragmentListener", arguments.toString())
        if (arguments != null){
            Log.d("fragmentListener", "went here")
            val b = requireArguments().getByteArray("data")
            binding.submitButton.setOnClickListener {
                name = binding.nameTextField.text.toString()
                Log.d("name", name!!)
                val bmp = b?.let { it1 -> BitmapFactory.decodeByteArray(b, 0, it1.size) }
                if (name != null) {
                    Log.d("name", "not mull")
                    var file = context?.getDir("Images", Context.MODE_PRIVATE)
                    Log.d("Save", name!!)
                    file = File(file, name)
                    val out = FileOutputStream(file)
                    bmp?.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                    out.close()
                    Log.d("Save", "image save")
                    val text = "Image saved"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                } else {
                    val text = "Bad Name"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                    Log.d("image", "bad name")
                }
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(false)
                    replace(
                        R.id.fragment_container_view, CanvasMainFragment()
                    )
                }
            }
        }

    }



    companion object {
        private const val Bundle_ID = "data"

        fun newInstnce(b: ByteArray) = NameDialogFragment().apply {
            arguments = bundleOf(Bundle_ID to b)
        }

    }
}


