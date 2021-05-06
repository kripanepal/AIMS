package com.fourofourfound.aims_delivery.settings

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.StatusMessageEnum
import com.fourofourfound.aims_delivery.utils.getDate
import com.fourofourfound.aims_delivery.utils.showStartCallDialog
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentSettingsBinding
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.guidance.NavigationManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


/**
 * Settings Fragment
 * This fragment is responsible for displaying the logout button to the user.
 * The user can click on the logout button to logout from the application.
 */
class SettingsFragment : Fragment() {

    /**
     * sharedViewModel
     * ViewModel that contains shared information about the user and the trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /**
     * _binding
     * The binding object that is used by this fragment
     */
    private var _binding: FragmentSettingsBinding? = null

    /**
     * binding
     * The binding object that is used by this fragment which delegates to
     * _binding to prevent memory leaks
     */
    private val binding get() = _binding!!

    /**
     * viewModel
     * ViewModel that is used by the fragment to store the data.
     */
    lateinit var viewModel: SettingsViewModel
    lateinit var observer: androidx.lifecycle.Observer<in Boolean>

    var needToSendSignedInInfo = false

    /**
     * On create view
     * This method initializes the fragment
     * @param inflater the inflater that is used to inflate the view
     * @param container the container that holds the fragment
     * @param savedInstanceState called when fragment is starting
     * @return the view that is inflated
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //create a binding object
        _binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater, R.layout.fragment_settings, container, false
        )
        //initialize viewModel and assign value to the viewModel in xml file
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //show user logout confirmation message
        binding.logoutView.setOnClickListener {
            CustomDialogBuilder(
                requireContext(),
                "Logout?",
                "Do you want to logout?",
                "Yes",
                { logoutUser() },
                "No",
                null,
                true
            ).builder.show()
        }
        binding.downloadMaps.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mapDownloadFragment)
        }
        binding.help.setOnClickListener {
            showStartCallDialog(requireContext())
        }
        binding.about.setOnClickListener {
            showAboutDialog()
        }
        viewModel.getDatabase()
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
        viewModel.getDeliveryData()
        viewModel.loading.observe(viewLifecycleOwner) {
            sharedViewModel.loading.value = it
        }
        sharedViewModel.driver?.apply {
            binding.driver = this
        }

        binding.clockInOutBtn.setOnClickListener {
            sharedViewModel.userClockedIn.value =  !sharedViewModel.userClockedIn.value!!
        }


        observer = androidx.lifecycle.Observer{
            var animation = binding.clockAnimated.drawable
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    if (sharedPref != null) {
                        with (sharedPref.edit()) {
                            putBoolean("userSignedIn", it)
                            apply()
                        }
                    }
                if (it) {

                    if (animation is Animatable)
                    animation.start()
                    if(needToSendSignedInInfo)
                    {
                        binding.clockInOutBtn.text = "Clock Out"
                        binding.clockInOutBtn.setBackgroundColor(Color.RED)
                        sendSigningOnOffMessage(StatusMessageEnum.ONDUTY, sharedViewModel.driver!!.code)
                        needToSendSignedInInfo = false
                    }
                }
                else {
                    if (animation is Animatable)
                   animation.stop()
                    binding.clockInOutBtn.text = "Clock In"
                    binding.clockInOutBtn.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                        R.color.Dark_green))
                    if (it)
                    sendSigningOnOffMessage(StatusMessageEnum.OFFDUTY, sharedViewModel.driver!!.code)
                    needToSendSignedInInfo = true
                }

        }
      sharedViewModel.userClockedIn.observe(viewLifecycleOwner, observer)

    }

    /**
     * Show about dialog
     * This method shows a dialog containing the information about the application
     */
    private fun showAboutDialog() {
        val dialogView = LayoutInflater.from(context).inflate(
            R.layout.about_dialog, null
        )
        CustomDialogBuilder(
            requireContext(),
            "About",
            null,
            "Ok",
            null,
            null,
            null,
            true
        ).builder.setView(dialogView).show()
    }

    /**
     * Logout user
     * This methods logout the user and navigate to
     * login screen.
     */
    private fun logoutUser() {
        viewModel.loading.value = true
        needToSendSignedInInfo = false
        sharedViewModel.userClockedIn.removeObserver(observer)
        if(sharedViewModel.userClockedIn.value!!)sendSigningOnOffMessage(StatusMessageEnum.OFFDUTY,sharedViewModel.driver!!.code)
        viewModel.logoutUser()
        NavigationManager.getInstance()?.stop()
        MapEngine.getInstance().onPause()
        sharedViewModel.activeRoute = null
        sharedViewModel.selectedTrip.value = (null)
        viewModel.loading.value = false
        clearViewModels()
        requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation

    }

    private fun sendSigningOnOffMessage(statusCodeToGet: StatusMessageEnum, code: String) {
        val toPut = DatabaseStatusPut(
           code,
            0,
            statusCodeToGet.code,
            statusCodeToGet.message,
            getDate(Calendar.getInstance())
        )

        DeliveryStatusViewModel.sendStatusUpdate(
            toPut,
            getDatabase(requireContext(),  code)
        )
    }

    /**
     * Clear View Model
     * This method erases all the info about the shared view model
     * @param activity the activity of the application
     */
    private fun clearViewModels() {
       sharedViewModel.apply {
           this.activeRoute = null
           this.driver = null
           this.selectedSourceOrSite.value = null
           this.selectedTrip.value = null
           this.userClockedIn.value = false
       }
         val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()

        deliveryStatusViewModel.apply {
           this.previousDestination = null
           this.destinationApproachingShown = false
           this.destinationLeavingShown = false
       }

    }
}