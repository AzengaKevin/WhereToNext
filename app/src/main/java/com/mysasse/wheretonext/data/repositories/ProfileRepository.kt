package com.mysasse.wheretonext.data.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mysasse.wheretonext.data.PROFILES_NODE
import com.mysasse.wheretonext.data.PROFILE_PICTURES_BUCKET
import com.mysasse.wheretonext.data.ProfileListener
import com.mysasse.wheretonext.data.models.Profile

class ProfileRepository(private val listener: ProfileListener) {

    private val mDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mFirebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getSingleProfile() {
        mDb.collection(PROFILES_NODE).document(mAuth.currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "User Profile: ", exception)
                    listener.onError(exception)
                    return@addSnapshotListener
                }

                snapshot.let { profileSnapshot ->
                    val profile = profileSnapshot!!.toObject(Profile::class.java)

                    profile.let { listener.get(it!!) }

                }
            }
    }

    fun getProfileByUid(uid: String) {

        mDb.collection(PROFILES_NODE).document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "User Profile: ", exception)
                    listener.onError(exception)
                    return@addSnapshotListener
                }

                val profile = snapshot!!.toObject(Profile::class.java)

                listener.get(profile!!)
            }

    }


    fun updateProfile(profile: Profile) {

        mDb.collection(PROFILES_NODE).document(mAuth.currentUser!!.uid)
            .set(profile)
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    Log.d(TAG, "Profile Updated")
                } else {

                    updateTask.exception.let { listener.onError(it!!) }

                    Log.e(TAG, "Error updating profile:", updateTask.exception)
                }
            }
    }

    fun uploadPicture(image: ByteArray) {

        val profilePicRef: StorageReference =
            mFirebaseStorage.getReference("$PROFILE_PICTURES_BUCKET/${mAuth.currentUser?.uid}")

        val uploadTask = profilePicRef.putBytes(image)

        uploadTask.addOnCompleteListener { mainTask ->

            if (mainTask.isSuccessful) {
                updateProfilePictureUri(profilePicRef)
            } else {
                mainTask.exception?.let {
                    listener.onError(it)
                }
            }
        }
    }

    private fun updateProfilePictureUri(profilePicRef: StorageReference) {

        profilePicRef.downloadUrl.addOnCompleteListener { urlTask ->

            if (urlTask.isSuccessful) {

                urlTask.result?.let { uri ->
                    mAuth.currentUser?.let { user ->
                        val imageUrlMap = mapOf("profilePic" to uri.toString())
                        mDb.collection(PROFILES_NODE).document(user.uid).update(imageUrlMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    listener.onProfileUploaded(uri)
                                    Log.d(TAG, "Profile picture updated")

                                } else {
                                    listener.onError(task.exception!!)
                                }
                            }
                    }
                }

            } else {
                urlTask.exception?.let {
                    listener.onError(it)
                }
            }
        }
    }

    fun uploadProfileUri(imageUri: Uri) {

        val profilePicRef: StorageReference =
            mFirebaseStorage.getReference("$PROFILE_PICTURES_BUCKET/${mAuth.currentUser?.uid}")

        val uploadTask = profilePicRef.putFile(imageUri)


        uploadTask.addOnCompleteListener { mainTask ->

            if (mainTask.isSuccessful) {

                updateProfilePictureUri(profilePicRef)

            } else {

                mainTask.exception?.let {
                    listener.onError(it)
                }
            }
        }


    }


    companion object UserRepository {
        const val TAG = "UserRepository"
    }

}