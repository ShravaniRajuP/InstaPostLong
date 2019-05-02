package com.shravanirajup.instapost.activities

import android.os.Bundle
import android.util.Log
import com.shravanirajup.instapost.R

class FavoritesActivity : BaseActivity(3) {
    private val TAG = "FavoritesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}
