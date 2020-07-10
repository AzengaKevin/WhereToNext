package com.mysasse.wheretonext.utils

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.mysasse.wheretonext.R

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(R.string.close_text, null).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ProgressBar.show() {
    this.visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    this.visibility = View.INVISIBLE
}