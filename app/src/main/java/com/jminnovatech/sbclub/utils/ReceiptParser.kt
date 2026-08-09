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

    // =========================================================
    // AMOUNT
    // =========================================================

    private fun findAmount(text: String): String {

        val lines = text
            .replace("\r", "")
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // =====================================================
        // 1. ₹100
        // 2. ₹ 100
        // 3. ₹1,000
        // 4. ₹ 1,000.00
        // =====================================================

        val rupeeRegex = Regex(
            "₹\\s*([0-9,]+(?:\\.[0-9]{1,2})?)"
        )

        for (line in lines) {

            val match = rupeeRegex.find(line)

            if (match != null) {

                return cleanAmount(
                    match.groupValues[1]
                )
            }
        }

        // =====================================================
        // 2. Rs 100
        // =====================================================

        val rsRegex = Regex(
            "\\bRs\\.?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        for (line in lines) {

            val match = rsRegex.find(line)

            if (match != null) {

                return cleanAmount(
                    match.groupValues[1]
                )
            }
        }

        // =====================================================
        // 3. INR 100
        // =====================================================

        val inrRegex = Regex(
            "\\bINR\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        for (line in lines) {

            val match = inrRegex.find(line)

            if (match != null) {

                return cleanAmount(
                    match.groupValues[1]
                )
            }
        }

        // =====================================================
        // 4. Amount ₹100
        // =====================================================

        val amountRegex = Regex(
            "Amount\\D{0,20}(?:₹|Rs\\.?|INR)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        val amountMatch = amountRegex.find(text)

        if (amountMatch != null) {

            return cleanAmount(
                amountMatch.groupValues[1]
            )
        }

        // =====================================================
        // 5. Paid ₹100
        // =====================================================

        val paidRegex = Regex(
            "Paid\\D{0,20}(?:₹|Rs\\.?|INR)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        val paidMatch = paidRegex.find(text)

        if (paidMatch != null) {

            return cleanAmount(
                paidMatch.groupValues[1]
            )
        }

        // =====================================================
        // 6. You paid 100
        // =====================================================

        val youPaidRegex = Regex(
            "You\\s+paid\\D{0,20}(?:₹|Rs\\.?|INR)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )

        val youPaidMatch = youPaidRegex.find(text)

        if (youPaidMatch != null) {

            return cleanAmount(
                youPaidMatch.groupValues[1]
            )
        }

        // =====================================================
        // 7. OCR comma amount
        //
        // Example:
        // 1,000
        // 5,210
        // 10,000
        // =====================================================

        val commaAmountRegex = Regex(
            "\\b([0-9]{1,3}(?:,[0-9]{2,3})+)\\b"
        )

        for (line in lines) {

            val match = commaAmountRegex.find(line)

            if (match != null) {

                val value = cleanAmount(match.value)

                val number = value.toDoubleOrNull()

                if (number != null && number >= 10) {

                    return value
                }
            }
        }

        // =====================================================
        // 8. OCR may convert ₹100 into just 100
        //
        // IMPORTANT:
        // Do NOT blindly take the first number.
        // First avoid UTR / transaction numbers.
        // =====================================================

        for (line in lines) {

            val normalized = line
                .replace(",", "")
                .trim()

            if (
                Regex(
                    "^[0-9]{1,6}(?:\\.[0-9]{1,2})?$"
                ).matches(normalized)
            ) {

                val number =
                    normalized.toDoubleOrNull()

                if (
                    number != null &&
                    number >= 10 &&
                    number <= 1000000
                ) {

                    return cleanAmount(normalized)
                }
            }
        }

        // =====================================================
        // 9. Last fallback
        // =====================================================

        val numberRegex = Regex(
            "\\b[0-9]{2,6}(?:\\.[0-9]{1,2})?\\b"
        )

        val candidates = numberRegex
            .findAll(text)
            .map { it.value }
            .toList()

        for (value in candidates) {

            val number = value.toDoubleOrNull()

            if (
                number != null &&
                number >= 10 &&
                number <= 1000000
            ) {

                // Avoid obvious UTR-sized values
                if (value.length < 10) {
                    return cleanAmount(value)
                }
            }
        }

        return ""
    }

    // =========================================================
    // CLEAN AMOUNT
    // =========================================================

    private fun cleanAmount(value: String): String {

        return value
            .replace(",", "")
            .replace("₹", "")
            .trim()
            .removeSuffix(".00")
    }

    // =========================================================
    // UTR
    // =========================================================

    private fun findUTR(text: String): String {

        // =====================================================
        // UTR
        // =====================================================

        val utrRegex = Regex(
            "UTR\\s*(?:No|Number|ID)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
            RegexOption.IGNORE_CASE
        )

        val utrMatch = utrRegex.find(text)

        if (utrMatch != null) {

            return utrMatch.groupValues[1]
        }

        // =====================================================
        // UPI Reference
        // =====================================================

        val upiReferenceRegex = Regex(
            "UPI\\s*Ref(?:erence)?\\s*(?:No|Number|ID)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
            RegexOption.IGNORE_CASE
        )

        val upiMatch =
            upiReferenceRegex.find(text)

        if (upiMatch != null) {

            return upiMatch.groupValues[1]
        }

        // =====================================================
        // Transaction ID
        // =====================================================

        val transactionRegex = Regex(
            "Transaction\\s*(?:ID|No|Number)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
            RegexOption.IGNORE_CASE
        )

        val transactionMatch =
            transactionRegex.find(text)

        if (transactionMatch != null) {

            return transactionMatch.groupValues[1]
        }

        // =====================================================
        // Reference
        // =====================================================

        val referenceRegex = Regex(
            "Ref(?:erence)?\\s*(?:No|Number|ID)?\\s*[:\\-]?\\s*([A-Za-z0-9]{10,30})",
            RegexOption.IGNORE_CASE
        )

        val referenceMatch =
            referenceRegex.find(text)

        if (referenceMatch != null) {

            return referenceMatch.groupValues[1]
        }

        // =====================================================
        // Pure numeric UTR
        // =====================================================

        val digitRegex =
            Regex("\\b\\d{12,18}\\b")

        val digitMatch =
            digitRegex.find(text)

        if (digitMatch != null) {

            return digitMatch.value
        }

        return ""
    }

    // =========================================================
    // APP DETECTION
    // =========================================================

    private fun findApp(text: String): String {

        val t = text.lowercase()

        return when {

            "google pay" in t ->
                "Google Pay"

            "gpay" in t ->
                "Google Pay"

            "phonepe" in t ->
                "PhonePe"

            "paytm" in t ->
                "Paytm"

            "bhim" in t ->
                "BHIM"

            "amazon pay" in t ->
                "Amazon Pay"

            else ->
                "UPI"
        }
    }
}