package com.example.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.location.LocationAdapter.ViewHolder.Companion.context
import com.example.location.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var radioSelected: String = "NONE"
    private var textEntered: String = ""

    var location = CustomLocation(0.0, 0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var locationProvider = MyLocationProvider(this)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        locationProvider.requestPermission()
        locationProvider.getLastLocation()
        //creating a view model
        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        button.setOnClickListener {
            locationProvider.requestPermission()
            locationProvider.getLastLocation()
            @SuppressLint("MissingPermission")
            if (locationProvider.isLocationEnabled()) {
                locationProvider.fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    it.result?.let { coordinate ->
                        location = CustomLocation(coordinate.latitude, coordinate.longitude)

                        val selectedRadioOption = radioGroup.checkedRadioButtonId

                        radioSelected = (findViewById<View>(selectedRadioOption) as RadioButton).text as String

                        textEntered = text_view.text.toString()
                        viewModel.saveData(location, radioSelected, textEntered)
                    }
                }
            }else{
                Toast.makeText(context, "Open Location", Toast.LENGTH_SHORT).show()
            }

        }
        val adapter = LocationAdapter()


        binding.recyclerView.adapter = adapter

        viewModel.records.observe(this) {
            adapter.submitList(it)
        }


    }

}