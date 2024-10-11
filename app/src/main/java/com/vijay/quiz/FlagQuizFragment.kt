package com.vijay.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.vijay.quiz.databinding.FragmentFlagQuizBinding
import java.io.IOException


class FlagQuizFragment : Fragment() {

    private lateinit var binding: FragmentFlagQuizBinding

    private val viewModel: FlagQuizViewModel by viewModels {
        FlagQuizViewModelFactory((requireActivity().application as MyApp).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout with data binding
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_flag_quiz, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()
        return binding.root
    }

    // Setup LiveData observers
    private fun setupObservers() {

        viewModel.isQuizCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                val action = FlagQuizFragmentDirections.actionFlagQuizFragmentToResultFragment(viewModel.score.value ?: 0)
                findNavController().navigate(action)
            }
        }

        // Observe the current question and display it when changed
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            question?.let { displayQuestion(it) }
        }

        // Observe the remaining time and update the timer text
        viewModel.timeLeft.observe(viewLifecycleOwner) { time ->
            binding.timerTextView.text = String.format("%02d:%02d", time / 60, time % 60)
        }

        // Observe the feedback visibility state and show/hide feedback accordingly
        viewModel.showFeedback.observe(viewLifecycleOwner) { show ->
            if (show) {
                showAnswerFeedback()
            } else {
                clearOptionFeedback()
            }
        }
    }

    // Display the current question and its options
    private fun displayQuestion(question: Question) {
        loadFlagImage(question.country_code)

        // Reset option buttons to default state
        resetOptions()

        // Set option button texts and click listeners
        binding.optionButton1.apply {
            text = question.countries[0].country_name
            setOnClickListener { onOptionSelected(0) }
            }
        binding.optionButton2.apply {
            text = question.countries[1].country_name
            setOnClickListener { onOptionSelected(1) }
    }
        binding.optionButton3.apply {
            text = question.countries[2].country_name
            setOnClickListener { onOptionSelected(2) }
}
        binding.optionButton4.apply {
            text = question.countries[3].country_name
            setOnClickListener { onOptionSelected(3) }
    }
}

    // Load the flag image for the given country code from assets
    private fun loadFlagImage(countryCode: String) {
        val flagImageView = binding.flagImageView
        val assetManager = requireContext().assets

        try {
            val inputStream = assetManager.open("flags/${countryCode.lowercase()}.png")
            val drawable = Drawable.createFromStream(inputStream, null)
            flagImageView.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Reset option buttons to their default states
    private fun resetOptions() {
        val defaultColor = ContextCompat.getColorStateList(requireContext(), R.color.purple_500)
        binding.optionButton1.apply { isEnabled = true; backgroundTintList = defaultColor }
        binding.optionButton2.apply { isEnabled = true; backgroundTintList = defaultColor }
        binding.optionButton3.apply { isEnabled = true; backgroundTintList = defaultColor }
        binding.optionButton4.apply { isEnabled = true; backgroundTintList = defaultColor }
    }

    // Handle the event when an option is selected
    private fun onOptionSelected(optionIndex: Int) {
        val selectedButton = getOptionButton(optionIndex)
        selectedButton?.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.teal_200)

        val selectedCountryId = viewModel.currentQuestion.value?.countries?.get(optionIndex)?.id ?: -1
        viewModel.checkAnswer(selectedCountryId)
        }

    // Show feedback for the selected answer and correct answer
    private fun showAnswerFeedback() {
        val selectedOptionIndex = viewModel.selectedOptionId.value
        val correctAnswerIndex = viewModel.currentQuestion.value?.countries?.indexOfFirst { it.id == viewModel.currentQuestion.value?.answer_id }

        // Check if correctAnswerIndex is valid
        if (correctAnswerIndex != null && correctAnswerIndex in 0..3) {
            // Display feedback text
            binding.feedbackTextView.apply {
                visibility = View.VISIBLE
                text = if (viewModel.isAnswerCorrect.value == true) "CORRECT" else "WRONG"
        }

            // Highlight the correct and incorrect options
            val buttons = getAllOptionButtons()
            val correctColor = ContextCompat.getColorStateList(requireContext(), R.color.correct_answer_color)
            val incorrectColor = ContextCompat.getColorStateList(requireContext(), R.color.incorrect_answer_color)

            buttons.forEachIndexed { index, button ->
                button.isEnabled = false
                button.backgroundTintList = if (index == correctAnswerIndex) correctColor else incorrectColor
    }

            selectedOptionIndex?.let { index ->
                if (index in buttons.indices) { // Check if selectedOptionIndex is within bounds
                    buttons[index].backgroundTintList = if (index == correctAnswerIndex) correctColor else incorrectColor
                } else {
                    Log.e("FlagQuizFragment", "Invalid selectedOptionIndex: $index")
                }
            }
        } else {
            // Handle invalid correctAnswerIndex (e.g., log an error, show a message)
            Log.e("FlagQuizFragment", "Invalid correctAnswerIndex: $correctAnswerIndex")
    }
    }

    // Clear the feedback UI state
    private fun clearOptionFeedback() {
        binding.feedbackTextView.visibility = View.GONE
    }

    // Helper function to get the option button by index
    private fun getOptionButton(index: Int) = when (index) {
        0 -> binding.optionButton1
        1 -> binding.optionButton2
        2 -> binding.optionButton3
        3 -> binding.optionButton4
        else -> null
    }

    // Helper function to get all option buttons as a list
    private fun getAllOptionButtons() = listOf(
        binding.optionButton1, binding.optionButton2, binding.optionButton3, binding.optionButton4
    )
}

@BindingAdapter("flagImage")
fun setFlagImage(imageView: ImageView, countryCode: String?) {
    countryCode?.let {
        val resourceId = imageView.context.resources.getIdentifier(
            it.lowercase(), "drawable", imageView.context.packageName
        )
        imageView.setImageResource(resourceId)
    }
}

@BindingAdapter("isCorrect", "selectedOptionId")
fun highlightCorrectOption(button: Button, isCorrect: LiveData<Boolean>, selectedOptionId: LiveData<Int>) {
    val context = button.context

    // Ensure both LiveData values are observed and have non-null values
    val lifecycleOwner = button.findViewTreeLifecycleOwner()
    lifecycleOwner?.let {
        isCorrect.observe(it) { correct ->
            selectedOptionId.observe(it) { selectedId ->
                if (correct == true && selectedId == button.id) {
                    button.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                } else {
                    button.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.purple_500) // default color
                    )
                }
            }
        }
    }
}

