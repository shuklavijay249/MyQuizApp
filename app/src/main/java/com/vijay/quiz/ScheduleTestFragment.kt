package com.vijay.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vijay.quiz.databinding.FragmentScheduleTestBinding
import java.util.*

class ScheduleTestFragment : Fragment() {

    private lateinit var binding: FragmentScheduleTestBinding
    var selectedHour = 0
    var selectedMinute = 0
    var selectedSecond = 0
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleTestBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Input validation for hour, minute, second fields
        setupInputValidation()

        binding.saveButton.setOnClickListener {
            selectedHour = binding.hourInput.text.toString().toIntOrNull() ?: 0
            selectedMinute = binding.minuteInput.text.toString().toIntOrNull() ?: 0
            selectedSecond = binding.secondInput.text.toString().toIntOrNull() ?: 0

            if (selectedHour !in 0..23 || selectedMinute !in 0..59 || selectedSecond !in 0..59) {
                Toast.makeText(requireContext(), "Please enter valid time values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate the time in seconds and compare with the current time
            val totalTimeInSeconds = (selectedHour * 3600) + (selectedMinute * 60) + selectedSecond
            val currentTime = Calendar.getInstance()
            val challengeTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, selectedSecond)
            }

            val timeDifference = challengeTime.timeInMillis - currentTime.timeInMillis

            if (timeDifference <= 0) {
                Toast.makeText(requireContext(), "The scheduled time must be in the future", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Start the countdown 20 seconds before the scheduled time
            startCountdown(timeDifference - 20000) // Start timer 20 seconds before the challenge time
        }
    }

    private fun setupInputValidation() {
        binding.hourInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hour = s.toString().toIntOrNull() ?: 0
                if (hour !in 0..23) {
                    binding.hourInput.error = "Invalid hour (0-23)"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.minuteInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val minute = s.toString().toIntOrNull() ?: 0
                if (minute !in 0..59) {
                    binding.minuteInput.error = "Invalid minute (0-59)"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.secondInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val second = s.toString().toIntOrNull() ?: 0
                if (second !in 0..59) {
                    binding.secondInput.error = "Invalid second (0-59)"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun startCountdown(timeUntilChallenge: Long) {
        // Show the "Challenge will start in..." text 20 seconds before the challenge
        countdownTimer = object : CountDownTimer(timeUntilChallenge, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                if (secondsLeft <= 20) {
                    binding.countdownText.visibility = View.VISIBLE
                    binding.countdownText.text = "CHALLENGE WILL START IN ${secondsLeft}s"
                }
            }

            override fun onFinish() {
                // After the countdown ends, start the challenge
                navigateToFlagQuizFragment()
            }
        }.start()
            }

    private fun navigateToFlagQuizFragment() {
        val action = ScheduleTestFragmentDirections.actionScheduleTestFragmentToFlagQuizFragment()
            findNavController().navigate(action)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel() // Clean up timer if the view is destroyed
    }
}
