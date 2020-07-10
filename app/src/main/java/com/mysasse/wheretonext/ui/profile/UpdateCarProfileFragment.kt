package com.mysasse.wheretonext.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Car
import com.mysasse.wheretonext.utils.hide
import com.mysasse.wheretonext.utils.show
import com.mysasse.wheretonext.utils.toast
import java.io.ByteArrayOutputStream

class UpdateCarProfileFragment : Fragment() {

    //Register views
    private lateinit var imageIv: ImageView
    private lateinit var updateCarProfileImageProgressBar: ProgressBar
    private lateinit var modelTxt: TextInputEditText
    private lateinit var plateNumberTxt: TextInputEditText
    private lateinit var updateCarProfileButton: Button
    private lateinit var updateCarProfileProgressBar: ProgressBar

    private var mCar: Car? = null

    companion object UpdateCarProfileFragment {
        const val TAG = "UpdateCarProfileFrag"
        const val IMAGE_CAPTURE_RC = 22
        const val SELECT_IMAGE_RC = 78
    }

    private lateinit var viewModel: UpdateCarProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_car_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageIv = view.findViewById(R.id.image_iv)
        updateCarProfileImageProgressBar =
            view.findViewById(R.id.update_car_profile_image_progress_bar)
        modelTxt = view.findViewById(R.id.model_txt)
        plateNumberTxt = view.findViewById(R.id.plate_number_txt)
        updateCarProfileButton = view.findViewById(R.id.update_car_profile_button)
        updateCarProfileProgressBar = view.findViewById(R.id.update_car_profile_progress_bar)

        //Set Click Listener on the Image
        imageIv.setOnClickListener {
            val items = arrayOf<CharSequence>("Take Picture", "Select From Gallery")
            val optionsDialog = AlertDialog.Builder(requireContext())

            optionsDialog.setItems(items) { _, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> selectImage()
                }
            }

            optionsDialog.show()

        }

        updateCarProfileButton.setOnClickListener {

            val model = modelTxt.text.toString().trim()
            val plateNumber = plateNumberTxt.text.toString().trim()

            if (mCar == null) {

                val car = Car(model = model, plateNumber = plateNumber)
                updateCarProfileProgressBar.show()
                viewModel.addCar(car)

            } else {
                mCar?.model = model
                mCar?.plateNumber = plateNumber

                updateCarProfileProgressBar.show()
                viewModel.updateCar(mCar!!)
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UpdateCarProfileViewModel::class.java)

        viewModel.mUri.observe(viewLifecycleOwner, Observer { uri ->
            updateCarProfileImageProgressBar.hide()
            Log.d(TAG, "Uri: $uri")
        })

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->
            updateCarProfileImageProgressBar.hide()
            requireContext().toast("A fatal error occurred: ${exception.localizedMessage}")
        })

        viewModel.mCar.observe(viewLifecycleOwner, Observer { car ->
            updateCarProfileProgressBar.hide()
            requireContext().toast("Car profile updated")
            requireActivity().onBackPressed()
        })
    }


    private fun selectImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"

            resolveActivity(requireContext().packageManager).also {
                startActivityForResult(this@apply, SELECT_IMAGE_RC)
            }
        }
    }

    private fun takePicture() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager).also {
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_RC)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                SELECT_IMAGE_RC -> {
                    imageIv.setImageURI(data?.data)

                    updateCarProfileImageProgressBar.show()

                    viewModel.setImageWithUri(data?.data!!)
                }

                IMAGE_CAPTURE_RC -> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    imageIv.setImageBitmap(bitmap)

                    val byteArrayOutputStream = ByteArrayOutputStream()

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                    updateCarProfileImageProgressBar.show()

                    viewModel.setImageWithBitmap(byteArrayOutputStream.toByteArray())


                }

                else -> super.onActivityResult(requestCode, resultCode, data)
            }

        } else {
            Log.d(TAG, "onActivityResult resultCode fail")
            requireContext().toast("onActivityResult Error")
        }
    }

}
