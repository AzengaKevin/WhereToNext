package com.mysasse.wheretonext.ui.rides

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
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
import com.google.firebase.auth.FirebaseAuth
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.utils.toast

/**
 * Pick up location fragment to choose the pickup location for the ride
 */

class PickupLocationFragment : Fragment(), OnMapReadyCallback {

    /**
     * Firebase Variables
     *
     * @var FirebaseAuth mAuth
     */
    private lateinit var mAuth: FirebaseAuth


    /**
     * Google Maps Variables
     */
    private var mGoogleMap: GoogleMap? = null
    private var mMarker: Marker? = null
    private var pickupLatLng: LatLng? = null

    //Registered views
    private lateinit var mapView: MapView
    private lateinit var locationsAtv: AutoCompleteTextView
    private lateinit var nextButton: Button

    //Location Variables
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLastKnownLocation: Location
    private lateinit var mLocationCallback: LocationCallback
    private var requestingLocationUpdates = true

    /**
     * Places API Variables
     */
    private lateinit var mPlacesClient: PlacesClient
    private lateinit var mAutocompletePredictions: List<AutocompletePrediction>

    companion object {
        const val TAG = "PickupLocationFrag"
        const val RESOLVABLE_API_RC = 78
        const val DEFAULT_ZOOM = 18F
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(
            getLocationRequest(),
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getLocationRequest() = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

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
                            ).title(getString(R.string.pickup_location_text))
                        )
                    } else {
                        mMarker?.position =
                            LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude)
                    }
                }

                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
            }
        }

        Places.initialize(requireContext(), getString(R.string.google_api_key))
        mPlacesClient = Places.createClient(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pickup_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map)
        locationsAtv = view.findViewById(R.id.locations_atv)
        nextButton = view.findViewById(R.id.next_button)

        val sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

        locationsAtv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val autocompletePredictionRequest =
                    FindAutocompletePredictionsRequest.builder().apply {
                        setCountries("ke", "us")
                        setTypeFilter(TypeFilter.ADDRESS)
                        setSessionToken(sessionToken)
                        setQuery(s.toString())
                    }.build()

                mPlacesClient.findAutocompletePredictions(autocompletePredictionRequest)
                    .addOnCompleteListener { task: Task<FindAutocompletePredictionsResponse> ->
                        if (task.isSuccessful) {

                            val autocompletePredictionsResponse = task.result

                            autocompletePredictionsResponse ?: return@addOnCompleteListener

                            mAutocompletePredictions =
                                autocompletePredictionsResponse.autocompletePredictions

                            val autocompletePredictions = mutableListOf<String>()

                            mAutocompletePredictions.forEach { autocompletePrediction ->
                                autocompletePredictions.add(
                                    autocompletePrediction.getFullText(null).toString()
                                )
                            }

                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.simple_dropdown_menu_popup_item,
                                autocompletePredictions
                            )

                            locationsAtv.setAdapter(adapter)

                        } else {
                            Log.e(TAG, "Fetching places predictions failed", task.exception)
                        }
                    }
            }

        })

        locationsAtv.setOnItemClickListener { _, _, position, _ ->

            if (position >= mAutocompletePredictions.size) return@setOnItemClickListener

            val autocompletePrediction = mAutocompletePredictions[position]

            val clickedSuggestion: String = locationsAtv.adapter.getItem(position) as String
            locationsAtv.setText(clickedSuggestion)

            closeInputManager()

            val placeField = listOf(Place.Field.LAT_LNG)

            val fetchPlaceRequest: FetchPlaceRequest =
                FetchPlaceRequest.builder(autocompletePrediction.placeId, placeField).build()

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

                }
                .addOnFailureListener { exception ->

                    if (exception is ApiException) {
                        Log.d(TAG, "Status Code: ${exception.statusCode}")
                        Log.e(TAG, "Fetching Error: ", exception)
                    }
                }

        }


        nextButton.setOnClickListener { btn ->

            val ride = Ride(userId = mAuth.currentUser?.uid)

            /**
             * Validate the three required fields
             */

            if (TextUtils.isEmpty(locationsAtv.text)) {
                locationsAtv.error = "Location Name is required"
                locationsAtv.requestFocus()
                return@setOnClickListener
            }

            if (pickupLatLng == null) {
                requireContext().toast("You need to point to the pickup location on the map")
                return@setOnClickListener
            }

            val pickup = mapOf(
                "lat" to pickupLatLng?.latitude,
                "lng" to pickupLatLng?.longitude,
                "name" to locationsAtv.text.toString()
            )

            ride.pickup = pickup

            val action =
                PickupLocationFragmentDirections.actionPickupLocationFragmentToDropOffLocationFragment(
                    ride
                )
            Navigation.findNavController(btn).navigate(action)
        }
    }

    private fun closeInputManager() {

        val imm: InputMethodManager? =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        imm ?: return

        imm.hideSoftInputFromWindow(
            locationsAtv.applicationWindowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        mGoogleMap = googleMap

        MapsInitializer.initialize(requireContext())

        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true

        mGoogleMap?.setOnMapClickListener { latLng ->
            mMarker?.position = latLng

            pickupLatLng = latLng
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
                    Log.e(TAG, "Resolvable API Exception", sendException)
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { locationTask ->

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
                    ).title(getString(R.string.pickup_location_text))
                )
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
            Log.d(TAG, "Activity Result Not OK ln")
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }
}
