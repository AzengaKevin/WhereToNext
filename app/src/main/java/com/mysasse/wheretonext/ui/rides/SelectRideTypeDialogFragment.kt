package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.ui.DialogFragmentClickListener

class SelectRideTypeDialogFragment(private val listener: DialogFragmentClickListener) :
    DialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_select_ride_type, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.find_ride_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.offer_ride_btn).setOnClickListener(this)
        view.findViewById<TextView>(R.id.cancel_tv).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        listener.onClick(v!!)
        dismiss()
    }
}