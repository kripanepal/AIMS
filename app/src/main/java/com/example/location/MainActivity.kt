package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
    lateinit var viewModel: MainActivityViewModel

    var location = CustomLocation(0.0, 0.0)
    lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        var locationProvider = MyLocationProvider(this)
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        locationProvider.requestPermission()
        locationProvider.getLastLocation()
        //creating a view model
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        checkForStoredData()
        button.setOnClickListener {
            locationProvider.requestPermission()
            locationProvider.getLastLocation()
            @SuppressLint("MissingPermission")
            if (locationProvider.isLocationEnabled()) {
                locationProvider.fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    it.result?.let { coordinate ->
                        location = CustomLocation(coordinate.latitude, coordinate.longitude)

                        val selectedRadioOption = radioGroup.checkedRadioButtonId

                        radioSelected =
                            (findViewById<View>(selectedRadioOption) as RadioButton).text as String

                        textEntered = edit_text.text.toString()

                        if (!checkTextEmpty(textEntered)) {
                            viewModel.saveData(location, radioSelected, textEntered)
                        }

                    }
                }
            } else {
                Toast.makeText(context, "Open Location", Toast.LENGTH_SHORT).show()
            }
        }
        val adapter = LocationAdapter()

        binding.recyclerView.adapter = adapter

        viewModel.records.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun checkForStoredData() {
        edit_text.setText(sharedPref.getString("textEntered", null))
        radioGroup.check(sharedPref.getInt("radioSelected", R.id.Good))
    }

    override fun onStop() {
        super.onStop()
        val editor = sharedPref.edit()
        editor.putString("textEntered", edit_text.text.toString())
        editor.putInt("radioSelected", radioGroup.checkedRadioButtonId)
        editor.apply()


    }

    fun checkTextEmpty(text: String): Boolean {
        if (text.isNullOrEmpty()) {
            edit_text.error = "Required"
            return true
        }
        return false
    }
}



