package com.fourofourfound.aims_delivery.homePage

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.broadcastReceiver.NetworkChangedBroadCastReceiver
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_page.*

/**
 * Home page
 * This fragment is responsible for displaying the list of trips that are available to the user
 * User can start the trip in the order as it is being displayed
 *
 * @constructor Create empty Home page
 */
class HomePage : Fragment() {

    /**
     * _binding
     * The binding object that is used by this fragment
     */
    private var _binding: FragmentHomePageBinding? = null

    /**
     * binding
     * The binding object that is used by this fragment which delegates to
     * _binding to prevent memory leaks
     */
    private val binding get() = _binding!!

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /**
     * View model
     *  the ViewModel that is used by the fragment to store the data
     */
    lateinit var viewModel: HomePageViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //create a binding object
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false
        )

        //navigate user to login screen if user is not logged in
        if (!sharedViewModel.userLoggedIn.value!!) {
            findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
            return binding.root
        }

        //initialize viewModel and assign value to the viewModel in xml file
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        setUpRecyclerView()
        setUpSwipeToRefresh()
        registerBroadCastReceiver()
        setUpToolBar()


        return binding.root
    }

    /**
     * Set Up Tool Bar
     *This methods changes the visibility of toolbar and action bar to visible
     */
    private fun setUpToolBar() {
        requireActivity().apply {
            bottom_navigation.visibility = View.VISIBLE
            (this as AppCompatActivity?)?.supportActionBar!!.show()
        }
    }

    /**
     * Register broad cast receiver
     * This method registers for a broadcast receiver to listen for internet connection change.
     */
    private fun registerBroadCastReceiver() {
        if (!sharedViewModel.isLocationBroadcastReceiverInitialized) {
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            requireContext().registerReceiver(NetworkChangedBroadCastReceiver(), intentFilter)
            sharedViewModel.isLocationBroadcastReceiverInitialized = true
        }
    }


    /**
     * Set up swipe to refresh
     *Sets up swipe to refresh view which refreshes the trip list from the network
     */
    private fun setUpSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork()
            if (swipe_refresh.isRefreshing) swipe_refresh.isRefreshing = false
        }
    }

    /**
     * Set up recycler view
     *Sets up the recycler view to display the list of trips
     */
    private fun setUpRecyclerView() {
        //adapter for the recycler view
        val adapter = TripListAdapter(requireContext(), TripListListener { trip ->
            //set up the behaviour of button on the item being displayed
            if (trip.status == "COMPLETED") findNavController().navigate(
                HomePageDirections.actionHomePageToCompletedDeliveryFragment(
                    trip
                )
            )
            else {

                findNavController().navigate(
                    HomePageDirections.actionHomePageToLoadInfoFragment(
                        trip
                    )
                )
            }
        }, viewModel)

        binding.tripList.adapter = adapter


        //observe for any changes on the trips and inform that to the user
//        viewModel.tripList?.observe(viewLifecycleOwner) {
//            //TODO new trip was added or modified. Need to send the notification to the user
//            for(trip in it)
//            {
//                Log.i("AAAAAAAAA",trip.sourceOrSite.size.toString())
//            }
//            adapter.submitList(it)
//        }
    }


    /**
     * On destroy view
     *assign _binding to null to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * This method checks for all required permissions to access the location in background.
     */
    override fun onStart() {
        super.onStart()
        BackgroundLocationPermissionUtil(requireContext()).checkPermissionsOnStart()
    }


}


