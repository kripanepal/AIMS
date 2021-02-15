package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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
            saveData(locationProvider)
        }
        val adapter = LocationAdapter()

        binding.recyclerView.adapter = adapter

        viewModel.records.observe(this) {
            adapter.submitList(it)
            recycler_view.post { recycler_view.scrollToPosition(0) }
        }

        edit_text.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveData(locationProvider)
                true
            } else false
        }

        recycler_view.adapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recycler_view.scrollToPosition(0)
            }
        })
    }

    private fun saveData(locationProvider: MyLocationProvider) {
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
                        edit_text.text = null
                        radioGroup.check(R.id.Good)
                        closeKeyboard()
                    }

                }
            }
        } else {
            Toast.makeText(context, "Open Location", Toast.LENGTH_SHORT).show()
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

    private fun checkTextEmpty(text: String): Boolean {
        if (text.isNullOrEmpty()) {
            edit_text.error = "Required"
            return true
        }
        return false
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}



