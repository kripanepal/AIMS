package com.fourofourfound.aims_delivery.delivery.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryCompletedBinding

class CompletedDeliveryFragment : Fragment() {
    lateinit var binding: FragmentDeliveryCompletedBinding

    //view model and view model factory
    private lateinit var viewModel: CompletedDeliveryViewModel
    private lateinit var viewModelFactory: CompletedDeliveryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get args using by navArgs property delegate
        val tripFragmentArgs by navArgs<CompletedDeliveryFragmentArgs>()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_completed, container, false)

        //constructing a view model factory
        viewModelFactory = CompletedDeliveryViewModelFactory(tripFragmentArgs.trip)
        //getting a view model from a factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(CompletedDeliveryViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return  binding.root
    }



}