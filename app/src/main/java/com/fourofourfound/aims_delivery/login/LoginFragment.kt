package com.fourofourfound.aims_delivery.login
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Login fragment
 * This fragment is responsible for displaying the login screen, validate and
 * authenticate the user and redirect the user to homescreen
 *
 * @constructor Create empty Login fragment
 */
class LoginFragment : Fragment() {

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()


    /**
     * _binding
     * The binding object that is used by this fragment
     */
    private var _binding: FragmentLoginBinding? = null

    /**
     * binding
     * The binding object that is used by this fragment which delegates to
     * _binding to prevent memory leaks
     */
    private val binding get() = _binding!!

    /**
     * Loading animation
     * Animations that are used to display when user is being authenticated
     */
    private lateinit var loadingAnimation: AnimationDrawable

    /**
     * View model
     *  the ViewModel that is used by the fragment to store the data
     */
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
        return binding.root
    }

    /**
     * done button in the keyboard.
     * starts user authentication on click
     */
    private fun loginOnDoneKey() {
        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.authenticateUser()
            }
            false
        }
    }

    /**
     * Error message animation
     * Animate error messages if any, after the
     * network call is made to authenticate the user
     */
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
                } else stop()
            }
        })
    }

    /**
     * Observe login fields
     * observe login fields to display errors if the fields are empty
     */
    private fun observeLoginFields() {
        viewModel.userFieldTouched.observe(viewLifecycleOwner) {
            if (it) {
                binding.userIdInput.apply {
                    error = "User Id is required"
                    hint = "Please enter your user Id"
                }
            }
        }

        viewModel.passwordFieldTouched.observe(viewLifecycleOwner) {
            if (it) {
                binding.passwordInput.apply {
                    error = "Password is required"
                    hint = "Please enter your password"
                }
            }
        }
    }


    /**
     * Show dialog
     *method to display the dialog with provider's number
     */
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

    /**
     * Start call
     *Start an intent to start the call to a
     * specific number
     */
    private fun startCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        val phoneNumber = "tel:" + getString(R.string.provider_number)
        intent.data = Uri.parse(phoneNumber)
        startActivity(intent)
    }

    /**
     * On destroy view
     *assign _binding to null to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}