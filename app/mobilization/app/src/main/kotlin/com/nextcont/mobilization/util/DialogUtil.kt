package com.nextcont.mobilization.util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import com.nextcont.mobilization.R
import java.util.*


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

    fun showConfirm(
        context: Context,
        message: String?,
        confirm: String,
        action: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.app_tips))
            .setMessage(message)
            .setPositiveButton(confirm) { dialog, _ ->
                dialog.dismiss()
                action?.let {
                    it()
                }
            }
            .setNegativeButton(R.string.app_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    fun showChoice(context: Context, items: List<String>, action: ((index: Int) -> Unit)) {
        val adb = AlertDialog.Builder(context)
        adb.setSingleChoiceItems(items.toTypedArray(), 0) { dialog, which ->
            dialog.dismiss()
            action(which)
        }
        adb.setNegativeButton(R.string.app_cancel, null)
        adb.setTitle(R.string.app_choose)
        adb.show()
    }

    fun showDatePicker(context: Context, calendar: Calendar, action: ((calendar: Calendar) -> Unit)) {
        val datePickerDialog = DatePickerDialog(
            context,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, monthOfYear, dayOfMonth)
                action(c)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

}