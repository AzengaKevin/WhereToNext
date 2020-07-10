package com.mysasse.wheretonext.data

import com.mysasse.wheretonext.data.models.Ride
import java.lang.Exception

interface RideListener {

    fun onAdd(ride: Ride)

    fun onUpdate(ride: Ride)

    fun onRead(ride: Ride?)

    fun onBrowse(rides: List<Ride>)

    fun onDelete()

    fun onError(exception: Exception?)
}