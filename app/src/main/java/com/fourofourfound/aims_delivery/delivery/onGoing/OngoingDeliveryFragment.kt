package com.fourofourfound.aims_delivery.delivery.onGoing


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.deliveryForms.prePostCompletion.ReadingPrePostFilling
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.htmlToText
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
    val viewModel: OngoingDeliveryViewModel by viewModels()
    lateinit var currentSourceOrSite: SourceOrSite


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

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        currentSourceOrSite = sharedViewModel.selectedSourceOrSite.value!!
        sharedViewModel.activeRoute?.apply {
            binding.startFilling.visibility = View.GONE
        }

        binding.startNavigation.setOnClickListener {
            findNavController().navigate(R.id.navigationFragment)
        }

        viewModel.fillingEnded.observe(viewLifecycleOwner)
        {
            if (it) {
                fuelingEndViews()

            }
        }

        return binding.root
    }


    private fun observeStartFueling() {
        binding.startFilling.setOnClickListener {

            val  preFillingDialog = ReadingPrePostFilling()

            val args = Bundle()
            args.putBoolean("isFilling", true)
            args.putString("trailer", sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.trailerDesc)
            args.putBoolean("isSite", currentSourceOrSite.wayPointTypeDescription != "Source")
            preFillingDialog.arguments = args
            preFillingDialog.show(childFragmentManager, "PreFillingReadings")
        }

        viewModel.fillingStarted.observe(viewLifecycleOwner)
        {
            if(it)
            {
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

    private fun observeEndFueling() {
        binding.endFilling.setOnClickListener {
            viewModel.endDateAndTime = Calendar.getInstance()

            val  preFillingDialog = ReadingPrePostFilling()

            val args = Bundle()
            args.putBoolean("isFilling", false)
            args.putString("trailer", sharedViewModel.selectedSourceOrSite.value!!.trailerInfo.trailerDesc)
            args.putBoolean("isSite", currentSourceOrSite.wayPointTypeDescription != "Source")
            preFillingDialog.arguments = args
            preFillingDialog.show(childFragmentManager, "PostFillingReadings")

        }
    }

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
                if (it.wayPointTypeDescription == "Source") binding.destinationImage.setImageResource(
                    R.drawable.ic_source
                )
                else {
                    binding.destinationImage.setImageResource(R.drawable.ic_site)
                    val containerInfo =
                        htmlToText("<b>Container</b>: " + currentSourceOrSite.siteContainerDescription)
                    binding.siteContainer.text = containerInfo

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
        binding.fillForm.setOnClickListener {
            getFormConfirmation().builder.show()
        }
        binding.fillForm.performClick()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectedTrip.value?.apply {
            sharedViewModel.selectedSourceOrSite.value?.apply {

            (activity as AppCompatActivity).supportActionBar?.title =
                    sharedViewModel.selectedTrip.value!!.tripName

                viewModel.destination?.also {
                    if(it != currentSourceOrSite)
                    {
                        viewModel.fillingEnded.value=false
                        viewModel.fillingStarted.value=false
                    }
                }
                viewModel.destination = currentSourceOrSite
                if(viewModel.fillingEnded.value!!) fuelingEndViews() else observeEndFueling()
                if(viewModel.fillingStarted.value!!) fuelingStartViews() else observeStartFueling()
                observeDestination()
            }
        }
    }
}
