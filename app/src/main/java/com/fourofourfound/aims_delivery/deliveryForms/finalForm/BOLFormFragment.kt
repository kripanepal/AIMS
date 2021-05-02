package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.hideSoftKeyboard
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.*
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryInputFormBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.senab.photoview.PhotoViewAttacher
import java.text.SimpleDateFormat
import java.util.*


class BOLFormFragment : androidx.fragment.app.Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var binding: FragmentDeliveryInputFormBinding
    lateinit var viewModel: DeliveryCompletionViewModel
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory
    private val args by navArgs<BOLFormFragmentArgs>()
    lateinit var getImageContent: ActivityResultLauncher<Intent>
    private val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()
    var currentPhotoPath: String = ""
    lateinit var billOfLadingAdapter: BillOfLadingAdapter

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

        binding.uploadImageBtn.setOnClickListener {

            if (checkPermission(
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    requireContext()
                )
            ) {
                openCamera()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please provide storage permissions ",
                    Toast.LENGTH_SHORT
                ).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(), 10
                )
            }
        }

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        getImageContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.data?.apply {
                    currentPhotoPath = getRealPathFromURI(requireContext(), this)
                }
                if (currentPhotoPath.isNotEmpty() && getBitMapFromFilePath(
                        requireContext(),
                        currentPhotoPath
                    ) != null
                ) {
                    changeImagePaths(currentPhotoPath)
                }
                currentPhotoPath = ""
            }
    }

    private fun changeImagePaths(source: String) {
        if (viewModel.imagePaths.value.isNullOrEmpty())
            viewModel.imagePaths.value = mutableListOf(source)
        else
            viewModel.imagePaths.value =
                viewModel.imagePaths.value?.plus(
                    mutableListOf(
                        source
                    )
                ) as MutableList<String>?
        binding.billOfLadingImages.post { binding.billOfLadingImages.scrollToPosition(binding.billOfLadingImages.adapter!!.itemCount - 1) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSpinner()
        viewModel.tripId = sharedViewModel.selectedTrip.value!!.tripId
        setupImageRecyclerView()
        binding.formParentView.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }

        viewModel.formSubmitted.observe(viewLifecycleOwner)
        { status ->
            if (status) {

                findNavController().navigate(
                    BOLFormFragmentDirections.actionDeliveryCompletionFragmentToOngoingDeliveryFragment()
                )
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation

                //TODO need to manage this
                sharedViewModel.selectedSourceOrSite.value = null
            }
        }
    }

    private fun setupImageRecyclerView() {
        billOfLadingAdapter = BillOfLadingAdapter(
            BitmapListListener(
                { imagePath ->
                    val removeImage = {
                        viewModel.imagePaths.value = viewModel.imagePaths.value?.filter {
                            it != imagePath
                        } as MutableList<String>?

                    }
                    CustomDialogBuilder(
                        requireContext(),
                        "Delete",
                        "Do your want to remove this picture",
                        "Yes",
                        removeImage,
                        "No",
                        null,
                        false
                    ).builder.show()

                }) { imageBitMap ->


                var alertDialog =
                    AlertDialog.Builder(
                        context,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen
                    )
                alertDialog.setView(R.layout.each_image_view)
                var dialog = alertDialog.create()
                dialog.show()
                dialog.findViewById<ImageView>(R.id.image_to_display).apply {
                    setImageBitmap(imageBitMap)
                    PhotoViewAttacher(this).update()
                }

            }, requireContext()
        )


        binding.billOfLadingImages.adapter = billOfLadingAdapter
        observeImages()

        binding.billOfLadingImages.adapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.billOfLadingImages.scrollToPosition(binding.billOfLadingImages.adapter!!.itemCount - 1)
            }
        })
    }


    private fun observeImages() {
        viewModel.imagePaths.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.apply {
                billOfLadingAdapter.submitList(this)
                binding.formScrollView.post {
                    binding.formScrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeSpinner() {
        viewModel.productList.observe(viewLifecycleOwner) {
            if (it != null) {
                val products = viewModel.productList.value!!.toTypedArray()

                // Initializing an ArrayAdapter
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.drop_down_product,
                    R.id.dropdown_menu_item, products
                )

                // Set the drop down view resource
                adapter.setDropDownViewResource(R.layout.drop_down_product)
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

    private fun showGeneralErrors(view: EditText, error: String): Boolean {
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
        viewModel.trailerBeginReading.value = args.trailerBeginReading
        viewModel.trailerEndReading.value = args.trailerEndReading
        viewModel.meterReadingBefore.value = args.meterBeginReading
        viewModel.meterReadingAfter.value = args.meterEndReading
        viewModel.stickReadingBefore.value = args.stickBeginReading
        viewModel.stickReadingAfter.value = args.stickEndReading

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
                    sharedViewModel.driver!!.code,
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
                { //TODO need to save the captured image bitmap in the database
                    val signatureBitMap = signaturePad.signatureBitmap
                    dialog.cancel()
                    deliveryStatusViewModel.previousDestination =
                        sharedViewModel.selectedSourceOrSite.value!!.location
                    viewModel.submitForm()
                    viewModel.updateDeliveryStatus(
                        sharedViewModel.selectedTrip.value!!.tripId,
                        StatusEnum.COMPLETED
                    )
                    sharedViewModel.selectedTrip.value!!.sourceOrSite.find { it.status == StatusEnum.ONGOING }!!.status =
                        StatusEnum.COMPLETED

                },
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
            if (textView.id == binding.startDate.id) viewModel.startTime else viewModel.endTime
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            textView.text = SimpleDateFormat("yyyy:MM:dd", Locale.US).format(cal.time)

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
        }
    }


}
