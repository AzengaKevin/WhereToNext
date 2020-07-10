package com.mysasse.wheretonext.ui.rides

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
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.ui.RideClickListener
import com.mysasse.wheretonext.utils.toast

class RidesFragment : Fragment(), RideClickListener {

    private lateinit var ridesRecyclerView: RecyclerView

    companion object {
        const val TAG = "RidesFragment"
    }

    private lateinit var viewModel: RidesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rides_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ridesRecyclerView = view.findViewById(R.id.rides_recycler_view)

        ridesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RidesViewModel::class.java)

        viewModel.browseRides()

        viewModel.mRides.observe(viewLifecycleOwner, Observer { rides ->

            val adapter = RidesAdapter(rides, this)
            ridesRecyclerView.adapter = adapter

        })

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->
            Log.e(TAG, "Getting rides", exception)
            requireContext().toast("Error: ${exception?.localizedMessage}")
        })

    }

    override fun onRideClicked(ride: Ride, view: View) {
        val action = RidesFragmentDirections.actionRidesFragmentToDetailsFragment(ride)
        Navigation.findNavController(view).navigate(action)
    }

}
