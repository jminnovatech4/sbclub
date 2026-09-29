package com.sbclub.sbclub.data.model.user.BetItem
data class BetItem(
    val number: String,
    val amount: Int
)

data class BetRequest(
    val game_type: String,
    val bets: List<BetItem>
)
