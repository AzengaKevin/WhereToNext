package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.utils.hide
import com.mysasse.wheretonext.utils.show
import com.mysasse.wheretonext.utils.toast

/**
 * The fragment to review all your selections and choices before publishing your ride
 */
class PublishRideFragment : Fragment() {

    /**
     * The fragment view model
     */
    private lateinit var viewModel: PublishRideViewModel

    /**
     * @var Ride mRide passed from the previous fragment via arguments global track
     */
    private var mRide: Ride? = null

    /**
     * @vars ProgressBar publishRideProgressBar
     */
    private lateinit var publishRideProgressBar: ProgressBar

    companion object PublishRideFragment {
        const val TAG = "PublishRideFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the new ride from arguments
        mRide = PublishRideFragmentArgs.fromBundle(requireArguments()).ride
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.publish_ride_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.pickup_location_name_tv).text =
            mRide?.pickup?.get("name").toString()

        view.findViewById<TextView>(R.id.pickup_location_lat_tv).text =
            "Latitude: " + mRide?.pickup?.get("lat").toString()

        view.findViewById<TextView>(R.id.pickup_location_lng_tv).text =
            "Longitude: " + mRide?.pickup?.get("lng")

        view.findViewById<TextView>(R.id.drop_off_location_name_tv).text =
            mRide?.dropOff?.get("name").toString()

        view.findViewById<TextView>(R.id.drop_off_location_lat_tv).text =
            "Latitude: " + mRide?.dropOff?.get("lat").toString()

        view.findViewById<TextView>(R.id.drop_off_location_lng_tv).text =
            "Longitude: " + mRide?.dropOff?.get("lng").toString()

        view.findViewById<TextView>(R.id.ride_date_time_tv).text = "${mRide?.time} ${mRide?.date}"
        view.findViewById<TextView>(R.id.number_of_passenger_tv).text =
            mRide?.passengers.toString()
        view.findViewById<TextView>(R.id.cost_tv).text = mRide?.cost.toString()

        publishRideProgressBar = view.findViewById(R.id.publish_ride_progress_bar)

        view.findViewById<Button>(R.id.publish_ride_button).setOnClickListener {

            mRide ?: return@setOnClickListener

            publishRideProgressBar.show()
            viewModel.addRide(mRide!!)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PublishRideViewModel::class.java)

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->

            publishRideProgressBar.hide()

            Log.e(TAG, "Ride Exception", exception)

            requireContext().toast("Error: ${exception?.localizedMessage}")
        })

        viewModel.mRide.observe(viewLifecycleOwner, Observer {
            publishRideProgressBar.hide()
            requireContext().toast("Ride successfully published / Updated")

            requireActivity().onBackPressed()
        })
    }

}
