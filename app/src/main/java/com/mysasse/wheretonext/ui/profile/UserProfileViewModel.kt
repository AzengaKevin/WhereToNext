package com.mysasse.wheretonext.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.ProfileListener
import com.mysasse.wheretonext.data.models.Profile
import com.mysasse.wheretonext.data.repositories.ProfileRepository
import java.lang.Exception

class UserProfileViewModel : ViewModel(), ProfileListener {

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    private val _exception: MutableLiveData<Exception> = MutableLiveData()

    val mProfile: LiveData<Profile> get() = _profile
    val mException: LiveData<Exception> get() = _exception

    private var profileRepository: ProfileRepository = ProfileRepository(this)

    fun getProfileFromRepository() {
        profileRepository.getSingleProfile()
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
