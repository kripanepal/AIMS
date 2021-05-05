package com.fourofourfound.aims_delivery.homePage

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.fourofourfound.aims_delivery.utils.toggleViewVisibility
import com.fourofourfound.aims_delivery.worker.CustomWorkManager
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

    private val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        //create a binding object
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false
        )

        //navigate user to login screen if user is not logged in
        if (sharedViewModel.driver == null) {
            findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
            super.onDestroy()
            return binding.root

        }

        //initialize viewModel and assign value to the viewModel in xml file
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        viewModel.driver = sharedViewModel.driver!!
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (viewModel.tripList.value.isNullOrEmpty())
            viewModel.fetchTripFromNetwork(sharedViewModel.driver!!.code)
        viewModel.updating.observe(viewLifecycleOwner) { sharedViewModel.loading.value = it }
        viewModel.loaded = 0




        setUpRecyclerView()
        setUpSwipeToRefresh()
        setUpToolBar()
        changeContainerVisibility()

        return binding.root
    }


    private fun changeContainerVisibility() {
        binding.completedTripListContainer.setOnClickListener {
            toggleViewVisibility(completed_trip_list)
        }

        binding.currentTripListContainer.setOnClickListener {
            toggleViewVisibility(current_trip_list)
        }
        binding.upcomingTripListContainer.setOnClickListener {
            toggleViewVisibility(upcoming_trip_list)
        }
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
     * Set up swipe to refresh
     *Sets up swipe to refresh view which refreshes the trip list from the network
     */
    private fun setUpSwipeToRefresh() {

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork(viewModel.driver.code)
            if (swipe_refresh.isRefreshing) swipe_refresh.isRefreshing = false
        }
    }



    /**
     * Set up recycler view
     *Sets up the recycler view to display the list of trips
     */
    private fun setUpRecyclerView() {
        val (currentTripAdapter, upComingTripAdapter, completedTripAdapter) = setUpAdapters()
        //observe for any changes on the trips and inform that to the user
        viewModel.tripList.observe(viewLifecycleOwner) {
                if (viewModel.loaded>0  && viewModel.getUpdatingTripsStatus()) showTripModifiedNotification()

                it.filter { trip -> trip.deliveryStatus == DeliveryStatusEnum.ONGOING }.apply {
                    binding.ongoingTripMessage.text =
                        resources.getQuantityString(R.plurals.numberOfTripsAvailable, size, size)
                    currentTripAdapter.submitList(this)
                    if (this.isNotEmpty()) {
                        var selectedTrip = this[0]
                        sharedViewModel.selectedTrip.value = selectedTrip
                        var selectedDestination =
                            selectedTrip.sourceOrSite.find { each -> each.deliveryStatus == DeliveryStatusEnum.ONGOING }

                        val previousDestinationIndex = selectedTrip.sourceOrSite.indexOf(selectedDestination) -1
                        if(previousDestinationIndex>=0)   deliveryStatusViewModel.previousDestination = selectedTrip.sourceOrSite[previousDestinationIndex]

                        if (selectedDestination != null) sharedViewModel.selectedSourceOrSite.value =
                            selectedDestination
                    }
                }

                it.filter { trip -> trip.deliveryStatus == DeliveryStatusEnum.COMPLETED }.apply {
                    binding.completedTripMessage.text =
                        resources.getQuantityString(R.plurals.numberOfTripsAvailable, size, size)
                    completedTripAdapter.submitList(this)
                }

                it.filter { trip -> trip.deliveryStatus == DeliveryStatusEnum.NOT_STARTED }.apply {
                    binding.upcomingTripMessage.text =
                        resources.getQuantityString(R.plurals.numberOfTripsAvailable, size, size)
                    upComingTripAdapter.submitList(this)
                }

        }
    }

    private fun setUpAdapters(): Triple<TripListAdapter, TripListAdapter, TripListAdapter> {
        //adapter for the recycler view
        val currentTripAdapter = TripListAdapter(TripListListener { trip ->
            findNavController().navigate(
                HomePageDirections.actionHomePageToLoadInfoFragment(
                    trip
                )
            )
        })

        //adapter for the recycler view
        val upComingTripAdapter = TripListAdapter(TripListListener { trip ->
            findNavController().navigate(
                HomePageDirections.actionHomePageToLoadInfoFragment(
                    trip
                )
            )

        })

        //adapter for the recycler view
        val completedTripAdapter = TripListAdapter(TripListListener { trip ->
            //set up the behaviour of button on the item being displayed
            findNavController().navigate(
                HomePageDirections.actionHomePageToCompletedDeliveryFragment(
                    trip, -1
                )
            )

        })

        binding.currentTripList.adapter = currentTripAdapter
        binding.completedTripList.adapter = completedTripAdapter
        binding.upcomingTripList.adapter = upComingTripAdapter
        return Triple(currentTripAdapter, upComingTripAdapter, completedTripAdapter)
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


