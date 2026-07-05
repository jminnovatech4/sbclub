package com.jminnovatech.sbclub.ui.screens.user.progame


object BetValidator {

    fun validate(
        gameCode: String,
        number: String
    ): Boolean {

        return when (gameCode.lowercase()) {

            "single" -> validateSingle(number)

            "jodi" -> validateJodi(number)

            "patti" -> validatePatti(number)

            "sp" -> validateSP(number)

            "dp" -> validateDP(number)

            "panel" -> validatePanel(number)

            else -> true
        }
    }

    fun error(
        gameCode: String
    ): String {

        return when (gameCode.lowercase()) {

            "single" ->
                "Enter 1 digit (0-9)"

            "jodi" ->
                "Enter 2 digit number"

            "patti" ->
                "Enter 3 digit Patti"

            "sp" ->
                "Enter Single Patti"

            "dp" ->
                "Enter Double Patti"

            "panel" ->
                "Enter valid Panel"

            else ->
                "Invalid Number"
        }

    }

    //========================================

    private fun validateSingle(
        number: String
    ): Boolean {

        return number.matches(
            Regex("^[0-9]$")
        )

    }

    //========================================

    private fun validateJodi(
        number: String
    ): Boolean {

        return number.matches(
            Regex("^[0-9]{2}$")
        )

    }

    //========================================

    private fun validatePatti(
        number: String
    ): Boolean {

        return number.matches(
            Regex("^[0-9]{3}$")
        )

    }

    //========================================

    private fun validateSP(
        number: String
    ): Boolean {

        if (!number.matches(
                Regex("^[0-9]{3}$")
            )
        ) {
            return false
        }

        return number.toSet().size == 3
    }

    //========================================

    private fun validateDP(
        number: String
    ): Boolean {

        if (!number.matches(
                Regex("^[0-9]{3}$")
            )
        ) {
            return false
        }

        return number.toSet().size == 2
    }

    //========================================

    private fun validatePanel(
        number: String
    ): Boolean {

        return number.matches(
            Regex("^[0-9]{6}$")
        )

    }

}