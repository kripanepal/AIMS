package com.fourofourfound.aims_delivery.homePage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.CustomWorkManager
import com.fourofourfound.aims_delivery.utils.checkPermission
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var viewModel: HomePageViewModel
    var permissionsToCheck = mutableListOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate<FragmentHomePageBinding>(
            inflater, R.layout.fragment_home_page, container, false
        )

        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        activity?.title = "Trip List"

//        sharedViewModel.internetConnection.observe(viewLifecycleOwner) {
//            if (it) {
//                Log.i("Sending", "Sending location")
//               // viewModel.sendSavedLocation()
//            }
//        }

        startTripOnClick()
        setUpRecyclerView()
        setUpSwipeToRefresh()

        return binding.root
    }

    private fun setUpSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork()
            if (swipe_refresh.isRefreshing) {
                swipe_refresh.isRefreshing = false
            }
        }
    }

    private fun setUpRecyclerView() {
        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { trip ->
            if (trip.completed) findNavController().navigate(
                HomePageDirections.actionHomePageToCompletedDeliveryFragment(
                    trip
                )
            )
        })

        binding.tripList.adapter = adapter
        viewModel.tripList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun startTripOnClick() {
        binding.btnStartTrip.setOnClickListener {
            viewModel.tripList.value?.let {
                var tripToStart = it[0]
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
            {  markTripStart(tripToStart) },
            "No",
            null,
            true
        ).builder.show()
    }

    private fun markTripStart(tripToStart: Trip) {
        sharedViewModel.setSelectedTrip(tripToStart)
        CustomWorkManager(requireContext()).apply {
            //sendLocationAndUpdateTrips()
            sendLocationOnetime()
        }
        findNavController().navigate(R.id.ongoingDeliveryFragment)
    }


    private fun showLocationPermissionMissingDialog() {
        CustomDialogBuilder(
            requireContext(),
            "Missing background location access",
            "Please provide background location access all the time. " +
                    "This app uses background location to track the delivery",
            "Enable location",
            {  takeToPermissionScreen() },
            null,
            null,
            false
        ).builder.show()
    }

    private fun takeToPermissionScreen() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionsToCheck.removeLast()
        }
        if (!checkPermission(
                permissionsToCheck, requireContext()
            )
        ) {
            showLocationPermissionMissingDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermission(permissionsToCheck, requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    ), 50
                )
            }
            if (Build.VERSION.SDK_INT === 23) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    ), 50
                )
            } else {
                requestPermissions(permissionsToCheck.toTypedArray(), 50)
            }
        }

    }
}


