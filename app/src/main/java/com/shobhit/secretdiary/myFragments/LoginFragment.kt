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

    // DataBinding instance for accessing layout views
    private lateinit var binding: FragmentLoginBinding

    // ViewModel instance for handling authentication logic
    private lateinit var authViewModel: AuthViewModel

    // Variables for handling double back-press to exit
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout using ViewBinding
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** ---------------------- ViewModel Setup ---------------------- **/
        val sessionManager = SessionManager(requireContext())
        val authRepository = AuthRepository(RetrofitInstance.api)
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        /** ---------------------- Login Button Click ---------------------- **/
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validate input fields
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Email is required"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password is required"
                    return@setOnClickListener
                }
                else -> {
                    // Call ViewModel function to login
                    authViewModel.loginUser(LoginRequest(email, password))
                }
            }
        }

        /** ---------------------- Observe Button State ---------------------- **/
        authViewModel.buttonStatus.observe(viewLifecycleOwner) { isEnabled ->
            binding.loginButton.isEnabled = isEnabled
        }

        /** ---------------------- Observe Login Response ---------------------- **/
        authViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // Login successful → Navigate to HomeActivity
                Intent(requireContext(), HomeActivity::class.java).apply {
                    putExtra("showSnackbar", true)
                    startActivity(this)
                }
                requireActivity().finish()
            } else {
                // Login failed → Show error Snackbar
                showCustomSnackbar(binding.root, "Login Failed", alert = true)
            }
        }

        /** ---------------------- Observe Error Messages ---------------------- **/
        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            showCustomSnackbar(binding.root, error, alert = true)
        }

        /** ---------------------- Navigate to Register ---------------------- **/
        binding.registerText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        /** ---------------------- Double Back Press to Exit ---------------------- **/
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
