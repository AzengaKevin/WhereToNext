package com.mysasse.wheretonext.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Profile
import com.mysasse.wheretonext.utils.hide
import com.mysasse.wheretonext.utils.show
import com.mysasse.wheretonext.utils.toast
import java.io.ByteArrayOutputStream

class UpdateProfileFragment : Fragment() {

    private lateinit var userAvatarIv: ImageView
    private lateinit var nameTxt: TextInputEditText
    private lateinit var phoneTxt: TextInputEditText
    private lateinit var locationTxt: TextInputEditText
    private lateinit var bioTxt: TextInputEditText
    private lateinit var updateProfileButton: Button
    private lateinit var updateProfileProgressBar: ProgressBar

    private lateinit var viewModel: UpdateProfileViewModel

    private lateinit var mProfile: Profile

    companion object UpdateProfileFragment {
        const val SELECT_IMAGE_RC = 22
        const val IMAGE_CAPTURE_RC = 78
        const val TAG = "UpdateProfileFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProfile = UpdateProfileFragmentArgs.fromBundle(requireArguments()).profile

        Log.d(TAG, "Profile: $mProfile")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAvatarIv = view.findViewById(R.id.user_avatar_iv)
        nameTxt = view.findViewById(R.id.name_txt)
        phoneTxt = view.findViewById(R.id.phone_txt)
        locationTxt = view.findViewById(R.id.location_txt)
        bioTxt = view.findViewById(R.id.bio_txt)

        //Populate the values in the fields
        nameTxt.setText(mProfile.name)
        phoneTxt.setText(mProfile.phone)
        locationTxt.setText(mProfile.location)
        bioTxt.setText(mProfile.bio)


        //Load the current image to the profile
        Glide.with(this)
            .load(mProfile.profilePic)
            .centerCrop()
            .placeholder(R.mipmap.avatar)
            .into(userAvatarIv)

        updateProfileButton = view.findViewById(R.id.update_profile_button)
        updateProfileProgressBar = view.findViewById(R.id.update_profile_progress_bar)

        userAvatarIv.setOnClickListener {

            val items = arrayOf<CharSequence>("Select Image", "Take Picture")

            val dialog = AlertDialog.Builder(requireContext())
            dialog.setItems(items) { _, which ->
                when (which) {
                    0 -> selectImage()
                    1 -> takePicture()
                }

            }

            dialog.show()

        }
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

    private fun isValid(phone: String): Boolean {

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneTxt.error = "Invalid phone format"
            phoneTxt.requestFocus()
            return false
        }

        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UpdateProfileViewModel::class.java)

        updateProfileButton.setOnClickListener {
            val name = nameTxt.text.toString().trim()
            val phone = phoneTxt.text.toString().trim()
            val location = locationTxt.text.toString().trim()
            val bio = bioTxt.text.toString().trim()

            if (!isValid(phone)) return@setOnClickListener

            mProfile.name = name
            mProfile.phone = phone
            mProfile.location = location
            mProfile.bio = bio

            updateProfileProgressBar.show()
            viewModel.updateProfile(mProfile)

        }

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->

            updateProfileProgressBar.hide()
            requireContext().toast("Profile update failed: " + exception.localizedMessage)

        })

        viewModel.mProfile.observe(viewLifecycleOwner, Observer { profile ->

            updateProfileProgressBar.hide()

            requireContext().toast("Profile successfully updated")

            nameTxt.setText(profile.name)
            phoneTxt.setText(profile.phone)
            locationTxt.setText(profile.location)
            bioTxt.setText(profile.bio)

        })

        viewModel.mUri.observe(viewLifecycleOwner, Observer { uri ->

            updateProfileProgressBar.hide()
            requireContext().toast("Remember to click the update profile button to persist you image")
            Log.d(TAG, uri.toString())

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                SELECT_IMAGE_RC -> {

                    val selectedImageUri = data!!.data

                    userAvatarIv.setImageURI(selectedImageUri)


                    selectedImageUri.let { uri ->
                        updateProfileProgressBar.show()
                        viewModel.uploadProfileUri(uri!!)
                    }

                }

                IMAGE_CAPTURE_RC -> {

                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    userAvatarIv.setImageBitmap(imageBitmap)

                    //Upload the image to the server
                    val baos = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                    updateProfileProgressBar.show()
                    viewModel.uploadUserAvatar(baos.toByteArray())

                }

                else -> super.onActivityResult(requestCode, resultCode, data)

            }
        } else {
            Log.d(TAG, "Data not found")
        }
    }

}
