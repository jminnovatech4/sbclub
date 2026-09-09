package com.jminnovatech.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.net.Uri
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.jminnovatech.sbclub.utils.ReceiptParser
class ReceiptActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        when {

            intent.type == "text/plain" -> {

                handleText()

            }

            intent.type?.startsWith("image/") == true -> {

                handleImage()

            }

            else -> {

                Toast.makeText(
                    this,
                    "Unsupported Share",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            }

        }

    }
    private fun handleText() {

        val receipt =

            intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""

        processReceipt(receipt)

    }
    private fun processReceipt(text: String) {

        val result =
            ReceiptParser.parse(text)

        // =====================================
        // SAVED PAYMENT AMOUNT
        // =====================================

        val prefs =
            getSharedPreferences(
                "SBCLUB_PAYMENT",
                MODE_PRIVATE
            )

        val savedAmount =
            prefs.getString(
                "pending_amount",
                ""
            ) ?: ""

        val ocrAmount =
            result.amount.trim()


        // =====================================
        // CHECK OCR AMOUNT
        // =====================================

        if (
            savedAmount.isNotBlank() &&
            ocrAmount.isNotBlank()
        ) {

            val saved =
                savedAmount
                    .replace(",", "")
                    .toDoubleOrNull()

            val ocr =
                ocrAmount
                    .replace(",", "")
                    .toDoubleOrNull()


            // =================================
            // AMOUNT MISMATCH
            // =================================

            if (
                saved != null &&
                ocr != null &&
                saved != ocr
            ) {

                android.app.AlertDialog.Builder(this)

                    .setTitle("Amount Mismatch")

                    .setMessage(
                        "Payment amount: ₹$savedAmount\n" +
                                "Receipt amount: ₹$ocrAmount\n\n" +
                                "Receipt amount does not match.\n" +
                                "Please enter the correct amount manually."
                    )

                    .setPositiveButton("Enter Amount") { _, _ ->

                        val i =
                            Intent(
                                this,
                                MainActivity::class.java
                            )

                        i.putExtra(
                            "open_deposit",
                            true
                        )

                        // ❌ OCR amount remove
                        // User will enter amount manually
                        i.putExtra(
                            "amount",
                            ""
                        )

                        // ✅ Keep UTR
                        i.putExtra(
                            "utr",
                            result.utr
                        )

                        i.putExtra(
                            "upi_app",
                            result.app
                        )

                        i.putExtra(
                            "receipt_text",
                            result.rawText
                        )

                        i.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP

                        startActivity(i)

                        finish()
                    }

                    .setNegativeButton("Cancel") { dialog, _ ->

                        dialog.dismiss()
                        finish()

                    }

                    .show()

                return
            }
        }


        // =====================================
        // NORMAL EXISTING FLOW
        // =====================================

        val i =
            Intent(
                this,
                MainActivity::class.java
            )

        i.putExtra(
            "open_deposit",
            true
        )

        // Existing OCR amount
        i.putExtra(
            "amount",
            result.amount
        )

        i.putExtra(
            "utr",
            result.utr
        )

        i.putExtra(
            "upi_app",
            result.app
        )

        i.putExtra(
            "receipt_text",
            result.rawText
        )

        i.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(i)

        finish()
    }
    private fun handleImage() {

        val uri =
            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        if (uri == null) {

            Toast.makeText(
                this,
                "Image not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return

        }

        recognizeReceipt(uri)

    }
    private fun recognizeReceipt(uri: Uri) {

        try {

            val image = InputImage.fromFilePath(this, uri)

            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            recognizer.process(image)

                .addOnSuccessListener { visionText ->

                    if (visionText.text.isBlank()) {

                        Toast.makeText(
                            this,
                            "No text found in receipt",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                        return@addOnSuccessListener
                    }

                    // 🔥 পুরো OCR Text Logcat এ দেখাবে
                    android.util.Log.d(
                        "OCR_TEXT",
                        visionText.text
                    )

                    // 🔥 পুরো OCR Text Screen এ দেখাবে
                    Toast.makeText(
                        this,
                        visionText.text,
                        Toast.LENGTH_LONG
                    ).show()

                    processReceipt(
                        visionText.text
                    )
                }

                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "OCR Failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to read receipt",
                Toast.LENGTH_LONG
            ).show()

            finish()

        }

    }

}