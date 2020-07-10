package com.mysasse.wheretonext.data.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class Booking(
    @DocumentId
    var id: String? = null,
    var rideId: String? = null,
    var paid: Boolean? = null,
    var userId: String? = null,
    var passenger: Int? = null
) : Serializable