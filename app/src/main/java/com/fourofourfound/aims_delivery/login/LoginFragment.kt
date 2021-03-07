package com.fourofourfound.aims_delivery.login

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.hideSoftKeyboard
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*


class LoginFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingAnimation: AnimationDrawable
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        errorMessageAnimation()
        requireActivity().bottom_navigation.visibility = View.GONE
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        //checks if shared preferences already contains a user that is logged in
        if(viewModel.checkUserLoggedIn()) {
            findNavController().navigate(R.id.homePage)
            sharedViewModel.userLoggedIn.value = true
        }


        //navigate to the homepage if valid authentication is provided
        viewModel.navigate.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(R.id.homePage)
                sharedViewModel.userLoggedIn.value = true
                viewModel.doneNavigatingToHomePage()
            }
        })

        //show dialog listener
        binding.contactMyProvider.setOnClickListener {
            showDialog()
        }

        observeLoginFields()
        loginOnDoneKey()
        binding.loginPageMainView.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        viewModel.loading.observe(viewLifecycleOwner) { if (it) hideSoftKeyboard(requireActivity()) }
        return binding.root
    }

    /*added done button in the keyboard.
starts user authentication on click*/
    private fun loginOnDoneKey() {
        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.authenticateUser()
            }
            false
        }
    }

    private fun errorMessageAnimation() {
        //initialize background resource for loading animation
        binding.isLoading.setBackgroundResource(R.drawable.anim_loading)
        loadingAnimation = binding.isLoading.background as AnimationDrawable
        //animate the error message by zooming in
        viewModel.errorMessage.observe(viewLifecycleOwner, {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in_animation)
            binding.loginErrorMessage.startAnimation(animation)
        })
        //loading animation
        viewModel.loading.observe(viewLifecycleOwner, {
            loadingAnimation.apply {
                if (it) {
                    start()
                    val imm =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                } else stop()
            }
        })
    }

    private fun observeLoginFields() {
        viewModel.userFieldTouched.observe(viewLifecycleOwner) {
            if (it) binding.userIdInput.error = "User Id is required"
        }

        viewModel.passwordFieldTouched.observe(viewLifecycleOwner) {
            if (it) {
                binding.textInputLayout.isEndIconVisible = false
                binding.passwordInput.error = "Password is required"
            } else binding.textInputLayout.isEndIconVisible = true

        }
    }

    //method to display the dialog with provider's number
    private fun showDialog() {
        val dialogView = LayoutInflater.from(context).inflate(
            R.layout.contact_my_provider_dialog, null
        )
        CustomDialogBuilder(
            requireContext(),
            "Contact Info",
            null,
            "Call now",
            { startCall() },
            "Cancel",
            null,
            false
        ).builder.setView(dialogView).show()
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