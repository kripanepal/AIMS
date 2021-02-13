package com.fourofourfound.aims_delivery.delivery.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aims_delivery.delivery.onGoing.OngoingDeliveryFragmentDirections
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryCompletedBinding
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding

class CompletedDeliveryFragment : Fragment() {
    private var _binding: FragmentDeliveryCompletedBinding? = null
    private val binding get() = _binding!!

    //view model and view model factory
    private lateinit var viewModel: CompletedDeliveryViewModel
    private lateinit var viewModelFactory: CompletedDeliveryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get args using by navArgs property delegate
        val scoreFragmentArgs by navArgs<CompletedDeliveryFragmentArgs>()


        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_completed, container, false)

            //constructing a view model factory
        viewModelFactory = CompletedDeliveryViewModelFactory(scoreFragmentArgs.trip)

        //getting a view model from a factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(CompletedDeliveryViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this




        return  binding.root
    }

}