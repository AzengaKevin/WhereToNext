package com.mysasse.wheretonext.ui.rides

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.RideListener
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.data.repositories.RideRepository
import java.lang.Exception

class PublishRideViewModel : ViewModel(), RideListener {

    private val _ride: MutableLiveData<Ride> = MutableLiveData()
    private val _exception: MutableLiveData<Exception?> = MutableLiveData()
    private val _rides: MutableLiveData<List<Ride>> = MutableLiveData()

    val mRide: LiveData<Ride> get() = _ride
    val mException: LiveData<Exception?> get() = _exception
    val mRides: LiveData<List<Ride>> get() = _rides

    private val rideRepository: RideRepository = RideRepository(this)

    fun addRide(ride: Ride) {
        rideRepository.add(ride)
    }


    override fun onAdd(ride: Ride) {
        _ride.value = ride
    }

    override fun onUpdate(ride: Ride) {
        _ride.value = ride
    }

    override fun onRead(ride: Ride?) {
        _ride.value = ride
    }

    override fun onBrowse(rides: List<Ride>) {
        _rides.value = rides
    }

    override fun onDelete() {
    }

    override fun onError(exception: Exception?) {
        _exception.value = exception
    }


}
