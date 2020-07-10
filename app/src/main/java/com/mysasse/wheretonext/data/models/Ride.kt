package com.mysasse.wheretonext.data.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Ride(
    @DocumentId
    var id: String? = null,
    var userId: String? = null,
    var date: String? = null,
    var time: String? = null,
    var passengers: Int? = null,
    var pickup: Map<String, Any?>? = null,
    var dropOff: Map<String, Any?>? = null,
    var status: Int? = null,
    var cost: Double? = null
) : Serializable