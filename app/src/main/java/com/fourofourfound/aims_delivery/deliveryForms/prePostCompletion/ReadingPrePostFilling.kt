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


class ReadingPrePostFilling : DialogFragment() {
    private val parentViewModel by viewModels<OngoingDeliveryViewModel>({requireParentFragment()})
    lateinit var binding : FragmentReadingPreFillingBinding
    private var isSite = false
    var isFilling = true



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reading_pre_filling,container,false)
        isSite = arguments?.getBoolean("isSite") ?:false
        isFilling = arguments?.getBoolean("isFilling") ?: false
        binding.isSite = isSite
        binding.trailerDetail.text = arguments?.getString("trailer") ?: ""
        binding.info.text = if(isFilling)"Fill in the info Before filling the fuel" else "Fill in the info After filling the fuel"

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnClickListener {
            if(binding.trailerReading.text.toString().isNullOrBlank()) binding.trailerReading.error = "This field is required"
            else{
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