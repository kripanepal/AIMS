package com.fourofourfound.aims_delivery.delivery.onGoing


import android.os.Bundle
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
import com.fourofourfound.aims_delivery.utils.getTripCompletedDialogBox
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding
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

        //return if no this is not an ongoing delivery
        if (sharedViewModel.selectedTrip.value == null ) {
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

        if(sharedViewModel.selectedSourceOrSite.value == null)
        {
            findNavController().navigateUp()
            return null
        }


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

        if (sharedViewModel.activeRoute !== null) binding.startNavigation.text =
            "Continue Navigation"

        binding.startNavigation.setOnClickListener {
            findNavController().navigate(R.id.navigationFragment)
        }



        var currentSourceOrSite = sharedViewModel.selectedSourceOrSite.value!!
        binding.sourceOrSite = currentSourceOrSite
        binding.currentTrip = sharedViewModel.selectedTrip.value
        binding.sourceOrSiteInfo.apply {

            sourceOrSiteName.text = currentSourceOrSite.location.destinationName
            address.text = currentSourceOrSite.location.address1
            productDesc.text = currentSourceOrSite.productInfo.productDesc
            productQty.text =
                currentSourceOrSite.productInfo.requestedQty.toString() + " " + currentSourceOrSite.productInfo.uom

        }


        binding.deliveryCompletedButton.setOnClickListener {
            var navigateToForm = {
                findNavController().navigate(
                    OngoingDeliveryFragmentDirections.actionOngoingDeliveryFragmentToDeliveryCompletionFragment(
                        currentSourceOrSite
                    )
                )
            }
            getTripCompletedDialogBox(requireContext(), navigateToForm).show()
        }

        return binding.root
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
