package com.vijay.quiz

import FlagRepository
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class FlagQuizViewModel(private val repository: FlagRepository) : ViewModel() {

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> get() = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val questions: List<Question> = repository.getQuestions()
    private var questionIndex = 0

    // LiveData for current question number
    private val _currentQuestionNumber = MutableLiveData<Int>()
    val currentQuestionNumber: LiveData<Int> get() = _currentQuestionNumber

    // Total number of questions
    val totalQuestions: Int get() = questions.size

    private val _isQuizCompleted = MutableLiveData(false)
    val isQuizCompleted: LiveData<Boolean> get() = _isQuizCompleted

    private val _selectedOptionId = MutableLiveData<Int>()
    val selectedOptionId: LiveData<Int> = _selectedOptionId

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect: LiveData<Boolean> = _isAnswerCorrect

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> get() = _timeLeft

    private val _showFeedback = MutableLiveData<Boolean>()
    val showFeedback: LiveData<Boolean> get() = _showFeedback

    private var questionTimer: CountDownTimer? = null

    private var intervalTimer: CountDownTimer? = null

    init {
        loadNextQuestion() // Immediately load the first question
    }

    private fun loadNextQuestion() {
        if (questionIndex < questions.size) {
            _currentQuestion.value = questions[questionIndex]
            _currentQuestionNumber.value = questionIndex + 1 // Update question number
            questionIndex++
            startQuestionTimer()
        } else {
            _isQuizCompleted.value = true
            questionTimer?.cancel()
            intervalTimer?.cancel()
        }
    }

    private fun startQuestionTimer() {
        questionTimer?.cancel()
        _timeLeft.value = 1 // Reset timer display to 30 seconds

        questionTimer = object : CountDownTimer(30000, 1000) { // 30 seconds timer for each question
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                loadNextQuestion()  // Automatically move to the next question
            }
        }.start()
    }

    fun increaseScore() {
        _score.value = _score.value?.plus(1)
    }

    fun checkAnswer(selectedCountryId: Int) {
        val correctAnswerId = currentQuestion.value?.answer_id
        if (correctAnswerId == selectedCountryId) {
            increaseScore()
        }
        loadNextQuestion()
    }

    override fun onCleared() {
        super.onCleared()
        questionTimer?.cancel()
        intervalTimer?.cancel()
    }
}


