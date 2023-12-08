package com.example.myapplication.ui.gallery

import android.app.AlertDialog
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.FragmentGalleryBinding
import java.io.File

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageFiles = getAllShownImagesPath().toMutableList()
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(context, 3)

        binding.imagesRecyclerView.adapter = ImageAdapter(imageFiles, object : ImageAdapter.OnImageClickListener {
            override fun onImageClick(file: File) {
                showDeleteConfirmationDialog(file)
            }
        })

        return root
    }

    private fun getAllShownImagesPath(): List<File> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Images.Media.RELATIVE_PATH + "=?"
        val selectionArgs = arrayOf("Pictures/Caro-image/")

        val cursor = requireContext().contentResolver.query(uri, null, selection, selectionArgs, null)
        val columnIndexData = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val listOfAllImages = mutableListOf<File>()

        while (cursor?.moveToNext() == true) {
            columnIndexData?.let {
                val imagePath = cursor.getString(it)
                listOfAllImages.add(File(imagePath))
            }
        }
        cursor?.close()
        return listOfAllImages
    }

    private fun showDeleteConfirmationDialog(file: File) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteImage(file)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteImage(file: File) {
        val contentResolver = requireContext().contentResolver
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Images.Media.DATA + "=?"
        val selectionArgs = arrayOf(file.absolutePath)

        contentResolver.delete(uri, selection, selectionArgs)

        // Mettre à jour la liste des images et l'adapter
        val updatedImages = getAllShownImagesPath()
        (binding.imagesRecyclerView.adapter as ImageAdapter).updateImages(updatedImages)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
