package com.example.anime.ui.search

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anime.R
import com.example.anime.databinding.FragmentSearchBinding
import com.example.anime.service.SearchAnime
import java.io.ByteArrayOutputStream

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private var searchAdapter: SearchListAdapter? = null

    // Delay creation of the viewModel until an appropriate lifecycle method
    private val viewModel: SearchViewModel by lazy {
        val activity = requireNotNull(this.activity){
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this,
            SearchViewModel.Factory(activity.application))[SearchViewModel::class.java]
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        // Set the lifecycleOwner so dataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        searchAdapter = SearchListAdapter()

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            selectImage()
        }

        binding.fab.setOnClickListener {
            selectImage()
        }

        binding.tvClick.setOnClickListener {
            selectImage()
        }

        viewModel.searchAnimeListLiveData.observe(viewLifecycleOwner) { responseString ->
            binding.spinner.visibility = View.GONE

            // Extract the result portion of the response
            val resultIndex = responseString.indexOf("result=[") + "result=[".length
            val resultString = responseString.substring(resultIndex, responseString.length - 1)

            // Split the result string into individual SearchAnime strings
            val searchAnimeStrings = resultString.split("), ")

            // Parse each SearchAnime string and create a list of SearchAnime objects
            val searchAnimeList = searchAnimeStrings.map { animeString ->
                val filenameStartIndex = animeString.indexOf("filename=") + "filename=".length
                val episodeStartIndex = animeString.indexOf("episode=") + "episode=".length

                val filename = animeString.substring(filenameStartIndex, episodeStartIndex - ", episode=".length)
                val episodeString = animeString.substring(episodeStartIndex)

                //val episode = if (episodeString == "null") null else episodeString.toInt()

                val episode = try {
                    episodeString.toInt()
                } catch (e: NumberFormatException) {
                    null
                }

                SearchAnime(filename, episode.toString())
            }

            if (responseString == null) {
                Log.d("##SEARCH-Fragment", "SearchAnimeList is empty")
            } else {
                binding.spinner.visibility = View.GONE
                searchAdapter?.setData(searchAnimeList)
            }
        }

    }

    private fun selectImage() {
        val items = arrayOf("Use Camera", "Open Gallery", "Cancel")

        AlertDialog.Builder(activity)
            .setTitle("")
            .setItems(items) { dialog, item ->
                when (items[item]) {
                    "Use Camera" -> {
                        // open camera
                        launchCamera()
                    }
                    "Open Gallery" -> {
                        // open gallery
                        launchGallery()
                    }
                    "Cancel" -> dialog.dismiss()
                }
            }.show()
    }

    private fun launchCamera() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        // Launch the camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun launchGallery() {
        // Check for read external storage permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            )
            return
        }

        // Launch the gallery
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.spinner.visibility = View.VISIBLE
            // Get the image from the camera
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)

            // Convert the image to a byte array
            val bitmap = binding.imageView.drawable.toBitmap()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Send image data
            viewModel.uploadImage(byteArray)

        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
            binding.spinner.visibility = View.VISIBLE
            // Get the image from the gallery
            val selectedImage = data?.data
            val uri = Uri.parse(selectedImage.toString())
            val imageBitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                uri
            )
            binding.imageView.setImageBitmap(imageBitmap)

            // Convert the image to a byte array
            val bitmap = binding.imageView.drawable.toBitmap()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Send image data
            viewModel.uploadImage(byteArray)
        }
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1
        const val REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2
        const val REQUEST_IMAGE_CAPTURE = 3
        const val REQUEST_GALLERY_IMAGE = 4
        const val IMAGE_QUALITY = 100
    }

    override fun onStart() {
        super.onStart()
        binding.spinner.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        binding.spinner.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.spinner.visibility = View.GONE
    }
}

