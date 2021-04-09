package com.fourofourfound.aims_delivery.delivery.onGoing


import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.deliveryCompletionForm.DeliveryCompletionFragment
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


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
    lateinit var viewModel: OngoingDeliveryViewModel

    lateinit var currentSourceOrSite: SourceOrSite

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

        if (sharedViewModel.selectedSourceOrSite.value == null) {
            findNavController().navigateUp()
            return null
        }


        //viewModel used by this fragment
        viewModel = ViewModelProvider(this).get(OngoingDeliveryViewModel::class.java)

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        if (sharedViewModel.activeRoute !== null) {
            binding.startNavigation.text =
                "Continue Navigation"
            binding.startFilling.visibility = View.GONE
        }

        binding.startNavigation.setOnClickListener {
            findNavController().navigate(R.id.navigationFragment)
        }

        if (viewModel.fillingStarted) fuelingStartViews()

        return binding.root
    }

    private fun observeStartFueling() {
        binding.startFilling.setOnClickListener {
            viewModel.fillingStarted = true
            showFuelConfirmDialog(requireContext())
        }
    }

    private fun observeEndFueling() {
        binding.endFilling.setOnClickListener {
            viewModel.endDateAndTime = Calendar.getInstance()

            var navigateToForm = {
                var formDialog = DeliveryCompletionFragment();
                val args = Bundle()
                args.putSerializable("startDateAndTime", viewModel.startDateAndTime)
                args.putSerializable("endDateAndTime", viewModel.endDateAndTime)
                args.putParcelable("currentSourceOrSite", currentSourceOrSite)
                formDialog.arguments = args
                formDialog.show(childFragmentManager, "Form")
            }
            CustomDialogBuilder(
                requireContext(),
                "Filling Complete",
                "Fill the form now.",
                "Ok",
                navigateToForm,
                "Cancel",
                null,
                false
            ).builder.show()
        }
    }

    private fun observeDestination() {
        sharedViewModel.selectedSourceOrSite.observe(viewLifecycleOwner)
        {
            it?.apply {
                currentSourceOrSite = it
                binding.sourceOrSite = currentSourceOrSite
                binding.currentTrip = sharedViewModel.selectedTrip.value
                binding.sourceOrSiteInfo.apply {
                    sourceOrSiteName.text = currentSourceOrSite.location.destinationName
                    address.text = currentSourceOrSite.location.address1
                    productDesc.text = currentSourceOrSite.productInfo.productDesc
                    productQty.text =
                        currentSourceOrSite.productInfo.requestedQty.toString() + " " + currentSourceOrSite.productInfo.uom
                }
            }
        }
    }

    private fun showFuelConfirmDialog(context: Context) {
        val input = EditText(context)
        sharedViewModel.selectedSourceOrSite.value?.trailerInfo?.fuelQuantity?.let {
            input.setText(it.toString())
        }
        input.inputType = InputType.TYPE_CLASS_NUMBER
        val alert = AlertDialog.Builder(context)
            .setTitle("Confirm Fuel Quantity")
            .setView(input)
            .setMessage("Please enter/confirm the trailer fuel quantity")
            .setPositiveButton("Submit") { dialog, _ ->
                viewModel.updateFuelInfo(
                    currentSourceOrSite.trailerInfo.trailerId,
                    Integer.parseInt(input.text.toString())
                )
                sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.fuelQuantity =
                    Integer.parseInt(input.text.toString())
                viewModel.startDateAndTime = Calendar.getInstance()
                fuelingStartViews()
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }

    private fun fuelingStartViews() {
        binding.startFilling.visibility = View.GONE
        binding.endFilling.visibility = View.VISIBLE
        binding.startNavigation.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title =
            sharedViewModel.selectedTrip.value!!.tripName

        observeDestination()
        observeStartFueling()
        observeEndFueling()
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
