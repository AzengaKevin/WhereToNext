package com.mysasse.wheretonext.data.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mysasse.wheretonext.data.CARS_NODE
import com.mysasse.wheretonext.data.CARS_PICTURE_BUCKET
import com.mysasse.wheretonext.data.CarListener
import com.mysasse.wheretonext.data.models.Car

class CarsRepository(private val listener: CarListener) {

    companion object CarsRepository {
        const val TAG = "CarsRepository"
    }

    private val mDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mRef: FirebaseStorage = FirebaseStorage.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun add(car: Car) {
        mDb.collection(CARS_NODE).document(mAuth.currentUser!!.uid)
            .set(car)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Car successfully added")
                    listener.onAdd(car)
                } else {
                    Log.e(TAG, "Adding car profile: ", task.exception)
                    listener.onError(task.exception!!)
                }
            }
    }

    fun uploadBitmap(bytes: ByteArray) {

        val carImageRef: StorageReference =
            mRef.getReference("$CARS_PICTURE_BUCKET/${mAuth.currentUser!!.uid}")

        val uploadTask = carImageRef.putBytes(bytes)

        uploadTask.addOnCompleteListener { uploadImageTask ->
            if (uploadImageTask.isSuccessful) {

                getDownloadUrl(carImageRef)

            } else {
                listener.onError(uploadImageTask.exception!!)
            }
        }

    }

    fun uploadUri(uri: Uri) {

        val carImageRef: StorageReference =
            mRef.getReference("$CARS_PICTURE_BUCKET/${mAuth.currentUser!!.uid}")

        val uploadTask = carImageRef.putFile(uri)

        uploadTask.addOnCompleteListener { uploadImageTask ->

            if (uploadImageTask.isSuccessful) {
                getDownloadUrl(carImageRef)
            } else {
                Log.e(TAG, "Uploading Uri", uploadImageTask.exception)
                listener.onError(uploadImageTask.exception!!)
            }

        }
    }

    private fun getDownloadUrl(carImageRef: StorageReference) {

        carImageRef.downloadUrl.addOnCompleteListener { uriTask ->

            if (uriTask.isSuccessful) {

                val imageUrlMap = mapOf("image" to uriTask.result.toString())

                mDb.collection(CARS_NODE).document(mAuth.currentUser!!.uid)
                    .update(imageUrlMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            listener.onImageUploaded(uriTask.result!!)
                        } else {
                            listener.onError(task.exception!!)
                        }
                    }

            } else {
                Log.e(TAG, "Get Image Uri", uriTask.exception)
                listener.onError(uriTask.exception!!)

            }
        }

    }


    fun read() {
        mDb.collection(CARS_NODE).document(mAuth.currentUser!!.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Getting riders car", e)
                    return@addSnapshotListener
                }

                val car = snapshot?.toObject(Car::class.java)
                listener.onRead(car)
            }
    }

    fun update(car: Car) {
        mDb.collection(CARS_NODE).document(mAuth.currentUser!!.uid)
            .set(car)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Car successfully updated")
                } else {
                    Log.e(TAG, "update riders car profile: ", task.exception)
                }
            }

    }

}