package com.mysasse.wheretonext.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.ui.DialogFragmentClickListener
import com.mysasse.wheretonext.ui.RideClickListener
import com.mysasse.wheretonext.ui.rides.RidesAdapter
import com.mysasse.wheretonext.ui.rides.SelectRideTypeDialogFragment
import com.mysasse.wheretonext.utils.toast

class HomeFragment : Fragment(), DialogFragmentClickListener, RideClickListener {

    private lateinit var addRideFab: FloatingActionButton
    private lateinit var viewModel: HomeViewModel
    private lateinit var rideRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rideRecyclerView = view.findViewById(R.id.rides_recycler_view)
        rideRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        addRideFab = view.findViewById(R.id.add_ride_fab)

        addRideFab.setOnClickListener {

            SelectRideTypeDialogFragment(this).show(childFragmentManager, TAG)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel.browseRides()

        viewModel.mRides.observe(viewLifecycleOwner, Observer { rides ->

            val ridesAdapter = RidesAdapter(rides, this)

            rideRecyclerView.adapter = ridesAdapter

        })

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->
            Log.e(TAG, "Getting Rides: ", exception)
            requireContext().toast("Error: ${exception?.localizedMessage}")
        })
    }

    companion object HomeFragment {
        const val TAG = "HomeFragment"
    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.find_ride_btn -> {
                Navigation.findNavController(addRideFab)
                    .navigate(R.id.action_homeFragment_to_findRideFragment)
            }
            R.id.offer_ride_btn -> {
                Navigation.findNavController(addRideFab)
                    .navigate(R.id.pickupLocationFragment)
            }
            R.id.cancel_tv -> {
            }
            else -> {
            }
        }
    }

    override fun onRideClicked(ride: Ride, view: View) {

        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(ride)

        Navigation.findNavController(view).navigate(action)
    }
}
