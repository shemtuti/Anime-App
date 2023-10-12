package com.example.anime.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.anime.R
import timber.log.Timber

// Binding adapter used to hide the spinner once data is available
@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

// Binding adapter used to hide textview with info
@BindingAdapter("showInfo")
fun showInfo(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

// Binding adapter used to display images from URL using Glide
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Glide
        .with(imageView.context)
        .load(url)
        .apply (
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
        ).into(imageView)

}
