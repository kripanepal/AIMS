package com.fourofourfound.aims_delivery.delivery.onGoing

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import kotlinx.android.synthetic.main.activity_main.*


class OngoingDeliveryFragment : Fragment() {
    private var _binding: FragmentDeliveryOngoingBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var mapView: MapView? = null
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (sharedViewModel.selectedTrip.value == null) {
            showNoTripSelectedDialog()
            return view
        }

        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_ongoing,
            container,
            false
        )

        val viewModel = ViewModelProvider(this).get(OngoingDeliveryViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            it?.let { viewModel.setCurrentTrip(sharedViewModel.selectedTrip.value!!) }
        }

        initializeMap(savedInstanceState)
        observeTripCompletion(viewModel)

        return binding.root
    }

    private fun initializeMap(savedInstanceState: Bundle?) {
        // Get a MapView instance from the layout.
        // Get a MapView instance from the layout.
        mapView = binding.mapView
        mapView!!.onCreate(savedInstanceState)

        mapView!!.setOnReadyListener { // This will be called each time after this activity is resumed.
            // It will not be called before the first map scene was loaded.
            // Any code that requires map data may not work as expected beforehand.
            Log.d("AAAAA", "HERE Rendering Engine attached.")
        }

        loadMapScene()
    }

    private fun observeTripCompletion(viewModel: OngoingDeliveryViewModel) {
        viewModel.tripCompleted.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
                sharedViewModel.setSelectedTrip(null)
                viewModel.doneNavigatingToHomePage()
            }
        }
    }



    private fun loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView!!.mapScene.loadScene(
            MapScheme.NORMAL_DAY
        ) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                mapView!!.camera.lookAt(
                    GeoCoordinates(52.530932, 13.384915), distanceInMeters
                )
            } else {
                Log.d("AAAAA", "Loading map failed: mapError: " + mapError.name)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        sharedPref?.edit()?.putString("currentTrip", sharedViewModel.selectedTrip.toString())
        mapView.onDestroy()
    }


    private fun showNoTripSelectedDialog() {

        val takeToHomeScreen =
            { requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation }

        CustomDialogBuilder(
            requireContext(),
            "No ongoing trip",
            "No trip was selected. Please select a trip from the menu",
            "Take me to trip list",
            takeToHomeScreen,
            null,
            null,
            false
        ).builder.show()
    }

}
