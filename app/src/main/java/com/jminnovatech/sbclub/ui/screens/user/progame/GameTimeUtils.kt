package com.jminnovatech.sbclub.ui.screens.user.progame

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object GameTimeUtils {

    private val inputFormat =
        SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    private val outputFormat =
        SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    fun formatTime(time: String): String {

        return try {

            val date = inputFormat.parse(time)

            outputFormat.format(date!!)

        } catch (e: Exception) {

            time

        }

    }

    fun formatToAmPm(time: String): String {

        return formatTime(time)

    }

    fun isRunning(
        startTime: String,
        endTime: String
    ): Boolean {

        return try {

            val now = Calendar.getInstance()

            val start = Calendar.getInstance()
            val end = Calendar.getInstance()

            val s = inputFormat.parse(startTime)
            val e = inputFormat.parse(endTime)

            start.set(
                Calendar.HOUR_OF_DAY,
                s!!.hours
            )

            start.set(
                Calendar.MINUTE,
                s.minutes
            )

            start.set(
                Calendar.SECOND,
                s.seconds
            )

            end.set(
                Calendar.HOUR_OF_DAY,
                e!!.hours
            )

            end.set(
                Calendar.MINUTE,
                e.minutes
            )

            end.set(
                Calendar.SECOND,
                e.seconds
            )

            now.after(start) && now.before(end)

        } catch (e: Exception) {

            false

        }

    }

    fun isLocked(
        startTime: String
    ): Boolean {

        return try {

            val now = Calendar.getInstance()

            val start = Calendar.getInstance()

            val s = inputFormat.parse(startTime)

            start.set(
                Calendar.HOUR_OF_DAY,
                s!!.hours
            )

            start.set(
                Calendar.MINUTE,
                s.minutes
            )

            start.set(
                Calendar.SECOND,
                s.seconds
            )

            now.before(start)

        } catch (e: Exception) {

            false

        }

    }

}