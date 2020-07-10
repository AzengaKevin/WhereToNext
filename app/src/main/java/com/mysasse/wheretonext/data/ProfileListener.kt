package com.mysasse.wheretonext.data

import android.net.Uri
import com.mysasse.wheretonext.data.models.Profile
import java.lang.Exception

interface ProfileListener {

    fun get(profile: Profile)

    fun getAll(profiles: List<Profile>)

    fun onError(exception: Exception)

    fun onProfileUploaded(uri: Uri)

}