package com.jminnovatech.sbclub.data.api

data class CurrentPreviewResponse(
    val status:Boolean,
    val round: RoundData,
    val server_time:String,
    val total_collection:Int,
    val round_id: Int,
    val result_number: Int,
    val normal_result_out: String,
    val patti_collection: Int,
    val patti_numbers: List<PattiItem>,
    val patti_result_out: String,
    val patti_result_number: Int?,

    val numbers: List<NumberPreview>
)

data class RoundData(
    val id:Int,
    val start_time:String,
    val result_time:String
)

data class NumberPreview(
    val number:Int,
    val total_users:Int,
    val total_bet:Int,
    val payout:Int,
    val profit:Int
)

data class PattiItem(
    val number: Int,
    val total_users: Int,
    val total_bet: Int
)