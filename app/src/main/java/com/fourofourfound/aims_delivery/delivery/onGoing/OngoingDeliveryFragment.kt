package com.fourofourfound.aims_delivery.delivery.onGoing


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.deliveryForms.finalForm.DeliveryCompletionViewModel
import com.fourofourfound.aims_delivery.deliveryForms.finalForm.DeliveryCompletionViewModelFactory
import com.fourofourfound.aims_delivery.deliveryForms.prePostCompletion.ReadingPrePostFilling
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.htmlToText
import com.fourofourfound.aims_delivery.utils.showUserNotClockedInMessage
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryOngoingBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.source_or_site_info.*
import kotlinx.android.synthetic.main.source_or_site_info.view.*
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
    lateinit var viewModel:OngoingDeliveryViewModel
    private lateinit var currentSourceOrSite: SourceOrSite


    /**
     * On create view
     * @param inflater the inflator used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (sharedViewModel.selectedTrip.value == null || sharedViewModel.selectedSourceOrSite.value == null) {
            var goback = inflater.inflate(R.layout.missing_trip_or_destination, container, false)
            goback.findViewById<Button>(R.id.back_to_homepage).setOnClickListener {
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
            }
            return goback
        }


        //inflate the layout and initialize the binding object
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_ongoing,
            container,
            false
        )

        val viewModelFactory = OnGoingDeliveryViewModelFactory(
            requireActivity().application,
            sharedViewModel.selectedSourceOrSite.value!!
        )

        //getting a view model from a factory
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(OngoingDeliveryViewModel::class.java)
        viewModel.trailerReading.observe(viewLifecycleOwner){}

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        currentSourceOrSite = sharedViewModel.selectedSourceOrSite.value!!
        sharedViewModel.activeRoute?.apply {
            binding.startFilling.visibility = View.GONE
            binding.directions.text = "Continue Navigation"
        }

        if(!sharedViewModel.userClockedIn.value!!)  Snackbar.make(binding.root, "Please clock in to continue", Snackbar.LENGTH_LONG)
            .setAction("Clock In") { requireActivity().bottom_navigation.selectedItemId = R.id.settings_navigation }
            .show()


        binding.startNavigation.setOnClickListener {
            if(showUserNotClockedInMessage(sharedViewModel,binding.root,requireActivity()))
            else findNavController().navigate(OngoingDeliveryFragmentDirections.actionOngoingDeliveryFragmentToNavigationFragment())
        }

        viewModel.fillingEnded.observe(viewLifecycleOwner)
        {
            if (it) {
                fuelingEndViews()

            }
        }

        viewModel.getDatabase()
        return binding.root
    }


    /**
     * Observe start fueling
     * This method observes if the fuel filling is started or not
     */
    private fun observeStartFueling() {
        binding.startFilling.setOnClickListener {
            if(showUserNotClockedInMessage(sharedViewModel,binding.root,requireActivity()))
            else {
                val preFillingDialog = ReadingPrePostFilling()
                val args = Bundle()
                args.putBoolean("isFilling", true)
                args.putString(
                    "trailer",
                    sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.trailerDesc
                )
                args.putBoolean("isSite", currentSourceOrSite.wayPointTypeDescription != "Source")
                viewModel.trailerReading.value?.let { it1 -> args.putInt("trailerReading", it1) }
                preFillingDialog.arguments = args
                preFillingDialog.show(childFragmentManager, "PreFillingReadings")
            }
        }

        viewModel.fillingStarted.observe(viewLifecycleOwner)
        {
            if(it) {
                viewModel.updateFuelInfo(
                    currentSourceOrSite.trailerInfo.trailerId,
                    viewModel.trailerReadingBegin.value!!
                )
                sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.fuelQuantity =
                    viewModel.trailerReadingBegin.value!!.toDouble()
                viewModel.startDateAndTime = Calendar.getInstance()
                fuelingStartViews()
            }

        }
    }

    /**
     * Observe end fueling
     * This method observes if the fuel filling is ended or not
     */
    private fun observeEndFueling() {
        binding.endFilling.setOnClickListener {
            if(showUserNotClockedInMessage(sharedViewModel,binding.root,requireActivity()))
            else {
                viewModel.endDateAndTime = Calendar.getInstance()

                val preFillingDialog = ReadingPrePostFilling()

                val args = Bundle()
                args.putBoolean("isFilling", false)
                args.putString(
                    "trailer",
                    sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.trailerDesc
                )
                args.putBoolean("isSite", currentSourceOrSite.wayPointTypeDescription != "Source")
                args.putInt("requiredQuantity", currentSourceOrSite.productInfo.requestedQty!!)

                viewModel.trailerReading.value?.let { it1 -> args.putInt("trailerReading", it1) }
                preFillingDialog.arguments = args
                preFillingDialog.show(childFragmentManager, "PostFillingReadings")
            }

        }
    }

    /**
     * Get form confirmation
     * This method records the time and fuel reading and navigates to the form page
     * @return a dialog
     */
    private fun getFormConfirmation(): CustomDialogBuilder {
        return CustomDialogBuilder(
            requireContext(),
            "Filling Complete",
            "Fill the form now.",
            "Ok",
            {
                findNavController().navigate(
                    OngoingDeliveryFragmentDirections.actionOngoingDeliveryFragmentToDeliveryCompletionFragment(
                        currentSourceOrSite,
                        viewModel.startDateAndTime,
                        viewModel.endDateAndTime,
                        viewModel.trailerReadingBegin.value!!,
                        viewModel.trailerReadingEnd.value!!,
                        viewModel.meterReadingBegin.value,
                        viewModel.meterReadingEnd.value,
                        viewModel.stickReadingBegin.value,
                        viewModel.stickReadingEnd.value
                    )
                )

            },
            "Cancel",
            null,
            false
        )
    }

    /**
     * Observe destination
     *
     */
    private fun observeDestination() {
        sharedViewModel.selectedSourceOrSite.observe(viewLifecycleOwner)
        {
            it?.apply {
                currentSourceOrSite = it
                binding.sourceOrSite = currentSourceOrSite
                binding.currentTrip = sharedViewModel.selectedTrip.value
                binding.sourceOrSiteInfo.apply {
                    sourceOrSiteName.text = currentSourceOrSite.location.destinationName
                    var fullAddress =
                        "${currentSourceOrSite.location.address1.trim()}, ${currentSourceOrSite.location.city.trim()}, ${currentSourceOrSite.location.stateAbbrev.trim()} ${currentSourceOrSite.location.postalCode}"
                    address.text = fullAddress
                    productDesc.text = currentSourceOrSite.productInfo.productDesc
                    var productQtyText =  currentSourceOrSite.productInfo.requestedQty.toString() + " " + currentSourceOrSite.productInfo.uom
                    productQty.text =productQtyText

                    truck_text.text = currentSourceOrSite.truckInfo.truckDesc
                    trailer_text.text = currentSourceOrSite.trailerInfo.trailerDesc
                }
                if (it.wayPointTypeDescription == "Source") binding.destinationImage.setImageResource(
                    R.drawable.ic_source
                )
                else {
                    binding.siteInfo.visibility = View.VISIBLE
                    binding.destinationImage.setImageResource(R.drawable.ic_site)
                    val containerInfo =
                        htmlToText("<b>Container</b>: " + currentSourceOrSite.siteContainerCode)
                    binding.siteContainer.text = containerInfo

                    val  containerDesc =
                        htmlToText("<b>Container Desc</b>: " + currentSourceOrSite.siteContainerDescription)
                    binding.siteContainerDescription.text = containerDesc

                    val notes = htmlToText("<b>Notes</b>: " + currentSourceOrSite.productInfo.fill)
                    binding.loadNotes.text = notes
                }
            }
        }
    }



    private fun fuelingStartViews() {
        binding.startFilling.visibility = View.GONE
        binding.endFilling.visibility = View.VISIBLE
        binding.startNavigation.visibility = View.GONE
    }

    private fun fuelingEndViews()
    {
        binding.fillForm.visibility = View.VISIBLE
        binding.endFilling.visibility = View.GONE
        binding.fillForm.setOnClickListener { if(showUserNotClockedInMessage(sharedViewModel,binding.root,requireActivity()))
        else getFormConfirmation().builder.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedViewModel.selectedTrip.value?.apply {
            sharedViewModel.selectedSourceOrSite.value?.apply {

            (activity as AppCompatActivity).supportActionBar?.title =
                    sharedViewModel.selectedTrip.value!!.tripName

                sharedViewModel.selectedSourceOrSite.value!!.also {
                    if(it != currentSourceOrSite)
                    {
                        viewModel.fillingEnded.value=false
                        viewModel.fillingStarted.value=false
                    }
                }
                if(viewModel.fillingEnded.value!!) fuelingEndViews() else observeEndFueling()
                if(viewModel.fillingStarted.value!!) fuelingStartViews() else observeStartFueling()
                observeDestination()
            }
        }
    }
}
