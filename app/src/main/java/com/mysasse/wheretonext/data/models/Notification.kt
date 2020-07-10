package com.mysasse.wheretonext.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    @DocumentId
    var id: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var content: String? = null,
    var rideId: String? = null,
    @ServerTimestamp
    var createdAt: Timestamp? = null
)