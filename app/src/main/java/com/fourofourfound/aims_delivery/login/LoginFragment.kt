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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fourofourfound.encrypted_preferences.R
import com.fourofourfound.encrypted_preferences.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var loadingAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentLoginBinding.inflate(inflater)

        val viewModel = LoginViewModel(requireNotNull(this.activity).application)
        binding.apply {
            this.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.isLoading.setBackgroundResource(R.drawable.anim_loading)
        loadingAnimation = binding.isLoading.background as AnimationDrawable
        if(viewModel.checkUserLoggedIn()) findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())

        viewModel.loading.observe(viewLifecycleOwner,  {
            loadingAnimation.apply {  if (it){start()} else stop() }
        })

        viewModel.navigate.observe(viewLifecycleOwner,  {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
                viewModel.doneNavigatingToHomePage() }
        })

        //TODO this will close the app. need to find a way to hide a keyboard
        //        binding.loginScreenConstraintLayout.setOnClickListener {
        //            hideSoftKeyboard(requireActivity())
        //        }

        viewModel.errorMessage.observe(viewLifecycleOwner,  {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in_animation)
            binding.loginErrorMessage.startAnimation(animation)
        })

        binding.contactMyProvider.setOnClickListener {
            showDialog()
      }

        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.authenticateUser()
            }
            false
        }
        return binding.root
    }

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
    private fun startCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        val phoneNumber = "tel:" + getString(R.string.provider_number)
        intent.data = Uri.parse(phoneNumber)
        startActivity(intent)
    }

}