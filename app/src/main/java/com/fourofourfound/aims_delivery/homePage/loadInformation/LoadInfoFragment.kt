package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.*
import com.fourofourfound.aims_delivery.worker.CustomWorkManager
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.LoadInformationBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class LoadInfoFragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: LoadInformationBinding
    private lateinit var viewModel: LoadInfoViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()
    var bottomText  =  "Start Trip"

    lateinit var currentTrip: Trip

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val tripFragmentArgs by navArgs<LoadInfoFragmentArgs>()
        currentTrip = tripFragmentArgs.trip

        binding = DataBindingUtil.inflate(
            inflater, R.layout.load_information, container, false
        )

        viewModel = ViewModelProvider(this).get(LoadInfoViewModel::class.java)
        val adapter = LoadInfoAdapter()

        binding.pickupList.adapter = adapter
        adapter.data = currentTrip.sourceOrSite
        adapter.trip = currentTrip
        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            it?.apply {
                if (it.tripId == currentTrip.tripId) {
                    adapter.data = it.sourceOrSite
                    currentTrip = it
                    startTripOnClick(currentTrip)
                }

            }
        }
        return binding.root
    }

    private fun scrollTripStartIcon() {
        binding.pickupList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0)
                    binding.startTripText.text =""
                else if (dy < 0)
                    binding.startTripText.text = bottomText
            }


        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = currentTrip.tripName


        if (sharedViewModel.selectedSourceOrSite.value == null && sharedViewModel.selectedTrip.value != null) {
            binding.startTripText.text = "Deliver next"
            bottomText =  "Deliver next"
        }

        if (sharedViewModel.selectedSourceOrSite.value != null && sharedViewModel.selectedTrip.value != null) {
            binding.startTripText.text = "Continue Delivery"
            bottomText = "Continue Delivery"
        }

        if (sharedViewModel.selectedTrip.value != null && sharedViewModel.selectedTrip.value!!.tripId != currentTrip.tripId) binding.startTripText.visibility =
            View.GONE
        else {
            scrollTripStartIcon()
            startTripOnClick(currentTrip)
        }


    }

    /**
     * Mark trip start
     * Starts a worker to start sending location updates and  navigates the user to
     * delivery page
     * @param tripToStart
     */
    private fun markTripStart(sourceOrSite: SourceOrSite, currentTrip: Trip) {
        var time = Calendar.getInstance()

        currentTrip.deliveryStatus = DeliveryStatusEnum.ONGOING
        sharedViewModel.selectedTrip.value = currentTrip
        val statusCode =  StatusMessageEnum.SELTRIP
        viewModel.changeTripStatus(currentTrip.tripId, DeliveryStatusEnum.ONGOING)

        val toPut = DatabaseStatusPut(  sharedViewModel.driver!!.code.trim(),
            currentTrip.tripId,
            statusCode.code,
            statusCode.message,
            getDate(time))
        DeliveryStatusViewModel.sendStatusUpdate(
          toPut, getDatabaseForDriver(requireContext())
        )

        markDestinationStart(sourceOrSite)

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
            "Start this trip?",
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
            it.deliveryStatus != DeliveryStatusEnum.COMPLETED
        }

        if (notCompletedList.isEmpty()) {

            binding.startTripText.text = "Trip Completed"
            bottomText = "Trip Completed"

            binding.startTripText.setOnClickListener { null }
            binding.startTripText.visibility = View.VISIBLE
            animateViewVisibility(
                binding.startTripText.rootView,
                binding.startTripText,
                true,
            )
            if(sharedViewModel.selectedTrip.value != null) {
                viewModel.changeTripStatus(
                    sharedViewModel.selectedTrip.value!!.tripId,
                    DeliveryStatusEnum.COMPLETED
                )

                val statusCodeToGet = StatusMessageEnum.TRIPDONE
                val toPut = DatabaseStatusPut(
                    sharedViewModel.driver!!.code.trim(),
                    sharedViewModel.selectedTrip.value!!.tripId,
                    statusCodeToGet.code,
                    statusCodeToGet.message,
                    getDate(Calendar.getInstance())
                )

                DeliveryStatusViewModel.sendStatusUpdate(
                    toPut,
                    getDatabaseForDriver(requireContext())
                )
                sharedViewModel.selectedTrip.value = null
            }


        }
        else
        {
            var sortedList = notCompletedList.sortedWith(compareBy { it.seqNum })
            setUpClickListener(currentTrip, sortedList)
        }

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
        if (sortedList.isNotEmpty()) {

            animateViewVisibility(
                binding.startTripText.rootView,
                binding.startTripText,
                true,
            )
            binding.startTripText.setOnClickListener {
                CustomWorkManager(requireContext()).apply {
                    sendLocationAndUpdateTrips()
                    sendLocationOnetime()
                }

                if (sharedViewModel.selectedTrip.value?.tripId != currentTrip.tripId) {
                    showStartTripDialog(sortedList[0], currentTrip)
                } else {
                    if (sharedViewModel.selectedSourceOrSite.value == null) {
                        markDestinationStart(sortedList[0])
                    } else {
                        deliveryStatusViewModel.destinationApproachingShown= false
                        requireActivity().bottom_navigation.selectedItemId =
                            R.id.delivery_navigation
                    }
                }

            }
        }

    }

    private fun markDestinationStart(sourceOrSite: SourceOrSite) {
        var time = Calendar.getInstance()


        Log.i(
            "NETWORK-CALL", String.format(
                "Sending destination started: %d-%d-%d %d:%d \nDriver ID: %s \nTrip ID: %s \nSource ID: %s",
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                sharedViewModel.driver!!.code,
                currentTrip.tripId,
                sourceOrSite.seqNum
            )
        )
        sourceOrSite.deliveryStatus = DeliveryStatusEnum.ONGOING
        sharedViewModel.selectedSourceOrSite.value = sourceOrSite
        viewModel.changeDeliveryStatus(
            currentTrip.tripId,
            sourceOrSite.seqNum,
            DeliveryStatusEnum.ONGOING
        )
        deliveryStatusViewModel.destinationApproachingShown= false
        deliveryStatusViewModel.destinationLeavingShown= false
        //change the active tab to delivery tab
        requireActivity().bottom_navigation.selectedItemId = R.id.delivery_navigation


    }
}