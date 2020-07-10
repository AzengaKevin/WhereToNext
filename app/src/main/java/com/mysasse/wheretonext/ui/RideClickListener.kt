package com.mysasse.wheretonext.ui

import android.view.View
import com.mysasse.wheretonext.data.models.Ride

interface RideClickListener {

    fun onRideClicked(ride: Ride, view: View)
}