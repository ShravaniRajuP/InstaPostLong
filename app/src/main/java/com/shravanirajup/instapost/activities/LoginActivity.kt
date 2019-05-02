package com.shravanirajup.instapost.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.shravanirajup.instapost.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")
        login_btn.isEnabled = true
        login_btn.setOnClickListener(this)
        register_account.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {

        when(view.id) {
            R.id.login_btn -> {
                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if (validate(email, password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        else{
                            Log.d(TAG, "Not logged in")
                        }
                    }
                }
                else{
                    showToast("Please enter email and password")
                }
            }
            R.id.register_account -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }

    }

    private fun validate(email: String, password:String) = email.isNotEmpty() && password.isNotEmpty()
}
