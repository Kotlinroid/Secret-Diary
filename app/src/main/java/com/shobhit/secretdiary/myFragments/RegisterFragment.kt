package com.shobhit.secretdiary.myFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.FragmentRegisterBinding
import com.shobhit.secretdiary.myDataClass.RegisterRequest
import com.shobhit.secretdiary.myObject.RetrofitInstance
import com.shobhit.secretdiary.myRepository.AuthRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar
import com.shobhit.secretdiary.myViewModel.AuthViewModel
import com.shobhit.secretdiary.myViewModelFactory.AuthViewModelFactory

class RegisterFragment : Fragment() {

    // DataBinding for accessing layout views
    private lateinit var binding: FragmentRegisterBinding

    // ViewModel for handling registration logic
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout with ViewBinding
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** ---------------------- ViewModel Setup ---------------------- **/
        val sessionManager = SessionManager(requireContext())
        val authRepository = AuthRepository(RetrofitInstance.api)
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        /** ---------------------- Back Button Click ---------------------- **/
        binding.backButtonLayout.setOnClickListener {
            findNavController().popBackStack()
        }

        /** ---------------------- Register Button Click ---------------------- **/
        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val fName = binding.fNameEditText.text.toString().trim()
            val lName = binding.lNameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            // Validate input fields
            when {
                username.isEmpty() -> {
                    binding.usernameEditText.error = "Username is required"
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.emailEditText.error = "Email is required"
                    return@setOnClickListener
                }
                fName.isEmpty() -> {
                    binding.fNameEditText.error = "First name is required"
                    return@setOnClickListener
                }
                lName.isEmpty() -> {
                    binding.lNameEditText.error = "Last name is required"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password is required"
                    return@setOnClickListener
                }
                confirmPassword.isEmpty() -> {
                    binding.confirmPasswordEditText.error = "Confirm password is required"
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    binding.confirmPasswordEditText.error = "Passwords do not match"
                    return@setOnClickListener
                }
                else -> {
                    // Call ViewModel function to register user
                    authViewModel.registerUser(
                        RegisterRequest(
                            email,
                            username,
                            fName,
                            lName,
                            password,
                            confirmPassword,
                            "professor" // default role
                        )
                    )
                }
            }
        }

        /** ---------------------- Observe Button State ---------------------- **/
        authViewModel.buttonStatus.observe(viewLifecycleOwner) { isEnabled ->
            binding.registerButton.isEnabled = isEnabled
        }

        /** ---------------------- Observe Error Messages ---------------------- **/
        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            showCustomSnackbar(binding.root, error, alert = true)
        }

        /** ---------------------- Observe Registration Response ---------------------- **/
        authViewModel.registerResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                showCustomSnackbar(binding.root, "Registration Successful")
                findNavController().popBackStack() // Go back to previous screen
            } else {
                showCustomSnackbar(binding.root, "Registration Failed", alert = true)
            }
        }

        /** ---------------------- Navigate to Login ---------------------- **/
        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}
