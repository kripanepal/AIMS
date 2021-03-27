package com.fourofourfound.aims_delivery.deliveryCompletionForm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.delivery.completed.CompletedDeliveryViewModel
import com.fourofourfound.aims_delivery.delivery.completed.CompletedDeliveryViewModelFactory
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.DeliveryInputFormBinding

class DeliveryCompletionFragment : Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var binding: DeliveryInputFormBinding

    private lateinit var viewModel: DeliveryCompletionViewModel
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.delivery_input_form, container, false)
        viewModelFactory = DeliveryCompletionViewModelFactory(requireActivity().application ,sharedViewModel.selectedSourceOrSite.value!!)

        //getting a view model from a factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(DeliveryCompletionViewModel::class.java)
        binding.viewModel = viewModel
        binding.submitBtn.setOnClickListener {
            viewModel.submitForm()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeliveryCompletionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}