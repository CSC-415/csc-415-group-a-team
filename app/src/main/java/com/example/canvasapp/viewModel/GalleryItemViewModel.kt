package com.example.canvasapp.viewModel

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import com.example.canvasapp.model.Gallery_item
import java.util.*

class GalleryItemViewModel : ViewModel() {

    val gallery = mutableListOf<Gallery_item>()

    init {
        for (i in 1..10) {
            gallery.add(
                createGalleryItem(
                    "https://th.bing.com/th/id/OIP.GAvKZNNy_8tdt9sZgZwQjQHaJQ?w=186&h=233&c=7&r=0&o=5&dpr=1.7&pid=1.7",
                    "Item $i",
                    "A cool stick dude",
                    i
                )
            )
        }
    }

    fun fillData() = gallery.toList()

    fun fetchById(id: Int) = gallery.first { it.id == id }

    private fun createGalleryItem(image: String, name: String, description: String, id: Int) =
        Gallery_item(
            name = name,
            image = image,
            description = description,
            editDate = formatDate(Date()),
            id = id
        )

    private fun formatDate(date: Date): String {
        val input = SimpleDateFormat("dd/mm/yyyy")
        val formatted = input.format(date)
        return formatted.toString()
    }


}