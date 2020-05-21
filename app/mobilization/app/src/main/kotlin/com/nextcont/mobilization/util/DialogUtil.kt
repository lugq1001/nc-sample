package com.nextcont.mobilization.util

import android.app.AlertDialog
import android.content.Context
import com.nextcont.mobilization.R

object DialogUtil {

    fun showAlert(context: Context, message: String?, action: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.app_tips))
            .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                context.getString(R.string.app_confirm)
            ) { dialog, _ ->
                dialog.dismiss()
                action?.let {
                    it()
                }
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .show()

    }

}