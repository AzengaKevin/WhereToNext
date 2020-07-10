package com.mysasse.wheretonext.ui.rides

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.ProfileListener
import com.mysasse.wheretonext.data.models.Profile
import com.mysasse.wheretonext.data.repositories.ProfileRepository
import java.lang.Exception

class DetailsViewModel : ViewModel(), ProfileListener {

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    private val _exception: MutableLiveData<Exception> = MutableLiveData()

    val mProfile get() = _profile
    val mException get() = _exception

    private val profileRepository = ProfileRepository(this)

    fun getRidersProfile(uid: String) {
        profileRepository.getProfileByUid(uid)
    }

    override fun get(profile: Profile) {
        _profile.value = profile
    }

    override fun getAll(profiles: List<Profile>) {}

    override fun onError(exception: Exception) {
        _exception.value = exception
    }

    override fun onProfileUploaded(uri: Uri) {}
}
