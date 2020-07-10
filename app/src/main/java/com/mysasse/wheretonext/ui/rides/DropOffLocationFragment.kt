package com.mysasse.wheretonext.ui.rides

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.Navigation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*

import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.utils.toast

/**
 * A map fragment for rider selecting the drop off location for the ride being offered
 */
class DropOffLocationFragment : Fragment(), OnMapReadyCallback {

    /**
     * Google Map fields for the map and the single needed marker
     */
    private var mGoogleMap: GoogleMap? = null
    private var mMarker: Marker? = null
    private var mDropOffLatLng: LatLng? = null

    private lateinit var mapView: MapView
    private lateinit var locationAtv: AutoCompleteTextView
    private lateinit var nextButton: Button

    companion object {
        const val DEFAULT_ZOOM: Float = 18F
        const val RESOLVABLE_API_RC = 82
        const val TAG = "DropOffLocationFrag"
    }

    /**
     * Get device location Field and location instances
     */
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLastKnownLocation: Location
    private lateinit var mLocationCallback: LocationCallback
    private var requestingLocationUpdates = true


    /**
     * Places API Instances
     */
    private lateinit var mPlacesClient: PlacesClient
    private lateinit var mAutocompletePredictions: List<AutocompletePrediction>

    /**
     * Passed arguments
     */
    private var mRide: Ride? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the passed ride if any from the arguments
        mRide = DropOffLocationFragmentArgs.fromBundle(requireArguments()).ride


        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        //Initialize places variables
        Places.initialize(requireContext(), getString(R.string.google_api_key))
        mPlacesClient = Places.createClient(requireContext())


        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult ?: return

                for (location in locationResult.locations) {
                    mLastKnownLocation = location
                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mLastKnownLocation.latitude,
                                mLastKnownLocation.longitude
                            ), DEFAULT_ZOOM
                        )
                    )

                    if (mMarker == null) {
                        mMarker = mGoogleMap?.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    mLastKnownLocation.latitude,
                                    mLastKnownLocation.longitude
                                )
                            ).title(getString(R.string.drop_off_location_text))
                        )

                    } else {
                        mMarker?.position =
                            LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude)
                    }
                }

                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requestingLocationUpdates)
            startLocationUpdates()
    }

    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(
            getLocationRequest(),
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drop_off_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map)
        locationAtv = view.findViewById(R.id.locations_atv)
        nextButton = view.findViewById(R.id.next_button)

        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)

        val sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

        locationAtv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val predictionsRequest: FindAutocompletePredictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(sessionToken)
                        .setCountry("ke")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setQuery(s.toString())
                        .build()

                mPlacesClient.findAutocompletePredictions(predictionsRequest)
                    .addOnSuccessListener { findAutocompletePredictionsResponse: FindAutocompletePredictionsResponse? ->

                        findAutocompletePredictionsResponse ?: return@addOnSuccessListener
                        mAutocompletePredictions =
                            findAutocompletePredictionsResponse.autocompletePredictions

                        val autocompletePredictions = mutableListOf<String>()

                        for (autocompletePrediction in mAutocompletePredictions) {
                            autocompletePredictions.add(
                                autocompletePrediction.getFullText(null).toString()
                            )
                        }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_dropdown_menu_popup_item,
                            autocompletePredictions
                        )

                        locationAtv.setAdapter(adapter)

                    }
                    .addOnFailureListener { exception ->

                        if (exception is ApiException) {
                            Log.d(
                                TAG,
                                "findAutocompletePredictions exception status: ${exception.statusCode}"
                            )
                        }

                        Log.e(TAG, "findAutocompletePredictions error:", exception)

                    }

            }


        })

        locationAtv.setOnItemClickListener { _, _, position, _ ->

            if (position >= mAutocompletePredictions.size) return@setOnItemClickListener

            val autocompletePrediction = mAutocompletePredictions[position]

            val clickedSuggestion: String = locationAtv.adapter.getItem(position) as String
            locationAtv.setText(clickedSuggestion)

            closeInputManager()

            val placeFields = listOf(Place.Field.LAT_LNG)

            val fetchPlaceRequest =
                FetchPlaceRequest.builder(autocompletePrediction.placeId, placeFields).build()

            mPlacesClient.fetchPlace(fetchPlaceRequest)
                .addOnSuccessListener { fetchPlaceResponse: FetchPlaceResponse? ->
                    val place = fetchPlaceResponse?.place

                    place ?: return@addOnSuccessListener

                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            place.latLng,
                            DEFAULT_ZOOM
                        )
                    )

                    if (mMarker == null) {
                        mMarker = mGoogleMap?.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    mLastKnownLocation.latitude,
                                    mLastKnownLocation.longitude
                                )
                            )
                        )

                    } else {
                        mMarker?.position =
                            LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude)
                    }

                }
                .addOnFailureListener { exception ->

                    if (exception is ApiException) {
                        Log.d(TAG, "Status Code: ${exception.statusCode}")
                        requireContext().toast("Api Exception: ${exception.localizedMessage}")
                    }

                    Log.e(TAG, "Fetching Error: ", exception)

                }
        }

        nextButton.setOnClickListener { btn ->

            /**
             * Validating the drop off location required fields
             */
            if (TextUtils.isEmpty(locationAtv.text)) {
                locationAtv.error = "Location Name is required"
                locationAtv.requestFocus()
                return@setOnClickListener
            }

            if (mDropOffLatLng == null) {
                requireContext().toast("You need to tap to drop off location on the map until a marker is there")
                return@setOnClickListener
            }

            val dropOffMap = mapOf(
                "lat" to mDropOffLatLng?.latitude,
                "lng" to mDropOffLatLng?.longitude,
                "name" to locationAtv.text.toString()
            )

            mRide?.dropOff = dropOffMap

            val action =
                DropOffLocationFragmentDirections.actionDropOffLocationFragmentToOfferDateFragment(
                    mRide
                )
            Navigation.findNavController(btn).navigate(action)
        }
    }

    private fun closeInputManager() {

        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm ?: return

        imm.hideSoftInputFromWindow(
            locationAtv.applicationWindowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        mGoogleMap = googleMap

        MapsInitializer.initialize(requireContext())

        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        //Enabling your location and setting my location button enabled
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true

        mGoogleMap?.setOnMapClickListener { latLng: LatLng? ->

            mMarker?.position = latLng
            mDropOffLatLng = latLng
        }

        val locationRequest = getLocationRequest()
        locationRequest ?: return

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient: SettingsClient =
            LocationServices.getSettingsClient(requireContext())

        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener { getDeviceLocation() }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {

                    exception.startResolutionForResult(requireActivity(), RESOLVABLE_API_RC)

                } catch (sendException: IntentSender.SendIntentException) {
                    Log.e(TAG, "Resolvable API Exception ln${171}", sendException)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESOLVABLE_API_RC -> getDeviceLocation()

                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            Log.d(TAG, "Activity Result Not OK")
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { locationTask ->

            if (locationTask.isSuccessful) {
                locationTask.result?.let { latestLocation ->
                    mLastKnownLocation = latestLocation

                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mLastKnownLocation.latitude,
                                mLastKnownLocation.longitude
                            ), DEFAULT_ZOOM
                        )
                    )

                    mMarker = mGoogleMap?.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                mLastKnownLocation.latitude,
                                mLastKnownLocation.longitude
                            )
                        ).title(getString(R.string.drop_off_location_text))
                    )

                    return@addOnCompleteListener
                }

            }
        }
    }

    private fun getLocationRequest() = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }

}
