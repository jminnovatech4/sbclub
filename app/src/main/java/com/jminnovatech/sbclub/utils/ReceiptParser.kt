package com.jminnovatech.sbclub.utils

data class ReceiptData(
    val amount: String,
    val utr: String,
    val app: String,
    val rawText: String
)

object ReceiptParser {

    fun parse(text: String): ReceiptData {

        return ReceiptData(
            amount = findAmount(text),
            utr = findUTR(text),
            app = findApp(text),
            rawText = text
        )
    }

    private fun findAmount(text: String): String {

        val lines = text.lines()

        // ==========================
        // ₹ Amount
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "₹\\s*([0-9,]+(?:\\.[0-9]{1,2})?)"
            ).find(line)

            if (match != null) {

                return match.groupValues[1]
                    .replace(",", "")
                    .replace(".00", "")
            }
        }

        // ==========================
        // Rs Amount
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "Rs\\.?\\s*([0-9,]+)",
                RegexOption.IGNORE_CASE
            ).find(line)

            if (match != null) {

                return match.groupValues[1]
                    .replace(",", "")
            }
        }

        // ==========================
        // INR Amount
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "INR\\s*([0-9,]+)",
                RegexOption.IGNORE_CASE
            ).find(line)

            if (match != null) {

                return match.groupValues[1]
                    .replace(",", "")
            }
        }

        // ==========================
        // Amount :
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "Amount\\D*([0-9,]+)",
                RegexOption.IGNORE_CASE
            ).find(line)

            if (match != null) {

                return match.groupValues[1]
                    .replace(",", "")
            }
        }

        // ==========================
        // Paid :
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "Paid\\D*([0-9,]+)",
                RegexOption.IGNORE_CASE
            ).find(line)

            if (match != null) {

                return match.groupValues[1]
                    .replace(",", "")
            }
        }

        // ==========================
        // PhonePe / GPay fallback
        // e.g. 5,210
        // ==========================
        lines.forEach { line ->

            val match = Regex(
                "\\b([0-9]{1,3}(?:,[0-9]{3})+)\\b"
            ).find(line)

            if (match != null) {

                return match.value
                    .replace(",", "")
            }
        }

        // ==========================
        // Last Fallback
        // ==========================
        val clean = text.replace("\n", " ")

        Regex("\\b([0-9]{2,6})\\b")
            .findAll(clean)
            .forEach {

                val value =
                    it.groupValues[1].toInt()

                if (value in 10..1000000) {

                    return value.toString()

                }

            }

        return ""

    }

    private fun findUTR(text: String): String {

        val patterns = listOf(

            Regex(
                "UTR\\s*(No|Number)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
                RegexOption.IGNORE_CASE
            ),

            Regex(
                "UPI\\s*Ref(?:erence)?\\s*(No|Number)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
                RegexOption.IGNORE_CASE
            ),

            Regex(
                "Transaction\\s*(ID|No|Number)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
                RegexOption.IGNORE_CASE
            ),

            Regex(
                "Ref(?:erence)?\\s*(No|Number)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
                RegexOption.IGNORE_CASE
            )

        )

        patterns.forEach {

            val m = it.find(text)

            if (m != null) {

                return m.groupValues.last()

            }

        }

        val digitRegex =
            Regex("\\b\\d{12,18}\\b")

        return digitRegex.find(text)?.value ?: ""

    }

    private fun findApp(text: String): String {

        val t = text.lowercase()

        return when {

            "google pay" in t -> "Google Pay"

            "gpay" in t -> "Google Pay"

            "phonepe" in t -> "PhonePe"

            "paytm" in t -> "Paytm"

            "bhim" in t -> "BHIM"

            "amazon pay" in t -> "Amazon Pay"

            else -> "UPI"

        }

    }

}