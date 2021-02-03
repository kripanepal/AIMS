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
import androidx.databinding.DataBindingUtil
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
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        val viewModel = LoginViewModel(requireNotNull(this.activity).application)
        binding.apply {
            this.viewModel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }

        binding.isLoading.setBackgroundResource(R.drawable.anim_loading)
        loadingAnimation = binding.isLoading.background as AnimationDrawable
       if(viewModel.checkUserLoggedIn())
       {
           findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
       }

        binding.loginButton.setOnClickListener {
           binding.apply {
               viewModel.authenticateUser(
                   userIdInput.text.toString(),
                   passwordInput.text.toString()
               )

           }

        }

        viewModel.loading.observe(viewLifecycleOwner,  {
            if (it) {

                loadingAnimation.start()
            } else {

                loadingAnimation.stop()
            }
        })

        viewModel.navigate.observe(viewLifecycleOwner,  {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
                viewModel.doneNavigatingToHomePage()
            }
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
        return binding.root
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(context).inflate(
            R.layout.contact_my_provider_dialog,
            null
        )
        val mBuilder = AlertDialog.Builder(context)
            .setTitle("Contact Info")
            .setView(dialogView)
            .setCancelable(false)
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .setPositiveButton("Call now") { dialogInterface: DialogInterface, i: Int ->
                startCall()
            }
        mBuilder.show()
    }

    private fun startCall() {
        val i = Intent(Intent.ACTION_DIAL)
        val p = "tel:" + getString(R.string.provider_number)
        i.data = Uri.parse(p)
        startActivity(i)
    }


}