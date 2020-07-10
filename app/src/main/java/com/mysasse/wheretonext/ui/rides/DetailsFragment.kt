package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class DetailsFragment : Fragment(), OnMapReadyCallback {

    /**
     * The fragments view model
     */
    private lateinit var viewModel: DetailsViewModel

    /**
     * Registered views that need to be updated
     */
    private lateinit var userAvatarCiv: CircleImageView
    private lateinit var userNameTv: TextView

    private lateinit var mMapView: MapView
    private lateinit var bookRideButton: Button


    /**
     * Google map variables
     */
    private var mGoogleMap: GoogleMap? = null

    /**
     * The passed ride for the details
     */
    private lateinit var mRide: Ride

    companion object {
        const val TAG = "RideDetailsFragment"
        const val DEFAULT_ZOOM = 16F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRide = DetailsFragmentArgs.fromBundle(requireArguments()).ride
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ride_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAvatarCiv = view.findViewById(R.id.user_avatar_civ)
        userNameTv = view.findViewById(R.id.user_name_tv)

        view.findViewById<TextView>(R.id.date_tv).text = mRide.date
        view.findViewById<TextView>(R.id.time_tv).text = mRide.time
        view.findViewById<TextView>(R.id.cost_tv).text = mRide.cost.toString()

        view.findViewById<TextView>(R.id.pickup_location_tv).text =
            mRide.pickup?.get("name").toString()
        view.findViewById<TextView>(R.id.drop_off_location_tv).text =
            mRide.dropOff?.get("name").toString()

        //Get and initialize the map view
        mMapView = view.findViewById(R.id.map)
        bookRideButton = view.findViewById(R.id.book_ride_btn)

        view.findViewById<Button>(R.id.view_ride_bookings_btn).setOnClickListener { btn ->
            Navigation.findNavController(btn).navigate(R.id.writeReviewFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        mMapView.onCreate(null)
        mMapView.onResume()
        mMapView.getMapAsync(this)

        /**
         * Request for the riders profile
         */
        if (mRide.userId != null)
            viewModel.getRidersProfile(mRide.userId!!)

        /**
         * Listen to profile if anything comes up
         */
        viewModel.mProfile.observe(viewLifecycleOwner, Observer { profile ->
            /**
             * Update the profile views
             */

            Glide.with(this)
                .load(profile.profilePic)
                .centerCrop()
                .placeholder(R.mipmap.avatar)
                .into(userAvatarCiv)

            userNameTv.text = profile.name

        })

    }


    override fun onMapReady(googleMap: GoogleMap?) {

        MapsInitializer.initialize(requireContext())

        mGoogleMap = googleMap
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        /**
         * Calculate the camera position using the pickup and drop off locations of the ride
         */

        val lat =
            getCenter(mRide.pickup?.get("lat").toString(), mRide.dropOff?.get("lat").toString())
        val lng =
            getCenter(mRide.pickup?.get("lng").toString(), mRide.dropOff?.get("lng").toString())

        if (lat != null && lng != null) {
            mGoogleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, lng),
                    DEFAULT_ZOOM
                )
            )

            mGoogleMap?.addMarker(
                MarkerOptions().title("Pickup Location").position(
                    LatLng(
                        mRide.pickup?.get("lat").toString().toDouble(),
                        mRide.pickup?.get("lng").toString().toDouble()
                    )
                )
            )

            mGoogleMap?.addMarker(
                MarkerOptions().title("Drop Off Location").position(
                    LatLng(
                        mRide.dropOff?.get("lat").toString().toDouble(),
                        mRide.dropOff?.get("lng").toString().toDouble()
                    )
                )
            )
        }

    }

    /**
     * Utility function to get the center Geo-codes of the pickup and drop off locations
     */
    private fun getCenter(val1: String, val2: String): Double? {
        try {

            return (val1.toDouble() + val2.toDouble()) / 2

        } catch (exception: Exception) {
            requireContext().toast("Null latitude instance are throwing conversion errors")
        }

        return null
    }

}
