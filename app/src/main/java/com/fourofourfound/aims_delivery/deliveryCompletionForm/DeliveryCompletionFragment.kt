package com.fourofourfound.aims_delivery.deliveryCompletionForm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.DeliveryInputFormBinding

class DeliveryCompletionFragment : Fragment() {

    private lateinit var binding: DeliveryInputFormBinding

    private lateinit var viewModel: DeliveryCompletionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.delivery_input_form, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeliveryCompletionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}