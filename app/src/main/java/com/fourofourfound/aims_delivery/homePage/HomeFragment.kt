package com.fourofourfound.aims_delivery.homePage

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
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
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


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


}


