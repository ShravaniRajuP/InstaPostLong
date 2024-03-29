package com.shravanirajup.instapost.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.shravanirajup.instapost.R
import kotlinx.android.synthetic.main.bottom_nav_view.*

abstract class BaseActivity(val navNumber: Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"
    fun setupBottomNavigation() {
        bottom_nav_view.setIconSize(29f, 29f)
        bottom_nav_view.setTextVisibility(false)
        bottom_nav_view.enableAnimation(false)
        for(i in 0 until bottom_nav_view.menu.size()){
            bottom_nav_view.setIconTintList(i, null)
        }
        bottom_nav_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                when(it.itemId){
                    R.id.nav_home -> HomeActivity::class.java
                    R.id.nav_search -> SearchActivity::class.java
                    R.id.nav_share -> ShareActivity::class.java
                    R.id.nav_favorites -> FavoritesActivity::class.java
                    R.id.nav_profile -> ProfileActivity::class.java
                    else -> {
                        Log.e(TAG, "unknown nav item clicked $it")
                        null
                    }
                }
            if(nextActivity != null){
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bottom_nav_view != null) {
            bottom_nav_view.menu.getItem(navNumber).isChecked = true
        }
    }
}