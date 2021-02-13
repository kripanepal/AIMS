package com.fourofourfound.aims_delivery.homePage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.trip_list_list_view.*

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

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

        viewModel.userLoggedIn.observe(viewLifecycleOwner, {
            if (!it) findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
        })

        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { trip ->
            if(!trip.completed)
            {
                sharedViewModel.setSelectedTrip(trip)
                findNavController().navigate(HomePageDirections.actionHomePageToDeliveryFragment())
            }
            else
            {
                findNavController().navigate(HomePageDirections.actionHomePageToCompletedDeliveryFragment())
            }

        })

        binding.sleepList.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener{
            if(swipe_refresh.isRefreshing){
                swipe_refresh.isRefreshing = false
            }
            viewModel.fetchTripFromNetwork()
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


}


