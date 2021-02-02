package com.fourofourfound.encrypted_preferences.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fourofourfound.encrypted_preferences.R
import com.fourofourfound.encrypted_preferences.databinding.FragmentLoginBinding
import com.fourofourfound.encrypted_preferences.hideSoftKeyboard

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        val viewModel = LoginViewModel(requireNotNull(this.activity).application)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

       if(viewModel.checkUserLoggedIn())
       {
           findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
       }

        binding.loginButton.setOnClickListener {
           binding.apply {
               viewModel.saveUser(userIdInput.toString(),passwordInput.toString())
           }
            it.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePage())
        }

        binding.loginScreenConstraintLayout.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }

        return binding.root
    }





}