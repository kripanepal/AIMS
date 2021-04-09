package com.fourofourfound.aims_delivery.deliveryCompletionForm

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryInputFormBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class DeliveryCompletionFragment : androidx.fragment.app.DialogFragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentDeliveryInputFormBinding
    private lateinit var viewModel: DeliveryCompletionViewModel
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Aims_delivery);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (sharedViewModel.selectedSourceOrSite.value == null) {
            findNavController().navigateUp()
            return null
        }


        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_input_form,
            container,
            false
        )

        viewModelFactory = DeliveryCompletionViewModelFactory(
            requireActivity().application,
            sharedViewModel.selectedSourceOrSite.value!!
        )

        //getting a view model from a factory
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DeliveryCompletionViewModel::class.java)
        binding.viewModel = viewModel
        binding.submitBtn.setOnClickListener {
            if (verifyInput())
                showSignatureDialog()
        }

        initializeViewModelVariables()
        setUpDialogActionBar()
        viewDateAndTime()

        return binding.root
    }

    private fun verifyInput(): Boolean {
        viewModel.apply {
            if (billOfLadingNumber == 4444) binding.billOfLading.error =
                "Bill of lading cannot be empty"
            if (productDesc.value!!.isEmpty()) binding.billOfLading.error =
                "Product cannot be empty"
            if (grossQty.value!!.isEmpty()) binding.grossQty.error =
                "Gross quantity cannot be empty"
            if (netQty.value!!.isEmpty()) binding.netQty.error = "Net quantity cannot be empty"
            if (trailerBeginReading.value!!.isEmpty()) binding.trailerBegin.error =
                "Trailer reading  cannot be null"
            if (viewModel.trailerEndReading.value!!.isEmpty()) binding.trailerEnd.error =
                "Product cannot be null"
            if (startDate.timeInMillis > endDate.timeInMillis) showDateTimeError("date")

            if (startTime.timeInMillis > endTime.timeInMillis) showDateTimeError("time")
        }

        return false
    }

    private fun showDateTimeError(error: String) {
        binding.errorText.text = "End $error cannot be greater than start $error"
        binding.errorText.visibility = View.VISIBLE
        binding.formScrollView.scrollTo(0, binding.formScrollView.top)
    }


    private fun initializeViewModelVariables() {
        viewModel.startTime = arguments?.getSerializable("startDateAndTime") as Calendar
        viewModel.endTime = arguments?.getSerializable("endDateAndTime") as Calendar


    }

    private fun viewDateAndTime() {
        getTime(binding.startTime, binding.startTimeContainer, requireContext())
        getTime(binding.endTime, binding.endTimeContainer, requireContext())
        getDate(binding.startDate, binding.startDateContainer, requireContext())
        getDate(binding.endDate, binding.endDateContainer, requireContext())
    }

    private fun setUpDialogActionBar() {
        binding.toolbar.title =
            sharedViewModel.selectedSourceOrSite.value!!.location.destinationName
        binding.toolbar.setNavigationOnClickListener { dialog?.let { dl -> onDismiss(dl) } }
    }


    private fun showSignatureDialog() {
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault)
        builder.setView(R.layout.signature_pad_layout)
        var dialog = builder.create()
        dialog.setTitle("Signature")
        dialog.show()

        var signaturePad = dialog.findViewById<SignaturePad>(R.id.signature_pad)
        dialog.findViewById<Button>(R.id.signature_clear).setOnClickListener {
            signaturePad.clear()
        }
        dialog.findViewById<Button>(R.id.signature_done).setOnClickListener {
            //TODO need to save the captured image bitmap in the database
            val signatureBitMap = signaturePad.signatureBitmap
            dialog.cancel()
            viewModel.submitForm()
            viewModel.updateDeliveryStatus(
                sharedViewModel.selectedTrip.value!!.tripId,
                StatusEnum.COMPLETED
            )
            sharedViewModel.selectedTrip.value!!.sourceOrSite.find { it.status == StatusEnum.ONGOING }?.status =
                StatusEnum.COMPLETED
            findNavController().popBackStack(R.id.ongoingDeliveryFragment, true)
            requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation

            //TODO need to manage this
            sharedViewModel.selectedSourceOrSite.value = null
        }
    }

    private fun getTime(textView: TextView, textInputLayout: TextInputLayout, context: Context) {

        val cal =
            if (textView.id == binding.startTime.id) viewModel.startTime else viewModel.endTime

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        textInputLayout.setEndIconOnClickListener {
            it
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
            false
        }
    }

    private fun getDate(textView: TextView, textInputLayout: TextInputLayout, context: Context) {

        val cal =
            if (textView.id == binding.startDate.id) viewModel.startDate else viewModel.endDate

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            textView.text = SimpleDateFormat("yyyy:MM:dd").format(cal.time)

        }

        textInputLayout.setEndIconOnClickListener {
            DatePickerDialog(
                context,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).also { picker ->
                picker.datePicker.maxDate = System.currentTimeMillis()
                picker.show()
            }
            false
        }
    }





}

