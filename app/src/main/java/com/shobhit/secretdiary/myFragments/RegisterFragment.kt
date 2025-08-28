package com.shobhit.secretdiary.myFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    lateinit var binding: FragmentRegisterBinding
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val authRepository = AuthRepository(RetrofitInstance.api)
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.backButtonLayout.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val fName = binding.fNameEditText.text.toString().trim()
            val lName = binding.lNameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if(username.isEmpty()){
                binding.usernameEditText.error = "Username is required"
                return@setOnClickListener
            }else if(email.isEmpty()){
                binding.emailEditText.error = "Email is required"
                return@setOnClickListener
            }else if(fName.isEmpty()){
                binding.fNameEditText.error = "First name is required"
                return@setOnClickListener
            }else if(lName.isEmpty()){
                binding.lNameEditText.error = "Last name is required"
                return@setOnClickListener
            }else if(password.isEmpty()){
                binding.passwordEditText.error = "Password is required"
                return@setOnClickListener
            }else if(confirmPassword.isEmpty()){
                binding.confirmPasswordEditText.error = "Confirm password is required"
                return@setOnClickListener
            }else if(password != confirmPassword){
                binding.confirmPasswordEditText.error = "Passwords do not match"
                return@setOnClickListener
            }
            authViewModel.registerUser(RegisterRequest(email, username, fName, lName, password, confirmPassword,
                    "professor"
                ))
        }

        authViewModel.buttonStatus.observe(viewLifecycleOwner){
            binding.registerButton.isEnabled = it
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner){
            showCustomSnackbar(binding.root, it, alert = true)
        }

        authViewModel.registerResponse.observe(viewLifecycleOwner){
            if (it != null){
                showCustomSnackbar(binding.root, "Registration Successful")
                findNavController().popBackStack()
            } else {
                showCustomSnackbar(binding.root, "Registration Failed", alert = true)
            }
        }

        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}