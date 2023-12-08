package com.example.myapplication.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import java.io.File

class ImageAdapter(private val images:  MutableList<File>, private val listener: OnImageClickListener) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(file: File)
    }

    fun updateImages(newImages: List<File>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val file = images[position]
        Glide.with(holder.imageView.context).load(file).into(holder.imageView)
        holder.itemView.setOnClickListener {
            listener.onImageClick(file)
        }

    }

    override fun getItemCount() = images.size
}
