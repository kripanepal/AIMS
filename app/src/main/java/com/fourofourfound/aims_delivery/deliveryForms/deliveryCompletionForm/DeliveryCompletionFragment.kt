package com.fourofourfound.aims_delivery.deliveryForms.deliveryCompletionForm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryInputFormBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class DeliveryCompletionFragment : Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentDeliveryInputFormBinding
    private lateinit var viewModel: DeliveryCompletionViewModel
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory
    val args by navArgs<DeliveryCompletionFragmentArgs>()
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
        viewDateAndTime()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSpinner()
        viewModel.tripId = sharedViewModel.selectedTrip.value!!.tripId
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeSpinner() {
        viewModel.productList.observe(viewLifecycleOwner) {
            if (it != null) {
                val products = viewModel.productList.value!!.toTypedArray()

                // Initializing an ArrayAdapter
                var adapter = ArrayAdapter(
                    requireContext(), // Context
                    android.R.layout.simple_spinner_item, // Layout
                    products // Array
                )


                // Set the drop down view resource
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                val autoCompleteTextView = binding.productDesc

                // Finally, data bind the spinner object with adapter
                autoCompleteTextView.setAdapter(adapter)
                autoCompleteTextView.threshold = 0
                autoCompleteTextView.setOnTouchListener { _, event ->
                    if (event != null) autoCompleteTextView.showDropDown()
                    false
                }

                binding.productDesc.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.productDesc.value = products[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }
            }
        }

    }

    private fun verifyInput(): Boolean {
        viewModel.apply {
            if (productDesc.value.isNullOrEmpty()) return showGeneralErrors(
                binding.billOfLading,
                "Invalid Product"
            )
            if (grossQty.value!! < 0) return showGeneralErrors(
                binding.grossQty,
                "Invalid Gross quantity"
            )
            if (netQty.value!! < 0) return showGeneralErrors(
                binding.netQty,
                "Invalid Net quantity"
            )
            if (trailerBeginReading.value!! < 0) return showGeneralErrors(
                binding.trailerBegin,
                "Invalid Trailer reading"
            )
            if (viewModel.trailerEndReading.value!! < 0) return showGeneralErrors(
                binding.trailerEnd,
                "Invalid Trailer Reading"
            )
            if (startDate.timeInMillis > endDate.timeInMillis) return showDateTimeError("date")

            if (startTime.timeInMillis > endTime.timeInMillis) return showDateTimeError("time")

            if ((trailerBeginReading.value!! > trailerEndReading.value!!) && sharedViewModel.selectedSourceOrSite.value!!.wayPointTypeDescription == "Source") {
                return showGeneralErrors(
                    binding.trailerEnd,
                    "Begin reading is greater than end reading"
                )
            }

            if ((trailerBeginReading.value!! < trailerEndReading.value!!) && sharedViewModel.selectedSourceOrSite.value!!.wayPointTypeDescription != "Source") {
                return showGeneralErrors(
                    binding.trailerEnd,
                    "End reading is greater than begin reading"
                )
            }
        }

        return true
    }

    fun showGeneralErrors(view: EditText, error: String): Boolean {
        view.error = error
        return false
    }

    private fun showDateTimeError(error: String): Boolean {
        binding.errorText.text = "End $error cannot be greater than start $error"
        binding.errorText.visibility = View.VISIBLE
        binding.formScrollView.scrollTo(0, binding.formScrollView.top)
        return false
    }


    private fun initializeViewModelVariables() {
        viewModel.startTime = args.startDateAndTime
        viewModel.endTime = args.endDateAndTime


    }

    private fun viewDateAndTime() {
        getTime(binding.startTime, binding.startTimeContainer, requireContext())
        getTime(binding.endTime, binding.endTimeContainer, requireContext())
        getDate(binding.startDate, binding.startDateContainer, requireContext())
        getDate(binding.endDate, binding.endDateContainer, requireContext())
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

        //TODO TO REMOVE

        dialog.findViewById<Button>(R.id.signature_done).setOnClickListener {

            var beta = { //TODO need to save the captured image bitmap in the database
                val signatureBitMap = signaturePad.signatureBitmap
                dialog.cancel()
                viewModel.submitForm()
                viewModel.updateDeliveryStatus(
                    sharedViewModel.selectedTrip.value!!.tripId,
                    StatusEnum.COMPLETED
                )
                sharedViewModel.selectedTrip.value!!.sourceOrSite.find { it.status == StatusEnum.ONGOING }?.status =
                    StatusEnum.COMPLETED
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
                dialog.dismiss()
                findNavController().navigateUp()
                //TODO need to manage this
                sharedViewModel.selectedSourceOrSite.value = null
            }

            var time = Calendar.getInstance()

            CustomDialogBuilder(
                requireContext(),
                "Sending Product Picked/Delivered Info",
                String.format(
                    "Time Stamp: %d-%d-%d %d:%d " +
                            "\nDriver ID: %s " +
                            "\nTrip ID: %d " +
                            "\nDestination ID: %d " +
                            "\nProduct ID: %s " +
                            "\nStart Time: %d:%d " +
                            "\nEnd Time: %d:%d " +
                            "\nGross Qty: %d " +
                            "\nNet Qty: %d",
                    time.get(Calendar.YEAR),
                    time.get(Calendar.MONTH),
                    time.get(Calendar.DAY_OF_MONTH),
                    time.get(Calendar.HOUR_OF_DAY),
                    time.get(Calendar.MINUTE),
                    sharedViewModel.driver.driver_id,
                    sharedViewModel.selectedTrip.value!!.tripId,
                    viewModel.destination.seqNum,
                    viewModel.destination.productInfo.productId,
                    viewModel.startTime.get(Calendar.HOUR_OF_DAY),
                    viewModel.startTime.get(Calendar.MINUTE),
                    viewModel.endTime.get(Calendar.HOUR_OF_DAY),
                    viewModel.endTime.get(Calendar.MINUTE),
                    viewModel.grossQty.value,
                    viewModel.netQty.value
                ),
                "OK",
                beta,
                null,
                null,
                false
            ).builder.show()
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

