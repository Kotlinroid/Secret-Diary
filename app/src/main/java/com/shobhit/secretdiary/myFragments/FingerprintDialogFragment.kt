package com.shobhit.secretdiary.myFragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.FingerprintDialogBinding

class FingerprintDialogFragment(
    private val onFingerprintSuccess: (Boolean) -> Unit
) : DialogFragment() {
    private lateinit var binding: FingerprintDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fingerprint_dialog,
            null,
            false
        )

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.setCancelable(false) // block outside touches; user can cancel with button

        // Make background transparent so we see blurred content behind
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

       //  Slight delay to ensure views are attached before prompting
        Handler(Looper.getMainLooper()).post { showBiometricPrompt() }

        return dialog
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onFingerprintSuccess(true)
                    dismiss()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
                    onFingerprintSuccess(false)
                    dismiss()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                    onFingerprintSuccess(false)
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Place your finger on the sensor")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(info)
    }

}

