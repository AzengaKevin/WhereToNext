package com.mysasse.wheretonext.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mysasse.wheretonext.data.CARS_NODE
import com.mysasse.wheretonext.data.RIDES_NODE
import com.mysasse.wheretonext.data.RideListener
import com.mysasse.wheretonext.data.models.Ride

class RideRepository(private val listener: RideListener) {

    private val mDb = FirebaseFirestore.getInstance()

    fun add(ride: Ride) {
        mDb.collection(RIDES_NODE).add(ride)
            .addOnCompleteListener { addRideTask ->
                if (addRideTask.isSuccessful) {
                    listener.onUpdate(ride)
                    Log.d(TAG, "Ride added successfully}")
                } else {
                    listener.onError(addRideTask.exception)
                    Log.e(TAG, "Error: Adding a ride", addRideTask.exception)
                }
            }
    }

    fun getAll() {
        mDb.collection(RIDES_NODE)
            .addSnapshotListener { querySnapshot, exception ->

                if (exception != null) {
                    Log.e(TAG, "Error getting rides", exception)
                    listener.onError(exception)
                    return@addSnapshotListener
                }

                val rides = querySnapshot?.toObjects(Ride::class.java)
                listener.onBrowse(rides!!)
            }
    }

    fun read(id: String) {
        mDb.collection(CARS_NODE).document(id)
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.e(TAG, "read ride", e)
                    return@addSnapshotListener
                }

                val ride = snapshot?.toObject(Ride::class.java)
                listener.onRead(ride)
            }
    }

    fun update(ride: Ride) {

        ride.id.let { id ->

            mDb.collection(RIDES_NODE)
                .document(id!!)
                .set(ride)
                .addOnCompleteListener { updateRideTask ->
                    if (updateRideTask.isSuccessful) {
                        listener.onUpdate(ride)
                        Log.d(TAG, "Ride: $id is updated")
                    } else {
                        listener.onError(updateRideTask.exception)
                        Log.e(TAG, "Ride $id updating failed", updateRideTask.exception)
                    }
                }

        }
    }

    fun delete(ride: Ride) {

        ride.id.let { id ->

            mDb.collection(RIDES_NODE)
                .document(id!!)
                .delete()
                .addOnCompleteListener { deleteRideTask ->
                    if (deleteRideTask.isSuccessful) {
                        listener.onDelete()
                        Log.d(TAG, "Ride: $id is deleted successfully")

                    } else {
                        listener.onError(deleteRideTask.exception)
                        Log.e(TAG, "Ride $id deletion failed", deleteRideTask.exception)
                    }

                }
        }
    }

    companion object RideRepository {
        const val TAG = "RideRepository"
    }
}