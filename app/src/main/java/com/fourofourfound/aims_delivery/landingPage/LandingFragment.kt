package com.fourofourfound.aims_delivery.landingPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fourofourfound.encrypted_preferences.databinding.LandingFragmentBinding

class LandingFragment : Fragment() {

    private lateinit var viewModel: LandingViewModel
    private lateinit var binding:LandingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LandingFragmentBinding.inflate(inflater)

        val viewModel = LandingViewModel(requireNotNull(this.activity).application)
        binding.apply {
            this.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }


        return binding.root
    }


}