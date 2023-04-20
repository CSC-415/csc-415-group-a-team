package com.example.canvasapp.viewModel

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.canvasapp.model.Gallery_item
import java.io.File
import java.util.*


class GalleryItemViewModel : ViewModel() {

    val gallery = mutableListOf<Gallery_item>()

    init {
        collectData()
    }

    fun fillData() = gallery.toList()

    fun refreshData(): List<Gallery_item> {
        gallery.clear()
        collectData()
        return gallery.toList()
    }


    fun fetchById(id: Int) = gallery.first { it.id == id }

    private fun createGalleryItem(image: String, name: String, date: String, id: Int) =
        Gallery_item(
            name = name,
            image = image,
            editDate = date,
            id = id
        )

    private fun formatDate(date: Date): String {
        val input = SimpleDateFormat("MM/dd/yyyy")
        val formatted = input.format(date)
        return formatted.toString()
    }

    fun collectData() {
        var i = 0
        File("/data/data/com.example.canvasapp/app_Images").walkTopDown().forEach {
            if (i == 0) {
                i++
            } else {
                gallery.add(
                    createGalleryItem(
                        it.absolutePath,
                        //"https://th.bing.com/th/id/OIP.GAvKZNNy_8tdt9sZgZwQjQHaJQ?w=186&h=233&c=7&r=0&o=5&dpr=1.7&pid=1.7",
                        it.name,
                        formatDate(Date(it.lastModified())),
                        i
                    )
                )
                i++
            }
        }
    }


}