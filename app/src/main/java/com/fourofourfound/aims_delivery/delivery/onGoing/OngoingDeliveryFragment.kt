package com.fourofourfound.aims_delivery.delivery.onGoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding

class OngoingDeliveryFragment : Fragment() {
    private var _binding: FragmentDeliveryOngoingBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_ongoing, container, false)

        val viewModel = ViewModelProvider(this).get(OngoingDeliveryViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            if(sharedViewModel.selectedTrip.value!=null)
            {
                viewModel.setCurrentTrip(sharedViewModel.selectedTrip.value!!)
            }
        }


        viewModel.tripCompleted.observe(viewLifecycleOwner){
            if(it) {
                findNavController().navigate(OngoingDeliveryFragmentDirections.actionDeliveryFragmentToHomePage())
                sharedViewModel.setSelectedTrip(null)
                viewModel.doneNavigatingToHomePage()
            }

        }

        return  binding.root
    }

}