package com.fourofourfound.aims_delivery.homePage

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.broadcastReceiver.NetworkChangedBroadCastReceiver
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.CustomWorkManager
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var viewModel: HomePageViewModel
    lateinit var locationPermissionUtil: BackgroundLocationPermissionUtil


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false
        )

        if (!sharedViewModel.userLoggedIn.value!!) {
            findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
        }


        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        startTripOnClick()
        setUpRecyclerView()
        setUpSwipeToRefresh()
        registerBroadCastReceiver()

        requireActivity().bottom_navigation.visibility = View.VISIBLE

        return binding.root
    }
    private fun registerBroadCastReceiver() {
        if (!sharedViewModel.isLocationBroadcastReceiverInitialized) {
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            requireContext().registerReceiver(NetworkChangedBroadCastReceiver(), intentFilter)
            sharedViewModel.isLocationBroadcastReceiverInitialized = true
        }
    }



    private fun setUpSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork()
            if (swipe_refresh.isRefreshing) swipe_refresh.isRefreshing = false

        }
    }


    private fun setUpRecyclerView() {
        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { trip ->
            if (trip.completed) findNavController().navigate(
                HomePageDirections.actionHomePageToCompletedDeliveryFragment(trip)
            )
        })

        binding.tripList.adapter = adapter
        viewModel.tripList.observe(viewLifecycleOwner) {

            //TODO new trip was added or modified. Need to send the notification to the user
            adapter.submitList(it)
        }
    }

    private fun startTripOnClick() {
        binding.btnStartTrip.setOnClickListener {
            viewModel.tripList.value?.let {
                val tripToStart = it[0]
                if (!tripToStart.completed)
                    showStartTripDialog(tripToStart)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showStartTripDialog(tripToStart: Trip) {
        CustomDialogBuilder(
            requireContext(),
            "Start a trip?",
            null,
            "Start now",
            { markTripStart(tripToStart) },
            "No",
            null,
            true
        ).builder.show()
    }

    override fun onStart() {
        super.onStart()
        BackgroundLocationPermissionUtil(requireContext()).checkPermissionsOnStart()
    }

    private fun markTripStart(tripToStart: Trip) {
        sharedViewModel.setSelectedTrip(tripToStart)
        CustomWorkManager(requireContext()).apply {
            //TODO need to call both methods
            sendLocationAndUpdateTrips()
            sendLocationOnetime()
        }
        requireActivity().bottom_navigation.selectedItemId = R.id.delivery_navigation
    }
}


