package com.mysasse.wheretonext.ui.rides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.ui.TransactDialogListener

class TransactDialog(private val listener: TransactDialogListener) : DialogFragment(),
    View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_transact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.pay_pal_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.cash_btn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        listener.onViewClicked(v)
    }

}