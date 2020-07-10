package com.mysasse.wheretonext.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.ProfileListener
import com.mysasse.wheretonext.data.models.Profile
import com.mysasse.wheretonext.data.repositories.ProfileRepository
import kotlin.Exception

class UpdateProfileViewModel : ViewModel(), ProfileListener {

    //Repository Instance
    private val profileRepository: ProfileRepository = ProfileRepository(this)

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    private val _exception: MutableLiveData<Exception> = MutableLiveData()
    private val _uri: MutableLiveData<Uri> = MutableLiveData()

    val mProfile: LiveData<Profile> get() = _profile
    val mException: LiveData<Exception> get() = _exception
    val mUri: LiveData<Uri> get() = _uri

    fun updateProfile(profile: Profile) {
        profileRepository.updateProfile(profile)
    }

    fun uploadUserAvatar(image: ByteArray) {
        profileRepository.uploadPicture(image)
    }

    fun uploadProfileUri(profileUri: Uri) {
        profileRepository.uploadProfileUri(profileUri)
    }

    override fun get(profile: Profile) {

        _profile.value = profile
    }

    override fun getAll(profiles: List<Profile>) {}

    override fun onError(exception: Exception) {
        _exception.value = exception
    }

    override fun onProfileUploaded(uri: Uri) {
        _uri.value = uri
    }

}
