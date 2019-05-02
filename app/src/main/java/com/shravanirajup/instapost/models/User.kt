package com.shravanirajup.instapost.models

import com.google.firebase.database.Exclude

data class User(val name: String = "",
                val username: String = "",
                val email: String = "",
                val website: String? = null,
                val bio: String? = null,
                val phone: Long? = null,
                val photo: String? = "",
                @Exclude val uid: String? = null) {
}