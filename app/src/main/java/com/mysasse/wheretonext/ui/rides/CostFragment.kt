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

class CostFragment : Fragment() {

    private lateinit var costTxt: TextInputEditText
    private lateinit var nextButton: Button

    /**
     * @var Ride mRide from the previous fragment
     */
    private var mRide: Ride? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get the passed arguments

        mRide = CostFragmentArgs.fromBundle(requireArguments()).ride

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cost, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        costTxt = view.findViewById(R.id.cost_txt)
        nextButton = view.findViewById(R.id.next_button)

        nextButton.setOnClickListener { btn ->

            val cost = costTxt.text

            if (TextUtils.isEmpty(cost)) {
                costTxt.error = "The cost per person for the ride is required"
                costTxt.requestFocus()
                return@setOnClickListener
            }

            mRide?.cost = cost.toString().toDouble()

            val action = CostFragmentDirections.actionCostFragmentToPublishRideFragment(mRide)

            Navigation.findNavController(btn).navigate(action)
        }
    }

}
