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
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding
import com.here.android.mpa.mapping.Map
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Ongoing delivery fragment
 *This fragment is responsible for displaying the data related to
 * deliveries that are being delivered
 * @constructor Create empty Ongoing delivery fragment
 */
class OngoingDeliveryFragment : Fragment() {
    /**
     * _binding
     * The binding object that is used by this fragment
     */
    private var _binding: FragmentDeliveryOngoingBinding? = null

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
    private var m_map: Map? = null


    /**
     * Shared pref this is used to store key value pair to the file system
     */
    private val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)


    /**
     * On create view
     * @param inflater the inflator used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("AAAAAAAA", "create view")

        //return if no this is not an ongoing delivery
        if (sharedViewModel.selectedTrip.value == null) {
            showNoTripSelectedDialog()
            return view
        }

        //inflate the layout and initialize the binding object
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_ongoing,
            container,
            false
        )

        //viewModel used by this fragment
        val viewModel = ViewModelProvider(this).get(OngoingDeliveryViewModel::class.java)

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //sets current trip to trip from the sharedViewModel
        sharedViewModel.selectedTrip.observe(viewLifecycleOwner)
        {
            it?.let { viewModel.setCurrentTrip(sharedViewModel.selectedTrip.value!!) }
        }

        observeTripCompletion(viewModel)

        binding.startNavigation.setOnClickListener {
            findNavController().navigate(R.id.navigationFragment)
        }



        return binding.root
    }


    /**
     * Observe trip completion
     * observe the viewModel to know when the trip is completed
     * @param viewModel the view Model where the information about the trip is present
     */
    private fun observeTripCompletion(viewModel: OngoingDeliveryViewModel) {
        viewModel.tripCompleted.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
                sharedViewModel.setSelectedTrip(null)
                viewModel.doneNavigatingToHomePage()
            }
        }
    }


    override fun onDestroy() {
        // Log.i("AAAAAA", "destroy")
        super.onDestroy()
        //save the ongoing trip info to sharedPreferences
        sharedPref?.edit()?.putString("currentTrip", sharedViewModel.selectedTrip.toString())
            ?.apply()
    }

    /**
     * Show no trip selected dialog
     *Returns an AlertDialog object to inform that no Ongoing Trip is present
     */
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
