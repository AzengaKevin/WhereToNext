package com.mysasse.wheretonext.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.CarListener
import com.mysasse.wheretonext.data.models.Car
import com.mysasse.wheretonext.data.repositories.CarsRepository
import java.lang.Exception

class CarProfileViewModel : ViewModel(), CarListener {

    private val _car = MutableLiveData<Car>()
    private val _exception = MutableLiveData<Exception>()

    val mCar: LiveData<Car> get() = _car
    val mException: LiveData<Exception> get() = _exception

    private val carsRepository: CarsRepository = CarsRepository(this)

    fun getCar() {
        carsRepository.read()
    }

    override fun onAdd(car: Car) {}

    override fun onRead(car: Car?) {
        car.let {
            _car.value = it
        }
    }

    override fun onUpdate(car: Car) {}

    override fun onDelete() {}

    override fun onError(exception: Exception) {}

    override fun onImageUploaded(uri: Uri) {}
}
