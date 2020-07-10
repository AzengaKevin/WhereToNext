package com.mysasse.wheretonext.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mysasse.wheretonext.data.NOTIFICATIONS_NODE
import com.mysasse.wheretonext.data.models.Notification

class NotificationsRepository {

    private val mDb = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    companion object NotificationsRepository {
        const val TAG = "NotificationsRepository"
    }

    fun getAll() {
        val userId: String = mAuth.currentUser?.uid!!
        mDb.collection(NOTIFICATIONS_NODE).whereEqualTo("userId", userId)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "User Notifications:", exception)
                    return@addSnapshotListener
                }

                val notifications = querySnapshot?.toObjects(Notification::class.java)
            }
    }
}