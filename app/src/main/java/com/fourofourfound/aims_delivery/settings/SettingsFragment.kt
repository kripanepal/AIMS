package com.fourofourfound.aims_delivery.settings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.showStartCallDialog
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentSettingsBinding
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.guidance.NavigationManager
import kotlin.system.exitProcess


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
        sharedViewModel.userLoggedIn.value = false
        sharedViewModel.activeRoute = null
        sharedViewModel.selectedTrip.value = (null)
        viewModel.logoutUser()
        sharedViewModel.driver = null
        NavigationManager.getInstance()?.stop()
        MapEngine.getInstance().onPause()
        viewModel.loading.value = false
        //requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
        restartApplication(requireActivity())
    }

    /**
     * Restart application
     * This method restarts the application when log out is pressed.
     * @param activity the activity of the application
     */
    fun restartApplication(activity: Activity) {
        viewModel.logoutUser.observe(viewLifecycleOwner) {
            if (it) {
                val pm = activity.packageManager
                val intent = pm.getLaunchIntentForPackage(activity.packageName)
                activity.finishAffinity() // Finishes all activities.
                activity.startActivity(intent) // Start the launch activity
                exitProcess(0) // System finishes and automatically relaunches us.
            }
        }

    }
}