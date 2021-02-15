package com.example.location

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
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

        //creating a view model
        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        button.setOnClickListener {
            locationProvider.requestPermission()
            locationProvider.getLastLocation()

           if(locationProvider.location != null){
               location = CustomLocation(locationProvider.location!!.latitude, locationProvider.location!!.longitude )

           }
            textEntered = text_view.text.toString()
            viewModel.saveData(location, radioSelected, textEntered)

        }
        val adapter = LocationAdapter()


        viewModel.records.observe(this) {
            Log.i("Bhalu", it.toString())
                adapter.submitList(it)
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            (findViewById<RadioButton>(checkedId).text as String).let {
                if (!it.isNullOrEmpty()) {
                    radioSelected = it
                }
            }
        }



        binding.recyclerView.adapter = adapter

        viewModel.records.observe(this) {
            adapter.submitList(it)
        }


    }

}