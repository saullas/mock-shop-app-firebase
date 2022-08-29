package com.example.shopappfirebase.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.shopappfirebase.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadPicture(imageURI: Uri, imageView: ImageView, cropType: String) {
        when (cropType) {
            "fitCenter" -> {
                try {
                    Glide
                        .with(context)
                        .load(imageURI)
                        .fitCenter()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(imageView)
                } catch (e: IOException) {

                }
            }
            "centerCrop" -> {
                try {
                    Glide
                        .with(context)
                        .load(imageURI)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(imageView)
                } catch (e: IOException) {

                }
            }
        }
    }

	fun loadProductPicture(imageURI: String, imageView: ImageView, cropType: String) {
        when (cropType) {
            "fitCenter" -> {
                try {
                    Glide
                        .with(context)
                        .load(imageURI)
                        .fitCenter()
                        .into(imageView)
                } catch (e: IOException) {

                }
            }
            "centerCrop" -> {
                try {
                    Glide
                        .with(context)
                        .load(imageURI)
                        .centerCrop()
                        .into(imageView)
                } catch (e: IOException) {

                }
            }
            "centerInside" -> {
                try {
                    Glide
                        .with(context)
                        .load(imageURI)
                        .centerInside()
                        .into(imageView)
                } catch (e: IOException) {

                }
            }
        }
	}
}