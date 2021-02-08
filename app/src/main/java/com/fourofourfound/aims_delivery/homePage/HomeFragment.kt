package com.fourofourfound.aims_delivery.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = DataBindingUtil.inflate<FragmentHomePageBinding>(
            inflater, R.layout.fragment_home_page, container, false
        )

        val viewModel = HomePageViewModel(requireNotNull(this.activity).application)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.userLoggedIn.observe(viewLifecycleOwner, {
            if (!it) findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
        })

        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { tripId ->
            Toast.makeText(context, "$tripId", Toast.LENGTH_SHORT).show()
        })
        binding.sleepList.adapter = adapter

        viewModel.tripList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}