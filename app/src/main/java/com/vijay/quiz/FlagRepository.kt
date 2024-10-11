import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.quiz.Question
import com.vijay.quiz.QuestionsResponse

class FlagRepository(private val context: Context) {

    fun getQuestions(): List<Question> {
        val json = loadJsonFromAssets("questions.json")

        // Parse the JSON as an object containing the questions array
        val responseType = object : TypeToken<QuestionsResponse>() {}.type
        val questionsResponse: QuestionsResponse = Gson().fromJson(json, responseType)

        return questionsResponse.questions
    }

    private fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
