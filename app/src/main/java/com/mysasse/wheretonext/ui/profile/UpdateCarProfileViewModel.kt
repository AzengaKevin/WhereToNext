package com.mysasse.wheretonext.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mysasse.wheretonext.data.CarListener
import com.mysasse.wheretonext.data.models.Car
import com.mysasse.wheretonext.data.repositories.CarsRepository
import java.lang.Exception

class UpdateCarProfileViewModel : ViewModel(), CarListener {

    private val _car = MutableLiveData<Car>()
    private val _exception = MutableLiveData<Exception>()
    private val _uri = MutableLiveData<Uri>()

    val mCar: LiveData<Car> get() = _car
    val mException: LiveData<Exception> get() = _exception
    val mUri: LiveData<Uri> get() = _uri

    private val carsRepository: CarsRepository = CarsRepository(this)

    fun setImageWithUri(uri: Uri) {
        carsRepository.uploadUri(uri)
    }

    fun setImageWithBitmap(image: ByteArray) {
        carsRepository.uploadBitmap(image)
    }

    fun addCar(car: Car) {
        carsRepository.add(car)
    }

    fun updateCar(car: Car) {
        carsRepository.update(car)
    }

    override fun onAdd(car: Car) {
        _car.value = car
    }

    override fun onRead(car: Car?) {
        _car.value = car
    }

    override fun onUpdate(car: Car) {

        _car.value = car
    }

    override fun onDelete() {

    }

    override fun onError(exception: Exception) {
        _exception.value = exception
    }

    override fun onImageUploaded(uri: Uri) {
        _uri.value = uri
    }


}
