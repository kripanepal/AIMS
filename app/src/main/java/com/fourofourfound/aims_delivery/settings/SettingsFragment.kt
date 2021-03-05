package com.fourofourfound.aims_delivery.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Settings Fragment
 * This fragment is responsible for displaying the logout button to the user.
 * The user can click on the logout button to logout from the application.
 */
class SettingsFragment : Fragment() {

    /**
     * sharedViewModel
     * ViewModel that contains shared information about the user and the
     * trip
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
        binding.logoutBtn.setOnClickListener {
            checkUserLoggedIn()
        }



        return binding.root
    }

    /**
     * This method checks is the user is logged into
     * the application.
     */

    private fun checkUserLoggedIn() {
        viewModel.logoutUser()
        sharedViewModel.userLoggedIn.value = false
        requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
    }
}