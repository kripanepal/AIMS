package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.CustomWorkManager
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.LoadInformationBinding
import kotlinx.android.synthetic.main.activity_main.*

class LoadInfoFragment : Fragment() {
    private lateinit var binding: LoadInformationBinding
    private lateinit var viewModel: LoadInfoViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()


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

        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            adapter.data = it.sourceOrSite
        }
        startTripOnClick(currentTrip)
        return binding.root
    }

    /**
     * Mark trip start
     * Starts a worker to start sending location updates and  navigates the user to
     * delivery page
     * @param tripToStart
     */
    private fun markTripStart(sourceOrSite: SourceOrSite) {
        sharedViewModel.selectedSourceOrSite.value = sourceOrSite
        CustomWorkManager(requireContext()).apply {
            //TODO need to call both methods
            sendLocationAndUpdateTrips()
            sendLocationOnetime()
        }

        //change the active tab to delivery tab
        requireActivity().bottom_navigation.selectedItemId = R.id.delivery_navigation

    }

    /**
     * Show start trip dialog
     *Displays the Dialog to make sure that user wants to start the trip
     * On pressing yes, it also registers a worker which starts sending the
     * user location every 15 minutes[default]
     * @param tripToStart
     */
    private fun showStartTripDialog(sourceOrSite: SourceOrSite) {
        CustomDialogBuilder(
            requireContext(),
            "Start a trip?",
            null,
            "Start now",
            { markTripStart(sourceOrSite) },
            "No",
            null,
            true
        ).builder.show()
    }

    /**
     * Start trip on click
     * redirect the user to delivery page for a specific trip
     */
    private fun startTripOnClick(currentTrip: Trip) {
        var notCompletedList = currentTrip.sourceOrSite.filter {
            it.status != StatusEnum.COMPLETED
        }
        if (notCompletedList.isEmpty()) {
            viewModel.markTripAsCompleted(sharedViewModel.selectedTrip.value!!.tripId)
            binding.startNavigation.visibility = View.GONE
        }
        var sortedList = notCompletedList.sortedWith(compareBy { it.seqNum })

        binding.startNavigation.setOnClickListener {
            if (sharedViewModel.selectedTrip.value != currentTrip) {
                sharedViewModel.selectedTrip.value = currentTrip
                showStartTripDialog(sortedList[0])
            } else {
                markTripStart(sortedList[0])
            }
        }
    }
}