package com.mysasse.wheretonext.data.models

import com.google.firebase.firestore.DocumentId

class Review(
    @DocumentId
    var id: String? = null,
    var rideId: String? = null,
    var bookingId: String? = null,
    var rating: Int? = null,
    var message: String? = null
)