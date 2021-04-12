package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.*
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.LoadInformationBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class LoadInfoFragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: LoadInformationBinding
    private lateinit var viewModel: LoadInfoViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var currentTrip: Trip

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tripFragmentArgs by navArgs<LoadInfoFragmentArgs>()
        currentTrip = tripFragmentArgs.trip

        binding = DataBindingUtil.inflate(
            inflater, R.layout.load_information, container, false
        )

        viewModel = ViewModelProvider(this).get(LoadInfoViewModel::class.java)
        val adapter = LoadInfoAdapter()
        binding.pickupList.adapter = adapter

        adapter.data = currentTrip.sourceOrSite

        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            it?.apply { if (it.tripId == currentTrip.tripId) adapter.data = it.sourceOrSite }


        }

        startTripOnClick(currentTrip)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = currentTrip.tripName
    }

    /**
     * Mark trip start
     * Starts a worker to start sending location updates and  navigates the user to
     * delivery page
     * @param tripToStart
     */
    private fun markTripStart(sourceOrSite: SourceOrSite, currentTrip: Trip) {
        var time = Calendar.getInstance()


        viewModel.sendTripSelectedData()

        //TODO REMOVE THIS
        var beta = {
            currentTrip.status = StatusEnum.ONGOING
            sharedViewModel.selectedTrip.value = currentTrip
            viewModel.changeTripStatus(currentTrip.tripId, StatusEnum.ONGOING)
            CustomWorkManager(requireContext()).apply {
                //TODO need to call both methods
                sendLocationAndUpdateTrips()
                sendLocationOnetime()
            }
            markDestinationStart(sourceOrSite)

        }
        CustomDialogBuilder(
            requireContext(),
            "Sending Starting Data Trip",
            String.format(
                "Time Stamp: %d-%d-%d %d:%d \nDriver ID: %s \nTrip ID: %s",
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                sharedViewModel.driver!!.driver_id,
                currentTrip.tripId
            ),
            "Ok",
            beta,
            "No",
            null,
            true
        ).builder.show()
    }

    /**
     * Show start trip dialog
     *Displays the Dialog to make sure that user wants to start the trip
     * On pressing yes, it also registers a worker which starts sending the
     * user location every 15 minutes[default]
     * @param tripToStart
     */
    private fun showStartTripDialog(sourceOrSite: SourceOrSite, currentTrip: Trip) {
        CustomDialogBuilder(
            requireContext(),
            "Start a trip?",
            null,
            "Start now",
            { markTripStart(sourceOrSite, currentTrip) },
            null,
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
            viewModel.changeTripStatus(
                sharedViewModel.selectedTrip.value!!.tripId,
                StatusEnum.COMPLETED
            )
            binding.startNavigation.visibility = View.GONE
        }
        var sortedList = notCompletedList.sortedWith(compareBy { it.seqNum })

        setUpClickListener(currentTrip, sortedList)
    }

    override fun onResume() {
        super.onResume()
        if (!sharedViewModel.userLoggedIn.value!!) {
            findNavController().navigateUp()
        }
    }

    private fun setUpClickListener(
        currentTrip: Trip,
        sortedList: List<SourceOrSite>
    ) {
        binding.startNavigation.setOnClickListener {
            if (sharedViewModel.selectedTrip.value?.tripId != currentTrip.tripId) {
                showStartTripDialog(sortedList[0], currentTrip)
            } else {
                markDestinationStart(sortedList[0])
            }
        }
    }

    private fun markDestinationStart(sourceOrSite: SourceOrSite) {
        var time = Calendar.getInstance()
        viewModel.sendDestinationSelectedData()

        //TODO REMOVE THIS
        var beta = {
            sourceOrSite.status = StatusEnum.ONGOING
            sharedViewModel.selectedSourceOrSite.value = sourceOrSite
            viewModel.changeDeliveryStatus(
                currentTrip.tripId,
                sourceOrSite.seqNum,
                StatusEnum.ONGOING
            )
            //change the active tab to delivery tab
            requireActivity().bottom_navigation.selectedItemId = R.id.delivery_navigation
        }
        CustomDialogBuilder(
            requireContext(),
            "Sending Starting Destination Info",
            String.format(
                "Time Stamp: %d-%d-%d %d:%d \nDriver ID: %s \nTrip ID: %s \nSource ID: %s",
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                sharedViewModel.driver.driver_id,
                currentTrip.tripId,
                sourceOrSite.seqNum
            ),
            "Ok",
            beta,
            null,
            null,
            true
        ).builder.show()
    }
}