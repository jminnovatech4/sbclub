package com.jminnovatech.sbclub.data.model.admin

data class MasterReportResponse(

    val status: Boolean,

    val data: List<MasterReport>
)

data class MasterReport(

    val master_id: Int,

    val master_name: String,

    val master_phone: String,

    val total_bet: Double,

    val total_commission: Double,

    val total_users: Int,

    val online_users: Int,

    val daily: List<MasterDaily>
)

data class MasterDaily(

    val date: String,

    val bet_amount: Double,

    val commission: Double,

    val users: Int,

    val online_users: Int
)