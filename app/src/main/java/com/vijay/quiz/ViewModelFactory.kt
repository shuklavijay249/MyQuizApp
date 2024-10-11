package com.vijay.quiz
import FlagRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FlagQuizViewModelFactory(
    private val repository: FlagRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlagQuizViewModel::class.java)) {
            return FlagQuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

