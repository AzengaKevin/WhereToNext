package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText

import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride

/**
 * Add the number of passengers that you are going to accommodate in the ride
 */
class PassengersFragment : Fragment() {

    private lateinit var passengersCountTxt: TextInputEditText
    private lateinit var nextButton: Button

    /**
     *  Global Variables
     *
     *  @var mRide: Ride?
     */
    private var mRide: Ride? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Get the passed ride from the global arguments
         */

        mRide = PassengersFragmentArgs.fromBundle(requireArguments()).ride
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ride_passengers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengersCountTxt = view.findViewById(R.id.passengers_count_txt)
        nextButton = view.findViewById(R.id.next_button)

        nextButton.setOnClickListener { btn ->

            val passengers = passengersCountTxt.text

            if (TextUtils.isEmpty(passengers)) {
                passengersCountTxt.error = "Number of passengers is required"
                passengersCountTxt.requestFocus()
                return@setOnClickListener
            }

            mRide?.passengers = passengers.toString().toInt()

            val action =
                PassengersFragmentDirections.actionRidePassengersFragmentToCostFragment(mRide)

            Navigation.findNavController(btn).navigate(action)
        }

    }

}
