package com.fourofourfound.encrypted_preferences.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fourofourfound.encrypted_preferences.R
import com.fourofourfound.encrypted_preferences.databinding.FragmentHomePageBinding

class HomePage : Fragment() {
    lateinit var binding:FragmentHomePageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate<FragmentHomePageBinding>(
            inflater, R.layout.fragment_home_page, container, false
        )

        val viewModel = HomePageViewModel(requireNotNull(this.activity).application)
        binding.viewModel =viewModel
        binding.lifecycleOwner = this

        viewModel.userLoggedIn.observe(viewLifecycleOwner, Observer {
            if(!it) findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
        })


        // Inflate the layout for this fragment
        return binding.root
    }



}