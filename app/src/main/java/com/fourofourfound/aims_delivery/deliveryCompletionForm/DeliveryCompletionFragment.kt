package com.fourofourfound.aims_delivery.deliveryCompletionForm

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
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.DeliveryInputFormBinding
import kotlinx.android.synthetic.main.activity_main.*

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


        if(sharedViewModel.selectedSourceOrSite.value == null)
        {
            findNavController().navigateUp()
            return null
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.delivery_input_form, container, false)
        viewModelFactory = DeliveryCompletionViewModelFactory(requireActivity().application ,sharedViewModel.selectedSourceOrSite.value!!)

        //getting a view model from a factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(DeliveryCompletionViewModel::class.java)
        binding.viewModel = viewModel
        binding.submitBtn.setOnClickListener {
            viewModel.submitForm()
        }

        viewModel.doneSubmitting.observe(viewLifecycleOwner)
        {
            if(it) {
                findNavController().popBackStack(R.id.ongoingDeliveryFragment, false);
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
                viewModel.doneNavigating()
                viewModel.markDeliveryCompleted(sharedViewModel.selectedTrip.value!!.tripId)
                //TODO need to manage this
                sharedViewModel.selectedSourceOrSite.value!!.status = StatusEnum.COMPLETED
                sharedViewModel.selectedSourceOrSite.value = null
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeliveryCompletionViewModel::class.java)
    }

}