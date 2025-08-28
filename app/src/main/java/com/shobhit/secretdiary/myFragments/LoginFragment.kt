package com.shobhit.secretdiary.myFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.FragmentLoginBinding
import com.shobhit.secretdiary.myActivities.HomeActivity
import com.shobhit.secretdiary.myDataClass.LoginRequest
import com.shobhit.secretdiary.myObject.RetrofitInstance
import com.shobhit.secretdiary.myRepository.AuthRepository
import com.shobhit.secretdiary.myUtilities.SessionManager
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar
import com.shobhit.secretdiary.myViewModel.AuthViewModel
import com.shobhit.secretdiary.myViewModelFactory.AuthViewModelFactory

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var authViewModel: AuthViewModel
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val authRepository = AuthRepository(RetrofitInstance.api)
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (email.isEmpty()) {
                binding.emailEditText.error = "Email is required"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                binding.passwordEditText.error = "Password is required"
                return@setOnClickListener
            }
            authViewModel.loginUser(LoginRequest(email, password))
        }

        authViewModel.buttonStatus.observe(viewLifecycleOwner) {
            binding.loginButton.isEnabled = it
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                Intent(requireContext(), HomeActivity::class.java).apply {
                    putExtra("showSnackbar", true)
                    startActivity(this)
                }
                requireActivity().finish()
            } else {
                showCustomSnackbar(binding.root, "Login Failed", alert = true)
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) {
            showCustomSnackbar(binding.root, it, alert = true)
        }

        binding.registerText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        toast = Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        toast.cancel()
                        requireActivity().finish()
                    } else {
                        toast.show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
        )
    }
}