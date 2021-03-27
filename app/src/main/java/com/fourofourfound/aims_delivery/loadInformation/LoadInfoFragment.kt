package com.fourofourfound.aims_delivery.loadInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.homePage.LoadInfoViewModel
import com.fourofourfound.aimsdelivery.databinding.LoadInformationBinding

class LoadInfoFragment  : Fragment(){
    private lateinit var binding: LoadInformationBinding
    private lateinit var viewModel: LoadInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(LoadInfoViewModel::class.java)
        val adapter = LoadInfoAdapter()
        binding.pickupList.adapter = adapter



        return binding.root
    }
}