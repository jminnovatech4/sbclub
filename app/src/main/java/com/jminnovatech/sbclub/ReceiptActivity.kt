package com.jminnovatech.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class ReceiptActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val receipt = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""

        val amount = extractAmount(receipt)

        val utr = extractUTR(receipt)

        val app = detectApp(receipt)

        val i = Intent(this, MainActivity::class.java)

        i.putExtra("receipt_text", receipt)
        i.putExtra("amount", amount)
        i.putExtra("utr", utr)
        i.putExtra("upi_app", app)
        i.putExtra("open_deposit", true)

        i.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(i)

        finish()
    }

    private fun extractAmount(text: String): String {

        val regex = Regex("₹\\s?([0-9]+(?:\\.[0-9]{1,2})?)")

        return regex.find(text)
            ?.groupValues
            ?.getOrNull(1)
            ?: ""
    }

    private fun extractUTR(text: String): String {

        val regex = Regex("\\b\\d{12,18}\\b")

        return regex.find(text)
            ?.value
            ?: ""
    }

    private fun detectApp(text: String): String {

        val t = text.lowercase()

        return when {

            "phonepe" in t -> "PhonePe"

            "google pay" in t || "gpay" in t -> "Google Pay"

            "paytm" in t -> "Paytm"

            "bhim" in t -> "BHIM"

            else -> "UPI"

        }
    }
}