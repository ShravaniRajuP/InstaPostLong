package com.shravanirajup.instapost.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ServerValue
import com.shravanirajup.instapost.R
import com.shravanirajup.instapost.models.User
import com.shravanirajup.instapost.utils.CameraPictureTaker
import com.shravanirajup.instapost.utils.FirebaseHelper
import com.shravanirajup.instapost.utils.GlideApp
import com.shravanirajup.instapost.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_share.*
import java.util.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraPictureTaker
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")
        mFirebase = FirebaseHelper(this)

        mCamera = CameraPictureTaker(this)
        mCamera.takeCameraPicture()

        go_back.setOnClickListener{ finish() }
        Log.d(TAG, "not entering share")
        share_text.setOnClickListener{ share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (imageUri != null) {
            val uid = mFirebase.auth.currentUser!!.uid
            mFirebase.storage.child("users").child(uid).child("images")
                .child(imageUri.lastPathSegment!!).putFile(imageUri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val imageDownloadUrl = it.result.storage.downloadUrl.toString()
                        mFirebase.database.child("images").child(uid).push()
                            .setValue(imageDownloadUrl)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    mFirebase.database.child("feed-posts").child(uid)
                                        .push()
                                        .setValue(mkFeedPost(uid, imageDownloadUrl))
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                startActivity(Intent(this,
                                                    ProfileActivity::class.java))
                                                finish()
                                            }
                                        }
                                } else {
                                    showToast(it.exception!!.message!!)
                                }
                            }
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }
    }

    private fun mkFeedPost(uid: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
            uid = uid,
            username = mUser.username,
            image = imageDownloadUrl,
            caption = caption_input.text.toString(),
            photo = mUser.photo
        )
    }
}

data class FeedPost(val uid: String = "", val username: String = "",
                    val image: String = "", val likesCount: Int = 0, val commentsCount: Int = 0,
                    val caption: String = "", val comments: List<Comment> = emptyList(),
                    val timestamp: Any = ServerValue.TIMESTAMP, val photo: String? = null) {
    fun timestampDate(): Date = Date(timestamp as Long)
}

data class Comment(val uid: String, val username: String, val text: String)