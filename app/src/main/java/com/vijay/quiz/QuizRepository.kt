//package com.vijay.quiz
//
//import org.json.JSONArray
//
//// QuizRepository.kt
//class QuizRepository {
//    fun loadQuestions(): List<Question> {
//        val jsonString = loadJSONFromAsset("questions.json")
//        val questionList = parseJSON(jsonString)
//        return questionList
//    }
//
//    private fun parseJSON(jsonString: String): List<Question> {
//        val jsonArray = JSONArray(jsonString)
//        val questions = mutableListOf<Question>()
//        for (i in 0 until jsonArray.length()) {
//            val questionObject = jsonArray.getJSONObject(i)
//            val countryCode = questionObject.getString("country_code")
//            val countries = questionObject.getJSONArray("countries")
//            val countryList = mutableListOf<Country>()
//            for (j in 0 until countries.length()) {
//                val country = countries.getJSONObject(j)
//                countryList.add(Country(country.getString("country_name"), country.getInt("id")))
//            }
//            questions.add(Question(questionObject.getInt("answer_id"), countryCode, countryList))
//        }
//        return questions
//    }
//
//    private fun loadJSONFromAsset(fileName: String): String {
//        // Load JSON from the assets
//    }
//}
