package com.shobhit.secretdiary.myUtilities

import android.provider.CalendarContract
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.shobhit.secretdiary.R

fun showCustomSnackbar(anchor: View, message: String, alert: Boolean = false){
    val snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT)

    snackbar.view.background = ContextCompat.getDrawable(anchor.context, R.drawable.snackbar_background)

    val textView = snackbar.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as android.widget.TextView
    textView.setTextColor(ContextCompat.getColor(anchor.context, R.color.snackbar_text_color))

    if (alert) {
        snackbar.view.backgroundTintList = ContextCompat.getColorStateList(anchor.context, R.color.snackbar_alert_color)
    } else {
        snackbar.view.backgroundTintList = ContextCompat.getColorStateList(anchor.context, R.color.snackbar_green_color)
    }
    textView.typeface = ResourcesCompat.getFont(anchor.context, R.font.poppins_semibold)
    textView.textSize = 16f

    val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(50, 0, 50, 50)
    snackbar.view.layoutParams = params

    snackbar.show()
}