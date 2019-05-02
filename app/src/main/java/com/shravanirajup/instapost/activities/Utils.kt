package com.shravanirajup.instapost.activities

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.widget.ImageView
import android.widget.Toast
import com.shravanirajup.instapost.R
import com.shravanirajup.instapost.utils.GlideApp

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, text, duration).show()
}

fun ImageView.loadUserPhoto(photoUrl: String?) {
    if(!(context as Activity).isDestroyed) {
        GlideApp.with(this).load(photoUrl).fallback(R.drawable.register_person).into(this)
    }
}

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

fun ImageView.loadImage(image: String?) {
    GlideApp.with(this).load(image).centerCrop().into(this)
}
