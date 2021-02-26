package com.fourofourfound.aims_delivery.login
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    private lateinit var loadingAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)

        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.apply {
            this.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        //initialize background resource for loading animation
        binding.isLoading.setBackgroundResource(R.drawable.anim_loading)
        loadingAnimation = binding.isLoading.background as AnimationDrawable

        //checks if shared preferences already contains a user that is logged in
        if(viewModel.checkUserLoggedIn()) findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())

        //loading animation
        viewModel.loading.observe(viewLifecycleOwner,  {
            loadingAnimation.apply {  if (it){start()} else stop() }
        })

        //navigate to the homepage if valid authentication is provided
        viewModel.navigate.observe(viewLifecycleOwner,  {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
                viewModel.doneNavigatingToHomePage() }
        })

        //TODO this will close the app. need to find a way to hide a keyboard
        //        binding.loginScreenConstraintLayout.setOnClickListener {
        //            hideSoftKeyboard(requireActivity())
        //        }

        //animate the error message by zooming in
        viewModel.errorMessage.observe(viewLifecycleOwner,  {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in_animation)
            binding.loginErrorMessage.startAnimation(animation)
        })

        //show dialog listener
        binding.contactMyProvider.setOnClickListener {
            showDialog()
      }

        viewModel.userFieldTouched.observe(viewLifecycleOwner){
            if(it){
                binding.userIdInput.apply {
                    error = "User Id is required"
                    hint = "Please enter your user Id"
                }
            }
        }

        viewModel.passwordFieldTouched.observe(viewLifecycleOwner){
            if(it){
                binding.passwordInput.apply {
                    error = "Password is required"
                    hint = "Please enter your password"
                }
            }
        }

        /*added done button in the keyboard.
        starts user authentication on click*/
        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.authenticateUser()
            }
            false
        }
        return binding.root
    }

    //method to display the dialog with provider's number
    private fun showDialog() {
        val dialogView = LayoutInflater.from(context).inflate(
            R.layout.contact_my_provider_dialog,
            null)
        AlertDialog.Builder(context)
            .setTitle("Contact Info")
            .setView(dialogView)
            .setCancelable(false)
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .setPositiveButton("Call now") { _: DialogInterface, _: Int ->
                startCall()
            }
        .show()
    }

    //start the intent to call the provider
    private fun startCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        val phoneNumber = "tel:" + getString(R.string.provider_number)
        intent.data = Uri.parse(phoneNumber)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}