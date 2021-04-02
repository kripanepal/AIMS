package com.fourofourfound.aims_delivery.deliveryCompletionForm

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.DeliveryInputFormBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.activity_main.*

class DeliveryCompletionFragment : Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: DeliveryInputFormBinding
    private lateinit var viewModel: DeliveryCompletionViewModel
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (sharedViewModel.selectedSourceOrSite.value == null) {
            findNavController().navigateUp()
            return null
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.delivery_input_form, container, false)
        viewModelFactory = DeliveryCompletionViewModelFactory(
            requireActivity().application,
            sharedViewModel.selectedSourceOrSite.value!!
        )

        //getting a view model from a factory
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DeliveryCompletionViewModel::class.java)
        binding.viewModel = viewModel
        binding.submitBtn.setOnClickListener {
            viewModel.submitForm()
        }

        viewModel.doneFilling.observe(viewLifecycleOwner)
        {
            if (it) showSignatureDialog()
        }
        return binding.root
    }

    private fun showSignatureDialog() {
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault)
        builder.setView(R.layout.signature_pad_layout)
        var dialog = builder.create()
        dialog.setTitle("Signature")
        dialog.setMessage("Sign below ")
        dialog.show()


        var signaturePad = dialog.findViewById<SignaturePad>(R.id.signature_pad)

        dialog.findViewById<Button>(R.id.signature_clear).setOnClickListener {
            signaturePad.clear()
        }
        dialog.findViewById<Button>(R.id.signature_done).setOnClickListener {
            //TODO need to save the captured image bitmap in the database
            val signatureBitMap = signaturePad.signatureBitmap
            dialog.cancel()
            findNavController().popBackStack(R.id.ongoingDeliveryFragment, false);
            requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
            viewModel.doneNavigating()
            viewModel.markDeliveryCompleted(sharedViewModel.selectedTrip.value!!.tripId)
            //TODO need to manage this
            sharedViewModel.selectedSourceOrSite.value!!.status = StatusEnum.COMPLETED
            sharedViewModel.selectedSourceOrSite.value = null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeliveryCompletionViewModel::class.java)
    }

}