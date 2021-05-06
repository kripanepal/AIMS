package com.fourofourfound.aims_delivery.deliveryForms.prePostCompletion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.fourofourfound.aims_delivery.delivery.onGoing.OngoingDeliveryViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentReadingPreFillingBinding

/**
 * Reading pre post filling
 * This class is responsible for showing a dialog to get information about pre and post filling.
 * @constructor Create empty Reading pre post filling
 */
class ReadingPrePostFilling : DialogFragment() {
    /**
     * Parent view model
     * The view model which holds the data of this fragment.
     */
    private val parentViewModel by viewModels<OngoingDeliveryViewModel>({ requireParentFragment() })

    /**
     * Binding
     * The binding object that is used by this fragment
     */
    lateinit var binding: FragmentReadingPreFillingBinding

    /**
     * Is site
     * The flag to check if the destination is source or site.
     */
    private var isSite = false

    /**
     * Is filling
     * The flag to check if the filling has started or not.
     */
    var isFilling = true

    /**
     * Trailer reading
     * The trailer reading value for a delivery.
     */
    var trailerReading = 0


    /**
     * On create view
     * It is called when the fragment is created.
     * @param inflater the inflater used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reading_pre_filling,
            container,
            false
        )
        isSite = arguments?.getBoolean("isSite") ?: false
        isFilling = arguments?.getBoolean("isFilling") ?: false
        trailerReading = arguments?.getInt("trailerReading") ?: 0
        val requiredQuantity = arguments?.getInt("requiredQuantity") ?: 0
        trailerReading =
            if (isSite) trailerReading - requiredQuantity else trailerReading + requiredQuantity
        binding.isSite = isSite
        binding.trailerReading.setText(trailerReading.toString())
        binding.trailerDetail.text = arguments?.getString("trailer") ?: ""
        binding.info.text =
            if (isFilling) "Fill in the info Before filling the fuel" else "Fill in the info After filling the fuel"
        return binding.root
    }

    /**
     * On view created
     * This method is called when the view is created
     * @param view the view that is created
     * @param savedInstanceState called when fragment is started
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnClickListener {
            val meterReading = binding.meterReading.text.toString()
            val stickReading = binding.stickReading.text.toString()
            if (binding.trailerReading.text.toString().isBlank()) binding.trailerReading.error =
                "This field is required"
            else if (meterReading.isNotBlank() && meterReading.toInt() < 0) binding.meterReading.error =
                "Invalid fuel amount"
            else if (stickReading.isNotBlank() && stickReading.toInt() < 0) binding.meterReading.error =
                "Invalid fuel amount"
            else {
                val trailerReading = binding.trailerReading.text.toString().toDouble()
                val meterReading = binding.meterReading.text.toString().toDoubleOrNull()
                val stickReading = binding.stickReading.text.toString().toDoubleOrNull()
                if(isFilling)
                {
                    parentViewModel.trailerReadingBegin.value = trailerReading
                    parentViewModel.meterReadingBegin.value = meterReading
                    parentViewModel.stickReadingBegin.value = stickReading
                    parentViewModel.fillingStarted.value = true
                }
                else
                {
                    parentViewModel.trailerReadingEnd.value = trailerReading
                    parentViewModel.meterReadingEnd.value = meterReading
                    parentViewModel.stickReadingEnd.value = stickReading
                    parentViewModel.fillingEnded.value = true
                }
                dialog?.cancel()
            }
        }
        binding.cancelBtn.setOnClickListener {
            dialog!!.cancel()
        }
    }
}