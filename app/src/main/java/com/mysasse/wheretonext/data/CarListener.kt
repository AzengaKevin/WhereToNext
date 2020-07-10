package com.mysasse.wheretonext.data

import android.net.Uri
import com.mysasse.wheretonext.data.models.Car
import java.lang.Exception

interface CarListener {

    fun onAdd(car: Car)
    fun onRead(car: Car?)
    fun onUpdate(car: Car)
    fun onDelete()
    fun onError(exception: Exception)

    fun onImageUploaded(uri: Uri)
}