package com.jminnovatech.sbclub.ui.screens.user.progame



    object BetValidator {

        fun validate(
            gameCode: String,
            number: String
        ): Boolean {

            return when (gameCode.lowercase()) {

                "single" -> number.matches(Regex("^[0-9]{1}$"))

                "jodi" -> number.matches(Regex("^[0-9]{2}$"))

                "patti" -> number.matches(Regex("^[0-9]{3}$"))

                "cp" -> number.matches(Regex("^[0-9]{5}$"))

                else -> true
            }
        }

        fun error(
            gameCode: String
        ): String {

            return when (gameCode.lowercase()) {

                "single" -> "Enter 1 digit number"

                "jodi" -> "Enter 2 digit number"

                "patti" -> "Enter 3 digit number"

                "cp" -> "Enter 5 digit number"

                else -> "Invalid Number"

            }

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

