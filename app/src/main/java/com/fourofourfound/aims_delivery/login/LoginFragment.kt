package com.fourofourfound.aims_delivery.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.MainActivity
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.hideSoftKeyboard
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.StatusMessageEnum
import com.fourofourfound.aims_delivery.utils.getDate
import com.fourofourfound.aims_delivery.utils.showStartCallDialog
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentLoginBinding
import java.util.*

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
     * View model
     *  the ViewModel that is used by the fragment to store the data
     */
    private lateinit var viewModel: LoginViewModel

    /**
     * Send signed in message
     * Sends signed message
     */
    var sendSignedInMessage = false

    /**
     * Animated
     * Flag to check if the login page is already animated.
     */
    var animated = false

    /**
     * On create view
     * It is called when the fragment is created.
     * @param inflater the inflater used to inflate the layout
     * @param container the viewGroup where the layout is added
     * @param savedInstanceState any saved data from configuration changes
     * @return the view that is displayed dby this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //checks if shared preferences already contains a user that is logged in
        if (viewModel.checkUserLoggedIn()) {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedViewModel.userClockedIn.value = sharedPref.getBoolean("userSignedIn", false)
            moveToHomePage()
            return binding.root
        }

        //navigate to the homepage if valid authentication is provided
        viewModel.navigate.observe(viewLifecycleOwner, {
            if (it) {
                binding.loginButton.apply {
                    visibility = View.GONE
                    text = "Login Successful"
                    setTextColor(requireContext().getColor(R.color.Green))
                }

                if (sendSignedInMessage) {
                    val statusCodeToGet = StatusMessageEnum.ONDUTY
                    val toPut = DatabaseStatusPut(
                        viewModel.loggedInDriver.code,
                        0,
                        statusCodeToGet.code,
                        statusCodeToGet.message,
                        getDate(Calendar.getInstance())
                    )

                    DeliveryStatusViewModel.sendStatusUpdate(
                        toPut,
                        getDatabase(requireContext(), viewModel.loggedInDriver.code)
                    )
                    setLoggedInMotionAnimation()
                    sharedViewModel.userClockedIn.value = true

                } else {
                    moveToHomePage()
                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    sharedViewModel.userClockedIn.value = sharedPref.getBoolean("userSignedIn", false)
                }
            }
        })
        //show dialog listener
        binding.contactMyProvider.setOnClickListener {
            showStartCallDialog(requireContext())
        }
        observeLoginFields()
        loginOnDoneKey()

        setMotionAnimations(binding.passwordInput)
        setMotionAnimations(binding.userIdInput)
        setReverseMotionAnimations()

        binding.loginPageMainView.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            sharedViewModel.loading.value = it
            if (it) hideSoftKeyboard(requireActivity())
        }
        return binding.root
    }

    /**
     * Move to home page
     * This method navigates the driver to the homepage if the user is already logged in.
     */
    private fun moveToHomePage() {
        (activity as MainActivity?)?.getDatabase()
        sharedViewModel.driver = viewModel.loggedInDriver

        findNavController().navigate(R.id.homePage)
        viewModel.doneNavigatingToHomePage()
    }

    /**
     * Set reverse motion animations
     * This method animates the login screen downward on click.
     */
    private fun setReverseMotionAnimations() {
        val motionContainer = binding.loginPageMainView
        (motionContainer  as View).setOnTouchListener { v, _ ->
            v.performClick()
            if(animated)
            {
                motionContainer.setTransition(R.id.end, R.id.start)
                motionContainer.transitionToEnd()
                animated = false
            }
            true
        }
    }

    /**
     * Login on done key
     * Starts user authentication on click
     */
    private fun loginOnDoneKey() {
        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.authenticateUser()
                sendSignedInMessage = true
            }
            false
        }
    }


    /**
     * Observe login fields
     * observe login fields to display errors if the fields are empty
     */
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

    /**
     * On destroy view
     * Assign _binding to null to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Set motion animations
     * This method animates animates the login screen upwards on click.
     * @param view
     */
    private fun setMotionAnimations(view: View) {
        val motionContainer = binding.loginPageMainView
        view.setOnTouchListener { v, _ ->
            v.performClick()
            if (!animated) {
                motionContainer.setTransition(R.id.start, R.id.end)
                motionContainer.transitionToEnd()
                animated = true
            }
            false
        }
    }

    /**
     * Set logged in motion animation
     * The method shows the animation the login is successful.
     */
    private fun setLoggedInMotionAnimation() {
        val motionContainer = binding.loginPageMainView
        motionContainer.setTransition(R.id.end, R.id.loggedIn)
        motionContainer.transitionToEnd()
        motionContainer.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                moveToHomePage()
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    /**
     * On view created
     * This method is called when the view is created
     * @param view the view that is created
     * @param savedInstanceState called when fragment is started
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            viewModel.authenticateUser()
            sendSignedInMessage = true
        }
    }
}