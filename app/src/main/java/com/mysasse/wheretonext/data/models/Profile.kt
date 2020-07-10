package com.mysasse.wheretonext.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Profile(
    @get:Exclude
    @DocumentId
    var id: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var location: String? = null,
    var profilePic: String? = null,
    var bio: String? = null
) : Serializable