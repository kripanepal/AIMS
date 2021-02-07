package com.fourofourfound.aims_delivery.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.data.trip.TripInfo
import com.fourofourfound.encrypted_preferences.R
import com.fourofourfound.encrypted_preferences.databinding.FragmentHomePageBinding

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
            Toast.makeText(context, "$tripId", Toast.LENGTH_LONG).show()
        })
        binding.sleepList.adapter = adapter

        adapter.submitList(
            listOf(
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa"),
                TripInfo("AA", "aa")
            )
        )


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}