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
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryCompletedBinding

/**
 * Completed delivery fragment
 * This fragment is responsible for displaying the data related to
 * completed deliveries.
 * @constructor Create empty Completed delivery fragment
 */
class CompletedDeliveryFragment : Fragment() {
    /**
     * Binding
     * The binding object that is used by this fragment
     */
    lateinit var binding: FragmentDeliveryCompletedBinding

    /**
     * View model
     * The view model that is used by this fragment to store data
     */
    private lateinit var viewModel: CompletedDeliveryViewModel

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /**
     * On create view
     * It is called when the fragment is created.
     * @param inflater the inflater used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get args using by navArgs property delegate
        val tripFragmentArgs by navArgs<CompletedDeliveryFragmentArgs>()
        //create a binding object
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_completed,
            container,
            false
        )
        if (sharedViewModel.driver == null) findNavController().navigateUp()
        //getting a view model from a factory
        viewModel = ViewModelProvider(this).get(CompletedDeliveryViewModel::class.java)
        viewModel.getTripDetails(tripFragmentArgs.trip.tripId, tripFragmentArgs.seqNo)
        val adapter = CompletedDeliveryAdapter()
        binding.sourceOrSiteInfo.adapter = adapter
        viewModel.loading.observe(viewLifecycleOwner)
        {
            adapter.data = viewModel.tripDetails
        }
        binding.lifecycleOwner = this
        binding.tripName.text = tripFragmentArgs.trip.tripName
        return binding.root
    }
}