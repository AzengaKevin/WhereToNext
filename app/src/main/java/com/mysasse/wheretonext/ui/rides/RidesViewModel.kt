package com.mysasse.wheretonext.ui.rides

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.RideListener
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.data.repositories.RideRepository
import java.lang.Exception

class RidesViewModel : ViewModel(), RideListener {

    private val _rides: MutableLiveData<List<Ride>> = MutableLiveData()
    private val _exception: MutableLiveData<Exception> = MutableLiveData()

    val mRides: LiveData<List<Ride>> get() = _rides
    val mException: LiveData<Exception?> get() = _exception

    private val rideRepository = RideRepository(this)

    fun browseRides() {
        rideRepository.getAll()
    }

    override fun onAdd(ride: Ride) {}

    override fun onUpdate(ride: Ride) {}

    override fun onRead(ride: Ride?) {}

    override fun onBrowse(rides: List<Ride>) {
        _rides.value = rides
    }

    override fun onDelete() {}

    override fun onError(exception: Exception?) {
        _exception.value = exception
    }
}
