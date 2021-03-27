package com.fourofourfound.aims_delivery.loadInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.LoadInformationBinding

class LoadInfoFragment  : Fragment(){
    private lateinit var binding: LoadInformationBinding
    private lateinit var viewModel: LoadInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val tripFragmentArgs by navArgs<LoadInfoFragmentArgs>()

        val currentTrip = tripFragmentArgs.trip

        binding = DataBindingUtil.inflate(
            inflater, R.layout.load_information, container, false
        )

        viewModel = ViewModelProvider(this).get(LoadInfoViewModel::class.java)
        val adapter = LoadInfoAdapter()
        binding.pickupList.adapter = adapter

        adapter.data = currentTrip.sourceOrSite


        return binding.root
    }
}