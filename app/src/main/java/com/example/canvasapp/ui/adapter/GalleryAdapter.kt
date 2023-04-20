package com.example.canvasapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.canvasapp.databinding.GalleryCardviewBinding
import com.example.canvasapp.model.Gallery_item

class GalleryAdapter(
    private val gallery: MutableList<Gallery_item>, private val onItemClick: (adapterPosition: Int) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GalleryCardviewBinding.inflate(layoutInflater, parent, false)
        return GalleryViewHolder(binding) { position ->
            onItemClick(position)
        }
    }

    override fun getItemCount() = gallery.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val galleryItem = gallery[position]
        holder.bind(galleryItem)
    }


    inner class GalleryViewHolder(
        private val binding: GalleryCardviewBinding,
        private val onItemClick: (adapterPosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {


        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }


        fun bind(galleryItem: Gallery_item) {
            Glide.with(binding.root).load(galleryItem.image).into(binding.galleryImage)
            binding.galleryName.text = galleryItem.name
            binding.galleryDate.text = galleryItem.editDate
        }
    }
}