package com.shravanirajup.instapost.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.shravanirajup.instapost.R
import kotlinx.android.synthetic.main.password_dialog.view.*

class PasswordDialog : DialogFragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.password_dialog, null)
        return AlertDialog.Builder(context!!)
            .setView(view)
            .setPositiveButton(android.R.string.ok, {_, _ ->
                // Send password to activity
                mListener.onPasswordConfirm(view.password_input.text.toString())
            })
            .setNegativeButton(android.R.string.cancel, {_, _ ->
                // do nothing
            })
            .setTitle(R.string.password_entry)
            .create()
    }
}