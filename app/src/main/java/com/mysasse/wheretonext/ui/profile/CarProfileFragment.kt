package com.mysasse.wheretonext.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.utils.toast

class CarProfileFragment : Fragment() {

    private lateinit var imageIv: ImageView
    private lateinit var modelTv: TextView
    private lateinit var plateNumberTv: TextView
    private lateinit var editCarProfileButton: Button

    private lateinit var viewModel: CarProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.car_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageIv = view.findViewById(R.id.image_iv)
        modelTv = view.findViewById(R.id.model_tv)
        plateNumberTv = view.findViewById(R.id.plate_number_tv)
        editCarProfileButton = view.findViewById(R.id.edit_car_profile_button)

        editCarProfileButton.setOnClickListener { btn ->
            Navigation.findNavController(btn).navigate(R.id.updateCarProfileFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CarProfileViewModel::class.java)

        viewModel.getCar()

        viewModel.mCar.observe(viewLifecycleOwner, Observer { car ->

            if (car == null) {
                requireContext().toast("You have not updated your cars profile")
            } else {
                modelTv.text = car.model
                plateNumberTv.text = car.plateNumber

                Glide.with(this)
                    .load(car.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_directions_car)
                    .into(imageIv)

            }

        })
    }

}
