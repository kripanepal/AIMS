package com.fourofourfound.aims_delivery.homePage

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.worker.SyncDataWithServer
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate<FragmentHomePageBinding>(
            inflater, R.layout.fragment_home_page, container, false
        )

        val viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        val sharedViewModel: SharedViewModel by activityViewModels()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.btnStartTrip.setOnClickListener {
            viewModel.tripList.value?.let {
                var tripToStart = it[0]
                if (!tripToStart.completed) {
                    showDialog(tripToStart)
                }
            }
        }


        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { trip ->
            if (trip.completed) findNavController().navigate(HomePageDirections.actionHomePageToCompletedDeliveryFragment(trip))
        })

        binding.sleepList.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork()
            if (swipe_refresh.isRefreshing) {
                swipe_refresh.isRefreshing = false
            }
        }

        viewModel.tripList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        scheduleWork()
        check()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(tripToStart: Trip) {
        AlertDialog.Builder(context)
            .setTitle("Start a trip?")
            .setCancelable(false)
            .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .setPositiveButton("Start now") { _: DialogInterface, _: Int ->
                sharedViewModel.setSelectedTrip(tripToStart)
                findNavController().navigate(R.id.ongoingDeliveryFragment)
            }
            .show()
    }

    private fun scheduleWork() {
        applicationScope.launch {
            var repeatingRequest = PeriodicWorkRequestBuilder<SyncDataWithServer>(
                15,
                TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                SyncDataWithServer.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }
    private fun check() {
        applicationScope.launch {
            val workManager = context?.let { WorkManager.getInstance(it) }

            val workInfos = workManager?.getWorkInfosForUniqueWork(SyncDataWithServer.WORK_NAME)
                ?.await()
            if (workInfos != null) {
                if (workInfos.size == 1) {
                    // for (workInfo in workInfos) {
                    val workInfo = workInfos[0]

                    if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                        Log.i("Refresh", "Running")
                    } else {
                        Log.i("Refresh", "Else Running")

                    }
                } else {
                    Log.i("Refresh", "Nothing")
                }
            }
        }
    }
}


