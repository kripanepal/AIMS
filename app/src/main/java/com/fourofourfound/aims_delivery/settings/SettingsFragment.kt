package com.fourofourfound.aims_delivery.settings

import android.content.Intent
import android.net.Uri
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
import kotlinx.android.synthetic.main.activity_main.*

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

        //checks if the user is logged in
        binding.logoutView.setOnClickListener {
            logoutUser()
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

        return binding.root
    }

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
            false
        ).builder.setView(dialogView).show()
    }

    /**
     * Logout user
     * This methods logout the user and navigate to
     * login screen.
     */
    private fun logoutUser() {
        sharedViewModel.userLoggedIn.value = false
        sharedViewModel.activeRoute = null
        sharedViewModel.selectedTrip.value = (null)
        viewModel.logoutUser()
        NavigationManager.getInstance()?.stop()
        MapEngine.getInstance().onPause()
        requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation

    }
}