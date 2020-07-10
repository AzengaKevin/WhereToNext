package com.mysasse.wheretonext.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Car(
    @get:Exclude
    @DocumentId
    var id: String? = null,
    var model: String? = null,
    var plateNumber: String? = null,
    var image: String? = null
) : Serializable