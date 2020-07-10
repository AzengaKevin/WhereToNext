package com.mysasse.wheretonext.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Profile
import com.mysasse.wheretonext.utils.toast

class UserProfileFragment : Fragment() {

    private lateinit var viewModel: UserProfileViewModel

    //Registered Views
    private lateinit var nameTv: TextView
    private lateinit var phoneTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var bioTv: TextView
    private lateinit var mProfile: Profile
    private lateinit var userAvatarIv: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTv = view.findViewById(R.id.name_tv)
        phoneTv = view.findViewById(R.id.phone_tv)
        locationTv = view.findViewById(R.id.location_tv)
        bioTv = view.findViewById(R.id.bio_tv)
        userAvatarIv = view.findViewById(R.id.user_avatar_iv)

        val editProfileButton = view.findViewById<Button>(R.id.edit_profile_button)

        editProfileButton.setOnClickListener { btn ->
            val action =
                UserProfileFragmentDirections.actionUserProfileFragmentToUpdateProfileFragment(
                    mProfile
                )
            Navigation.findNavController(btn).navigate(action)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        //Get the current authenticated user id and request the profile
        viewModel.getProfileFromRepository()

        viewModel.mProfile.observe(viewLifecycleOwner, Observer { profile ->

            mProfile = profile

            nameTv.text = profile.name
            phoneTv.text = profile.phone
            locationTv.text = profile.location
            bioTv.text = profile.bio

            //Load the current image to the profile
            Glide.with(this)
                .load(profile.profilePic)
                .centerCrop()
                .placeholder(R.mipmap.avatar)
                .into(userAvatarIv)

        })

        viewModel.mException.observe(viewLifecycleOwner, Observer { exception ->
            requireContext().toast("Error getting profile: " + exception.localizedMessage)
        })
    }

}
