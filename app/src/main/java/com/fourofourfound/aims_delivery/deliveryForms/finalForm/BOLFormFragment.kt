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
import kotlin.math.abs
import kotlin.properties.Delegates


class BOLFormFragment : androidx.fragment.app.Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /**
     * Binding
     * The binding object that is used by this fragment
     */
    lateinit var binding: FragmentDeliveryInputFormBinding

    /**
     * View model
     * View model to hold the data data of this fragment.
     */
    lateinit var viewModel: DeliveryCompletionViewModel

    /**
     * View model factory
     * Factory responsible for creating a view model.
     */
    private lateinit var viewModelFactory: DeliveryCompletionViewModelFactory

    /**
     * Args
     * The arguments for the bol form fragment.
     */
    private val args by navArgs<BOLFormFragmentArgs>()

    /**
     * Get image content
     * Activity Result launcher responsible for opening camera or gallery.
     */
    lateinit var getImageContent: ActivityResultLauncher<Intent>

    /**
     * Delivery status view model
     * The view model responsible for holding the data for this fragment.
     */
    private val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()

    /**
     * Current photo path
     * The current photo path of the bol images.
     */
    var currentPhotoPath: String = ""

    /**
     * Bill of lading adapter
     * The adapter responsible for showing the data in the recycler view.
     */
    lateinit var billOfLadingAdapter: BillOfLadingAdapter

    /**
     * Error margin
     * The accepted error margin.
     */
    private var errorMargin by Delegates.notNull<Double>()

    /**
     * On create view
     * This method initializes the fragment.
     * @param inflater the inflater that is used to inflate the view
     * @param container the container that holds the fragment
     * @param savedInstanceState called when fragment is starting
     * @return the view that is inflated
     */
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
        errorMargin = 1 - (requireContext().getString(R.string.net_gross_margin_error).toInt()
            .toDouble() / 100)

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

    /**
     * On attach
     * Called when fragment is attached to the activity
     * @param context the current context of the application
     */
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

    /**
     * Change image paths
     * This method sets the value of live data when the new images are added or removed.
     * @param source
     */
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

    /**
     * On view created
     * This method is called when the view is created
     * @param view the view that is created
     * @param savedInstanceState called when fragment is started
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSpinner()
        viewModel.tripId = sharedViewModel.selectedTrip.value!!.tripId
        viewModel.driver = sharedViewModel.driver!!.code
        setupImageRecyclerView()
        binding.formParentView.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        viewModel.navigate.observe(viewLifecycleOwner)
        { status ->
            if (status) {
                findNavController().navigate(BOLFormFragmentDirections.actionDeliveryCompletionFragmentToOngoingDeliveryFragment())
                requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
                sharedViewModel.selectedSourceOrSite.value = null
            }
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            sharedViewModel.loading.value = it
        }
    }

    /**
     * Setup image recycler view
     * This method sets up the recycler view.
     */
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

                val alertDialog =
                    AlertDialog.Builder(
                        context,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen
                    )
                alertDialog.setView(R.layout.each_image_view)
                val dialog = alertDialog.create()
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

            /**
             * On item range inserted
             * This method scrolls to the end of the recycler view when the new image is added.
             * @param positionStart the start position
             * @param itemCount the number of images
             */
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.billOfLadingImages.scrollToPosition(binding.billOfLadingImages.adapter!!.itemCount - 1)
            }
        })
    }

    /**
     * Observe images
     * This method observes the images for the change and updates the adapter.
     */
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

    /**
     * Initialize spinner
     * This method initialized the drop down for the product information.
     */
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

                        /**
                         * On item selected
                         * Callback method to be invoked when an item in this view has been
                         * selected.
                         * @param parent The AdapterView where the selection happened
                         * @param view The view within the AdapterView that was clicked
                         * @param position The position of the view in the adapter
                         * @param id The row id of the item that is selected
                         */
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

    /**
     * Verify input
     * This method checks if the user input is valid or not.
     * @return true if valid, false otherwise
     */
    private fun verifyInput(): Boolean {
        var netGrossMarginError =
            (requireContext().getString(R.string.net_gross_margin_error).toInt().toDouble() / 100)
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
            if (trailerEndReading.value!! < 0) return showGeneralErrors(
                binding.trailerEnd,
                "Invalid Trailer Reading"
            )

            if (startTime.timeInMillis > endTime.timeInMillis) return showErrorMessage("End time cannot be greater than start time")
            if (netQty.value!! < abs(errorMargin * (trailerEndReading.value!! - trailerBeginReading.value!!))) return showErrorMessage(
                "Difference too large for net quantity and trailer readings"
            )
            if (netQty.value!! < (1 - netGrossMarginError) * grossQty.value!! || netQty.value!! > (1 + netGrossMarginError) * grossQty.value!!) return showErrorMessage(
                "Difference too large for net quantity and gross quantity"
            )
            if (sharedViewModel.selectedSourceOrSite.value!!.wayPointTypeDescription == "Source") {
                if (trailerBeginReading.value!! > trailerEndReading.value!!) {
                    return showGeneralErrors(
                        binding.trailerEnd,
                        "Begin reading is greater than end reading"
                    )
                }
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

    /**
     * Show general errors
     * This method shows if any error in the view.
     * @param view the view that holds the data
     * @param error the error message
     * @return false
     */
    private fun showGeneralErrors(view: EditText, error: String): Boolean {
        view.error = error
        binding.errorText.text = ""
        return false
    }

    /**
     * Show error message
     * This method shows the error message if any
     * @param message the error message
     * @return false
     */
    private fun showErrorMessage(message: String): Boolean {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
        binding.formScrollView.scrollTo(0, binding.formScrollView.top)
        return false
    }

    /**
     * Initialize view model variables
     * This method initializes view model variables.
     */
    private fun initializeViewModelVariables() {
        viewModel.startTime = args.startDateAndTime
        viewModel.endTime = args.endDateAndTime
        viewModel.trailerBeginReading.value = args.trailerBeginReading
        viewModel.trailerEndReading.value = args.trailerEndReading
        viewModel.meterReadingBefore.value = args.meterBeginReading
        viewModel.meterReadingAfter.value = args.meterEndReading
        viewModel.stickReadingBefore.value = args.stickBeginReading
        viewModel.stickReadingAfter.value = args.stickEndReading
        sharedViewModel.selectedSourceOrSite.value?.apply {
            if (wayPointTypeDescription == "Source")
                viewModel.netQty.value =
                    args.trailerEndReading.toInt() - args.trailerBeginReading.toInt()
            else viewModel.netQty.value =
                args.trailerBeginReading.toInt() - args.trailerEndReading.toInt()
        }
        viewModel.grossQty.value = viewModel.netQty.value
    }

    /**
     * View date and time
     * This method is responsible of showing data and time in the view.
     */
    private fun viewDateAndTime() {
        getTime(binding.startTime, binding.startTimeContainer, requireContext())
        getTime(binding.endTime, binding.endTimeContainer, requireContext())
        getDate(binding.startDate, binding.startDateContainer, requireContext())
        getDate(binding.endDate, binding.endDateContainer, requireContext())
    }

    /**
     * Show signature dialog
     * This method shows the signature dialog.
     */
    private fun showSignatureDialog() {
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault)
        builder.setView(R.layout.signature_pad_layout)
        val dialog = builder.create()
        dialog.setTitle("Signature")
        dialog.show()

        val signaturePad = dialog.findViewById<SignaturePad>(R.id.signature_pad)
        dialog.findViewById<Button>(R.id.signature_clear).setOnClickListener {
            signaturePad.clear()
        }

        dialog.findViewById<Button>(R.id.signature_done).setOnClickListener {
            var time = Calendar.getInstance()
            CustomDialogBuilder(
                requireContext(),
                "Sending Product Picked/Delivered Info",
                String.format(
                    "Driver ID: %s " +
                            "\nTrip ID: %d " +
                            "\nDestination ID: %d " +
                            "\nProduct ID: %s " +
                            "\nStart Time: %d:%d " +
                            "\nEnd Time: %d:%d " +
                            "\nGross Qty: %d " +
                            "\nNet Qty: %d",

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
                        sharedViewModel.selectedSourceOrSite.value!!
                    viewModel.submitForm()
                    sharedViewModel.selectedTrip.value!!.sourceOrSite.find { it.deliveryStatus == DeliveryStatusEnum.ONGOING }!!.deliveryStatus =
                        DeliveryStatusEnum.COMPLETED
                    viewModel.updateDeliveryStatus(
                        sharedViewModel.selectedTrip.value!!.tripId,
                        DeliveryStatusEnum.COMPLETED
                    )
                },
                "Cancel",
                null,
                false
            ).builder.show()
        }

    }

    /**
     * Get time
     * This method initializes the dialog to select the time.
     * @param textView the text view to show time
     * @param textInputLayout responsible for showing time dialog
     * @param context the current context of the application
     */
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

    /**
     * Get date
     * This method initializes the dialog to select the date.
     * @param textView the text view to show date
     * @param textInputLayout responsible for showing date dialog
     * @param context the current context of the application
     */
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
