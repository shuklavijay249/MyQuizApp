package com.vijay.quiz

data class Question(
    val answer_id: Int,
    val countries: List<Country>,
    val country_code: String
)