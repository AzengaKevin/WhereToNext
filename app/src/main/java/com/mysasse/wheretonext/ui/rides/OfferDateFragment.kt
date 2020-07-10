package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride

/**
 * Select the date and time of travel
 */
class OfferDateFragment : Fragment() {

    private lateinit var dateTxt: TextInputEditText
    private lateinit var timeTxt: TextInputEditText
    private lateinit var nextButton: Button

    /**
     * Passed Arguments
     */
    private var mRide: Ride? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Get the passed from the bundles
         */
        mRide = OfferDateFragmentArgs.fromBundle(requireArguments()).ride
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Register the views
        dateTxt = view.findViewById(R.id.date_txt)
        timeTxt = view.findViewById(R.id.time_txt)

        nextButton = view.findViewById(R.id.next_button)

        nextButton.setOnClickListener { btn ->

            /**
             * Validate the entered date and time
             */
            val date = dateTxt.text
            val time = timeTxt.text

            if (TextUtils.isEmpty(date)) {
                dateTxt.error = "Travel date is required"
                dateTxt.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(time)) {
                timeTxt.error = "Travel time is required"
                timeTxt.requestFocus()
                return@setOnClickListener
            }

            mRide?.time = time.toString()
            mRide?.date = date.toString()

            val action =
                OfferDateFragmentDirections.actionOfferDateFragmentToRidePassengersFragment(mRide)
            Navigation.findNavController(btn).navigate(action)


        }
    }

}
