package com.shravanirajup.instapost.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shravanirajup.instapost.R
import com.shravanirajup.instapost.models.User
import kotlinx.android.synthetic.main.fragment_email_register.*
import kotlinx.android.synthetic.main.fragment_name_password_register.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {
    private val TAG = "RegisterActivity"
    private var mEmail: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()){
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email) {signInMethods ->
                if (signInMethods.isEmpty()) {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                        .addToBackStack(null)
                        .commit()
                }
                else {
                    showToast("This email already exists!")
                }
            }
        }
        else {
            showToast("Please enter email")
        }
    }

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email:String,
                                                        onSuccess: (List<String>) -> Unit) {
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result.signInMethods ?: emptyList<String>())
            }
            else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    override fun onRegister(fullname: String, password: String) {
        if(fullname.isNotEmpty() && password.isNotEmpty()) {
            val email = mEmail
            if(email != null) {
                mAuth.createUserWithEmailAndPassword(email, password){
                    mDatabase.createUser(it.user.uid, makeUser(fullname, email)){
                            startHomeActivity()
                        }
                    }
            }
            else{
                Log.d(TAG, "onRegister: email is null")
                showToast("Please enter email")
                supportFragmentManager.popBackStack()
            }
        }
        else {
            showToast("Please enter full name and password")
        }
    }

    private fun unknownRegisterError(it: Task<*>) {
        Log.d(TAG, "Failed to create user profile", it.exception)
        showToast("Technical difficulties...Try again later...")
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun makeUser(fullname: String, email: String): User {
        val username = makeUsername(fullname)
        return User(name = fullname, username = username, email = email)
    }

    private fun makeUsername(fullname: String): String =
        fullname.toLowerCase().replace(" ", ".")

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {
        val reference = child("users").child(uid)
        reference.setValue(user)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    onSuccess()
                }
                else {
                    unknownRegisterError(it)
                }
            }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String,
                                                            onSuccess: (AuthResult) -> Unit) {
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.result)
                }
                else {
                    unknownRegisterError(it)
                }
            }
    }
}

class EmailFragment: Fragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_email_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register_email.setOnClickListener {
            val email = email_input.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment: Fragment() {
    private lateinit var mListener: Listener

    interface Listener{
        fun onRegister(fullname: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_name_password_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register_namepass.setOnClickListener {
            val fullname = name_input.text.toString()
            val password = password_input.toString()
            mListener.onRegister(fullname, password)
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as NamePassFragment.Listener
    }
}