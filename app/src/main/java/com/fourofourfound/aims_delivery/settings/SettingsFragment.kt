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

class SettingsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater, R.layout.fragment_settings, container, false
        )
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.logoutBtn.setOnClickListener {
            checkUserLoggedIn()
        }



        return binding.root
    }

    private fun checkUserLoggedIn() {
        viewModel.logoutUser()
        sharedViewModel.userLoggedIn.value = false
        requireActivity().bottom_navigation.selectedItemId = R.id.home_navigation
    }
}