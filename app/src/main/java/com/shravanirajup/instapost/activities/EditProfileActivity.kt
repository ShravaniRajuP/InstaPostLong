package com.shravanirajup.instapost.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.shravanirajup.instapost.R
import com.shravanirajup.instapost.models.User
import com.shravanirajup.instapost.utils.CameraPictureTaker
import com.shravanirajup.instapost.utils.FirebaseHelper
import com.shravanirajup.instapost.utils.ValueEventListenerAdapter
import com.shravanirajup.instapost.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var cameraPictureTaker: CameraPictureTaker

    private val TAG = "EditProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        cameraPictureTaker = CameraPictureTaker(this)

        cancel_editing.setOnClickListener { finish() }
        done_editing.setOnClickListener { updateProfile() }
        change_profile_pic.setOnClickListener { cameraPictureTaker.takeCameraPicture() }

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.currentUserReference()
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser.name)
                username_input.setText(mUser.username)
                website_input.setText(mUser.website)
                bio_input.setText(mUser.bio)
                email_input.setText(mUser.email)
                phone_input.setText(mUser.phone?.toString())
                edit_profile_pic.loadUserPhoto(mUser.photo)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == cameraPictureTaker.REQUEST_CODE && resultCode == RESULT_OK) {
            // upload image to firebase storage
            mFirebaseHelper.uploadUserPhoto(cameraPictureTaker.imageUri!!) {
                val photoUrl = it.storage.downloadUrl.toString()
                mFirebaseHelper.updateUserPhoto(photoUrl) {
                    // save image to database user.photo
                    mUser = mUser.copy(photo = photoUrl)
                    edit_profile_pic.loadUserPhoto(mUser.photo)
                }
            }
        }
    }

    private fun updateProfile() {
        // Get user from inputs, Validate
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null){
            if (mPendingUser.email == mUser.email) {
                //update user
                updateUser(mPendingUser)
            }
            else {
                // password: Show dialog
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        }
        else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            website = website_input.text.toStringOrNull(),
            bio = bio_input.text.toStringOrNull(),
            email = email_input.text.toString(),
            phone = phone_input.text.toString().toLongOrNull()
        )
    }

    override fun onPasswordConfirm(password: String) {
        //reaunthenticate
        if(password.isNotEmpty()) {
            val credentials = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credentials) {
                mFirebaseHelper.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        }
        else{
            showToast("You must enter your password!")
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mFirebaseHelper.updateUser(updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun validate(user: User) =
        when {
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.email.isEmpty() -> "Please enter email"
            else -> null
        }
}