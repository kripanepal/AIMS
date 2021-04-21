package com.fourofourfound.aims_delivery.delivery.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fourofourfound.aims_delivery.homePage.loadInformation.LoadInfoAdapter
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentDeliveryCompletedBinding

/**
 * Completed delivery fragment
 *This fragment is responsible for displaying the data related to
 * deliveries that are completed
 *
 * @constructor Create empty Completed delivery fragment
 */
class CompletedDeliveryFragment : Fragment() {
    /**
     * Binding
     * The binding object that is used by this fragment
     */
    lateinit var binding: FragmentDeliveryCompletedBinding

    /**
     * View model factory is the dactory that is used to create the viewmodel for this
     * fragment.
     */
    private lateinit var viewModelFactory: CompletedDeliveryViewModelFactory

    /**
     * View model
     * The view model that is used by this fragment to store data
     */
    private lateinit var viewModel: CompletedDeliveryViewModel

    /**
     * On create view
     * @param inflater the inflator used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get args using by navArgs property delegate
        val tripFragmentArgs by navArgs<CompletedDeliveryFragmentArgs>()

        //create a binding object
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delivery_completed,
            container,
            false
        )

        //constructing a view model factory
        viewModelFactory = CompletedDeliveryViewModelFactory(tripFragmentArgs.trip)

        //getting a view model from a factory
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(CompletedDeliveryViewModel::class.java)
        val adapter = LoadInfoAdapter()
        binding.sourceOrSiteInfo.adapter = adapter



        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return  binding.root
    }


}