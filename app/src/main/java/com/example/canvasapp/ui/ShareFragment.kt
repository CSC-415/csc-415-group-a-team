package com.example.canvasapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.canvasapp.databinding.ShareViewBinding
import java.io.File

class ShareFragment: DialogFragment() {

    private var _binding: ShareViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ShareViewBinding.inflate(inflater, container, false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (arguments != null) {
            val imagePath = requireArguments().getString("image")
            Log.d("share", imagePath!!)

            val startActivityForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        if (it.data != null && it.data!!.data != null) {
                            val bmp = BitmapFactory.decodeFile(imagePath)
                            val uri: Uri = it.data!!.data!!
                            val op = requireActivity().contentResolver.openOutputStream(uri)
                            bmp?.compress(Bitmap.CompressFormat.PNG, 100, op)
                        }
                    }
                }


            //save to phone gallery
            binding.SaveToPhone.setOnClickListener {
                val imageName = File(imagePath).name
                createFile(imageName, startActivityForResult)
                createToast("Image Saved!")
            }

            //share to insta
            binding.instagram.setOnClickListener{
                val type = "image/*"
                val imageName =  imagePath
                Log.d("share", imageName)
                createInstagramIntent(type, imageName)
            }




        }

    }

    fun createToast(text: String) {
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun createFile(fileName: String, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // file type
        intent.type = "image/png"
        // file name
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        launcher.launch(intent)
    }

    private fun createInstagramIntent(type: String, mediaPath: String) {

        // Create the new Intent using the 'Send' action.
        val share = Intent(Intent.ACTION_SEND)

        // Set the MIME type
        share.type = type

        // Create the URI from the media
        val media = File(mediaPath)
        val uri = Uri.fromFile(media)

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, mediaPath)

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"))
    }


    companion object {
        private const val Bundle_ID = "image"

        fun newInstance(image: String) = ShareFragment().apply {
            arguments = bundleOf(Bundle_ID to image)
        }
    }


}