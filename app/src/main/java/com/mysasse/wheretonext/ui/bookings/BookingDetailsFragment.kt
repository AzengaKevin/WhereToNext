package com.mysasse.wheretonext.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.mysasse.wheretonext.R

class BookingDetailsFragment : Fragment() {

    companion object {
        const val TAG = "BookingDetailsFrag"
    }

    /**
     * The rideId of the ride in question
     *
     * @var rideId: String
     */
    private lateinit var rideId: String

    /**
     * View Model for the ride
     *
     * @var viewModel : BookingDetailsViewModel
     */
    private lateinit var viewModel: BookingDetailsViewModel

    /**
     * Globally tracked view instances
     *
     */
    private lateinit var passengersCountTxt: TextInputEditText
    private lateinit var payLaterCheckBox: CheckBox
    private lateinit var submitBookingProgressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rideId = BookingDetailsFragmentArgs.fromBundle(requireArguments()).rideId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.booking_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengersCountTxt = view.findViewById(R.id.passengers_count_txt)
        payLaterCheckBox = view.findViewById(R.id.pay_later_check_box)
        submitBookingProgressBar = view.findViewById(R.id.submit_booking_progress_bar)


        view.findViewById<Button>(R.id.submit_booking_btn).setOnClickListener { btn ->

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookingDetailsViewModel::class.java)
    }

}
