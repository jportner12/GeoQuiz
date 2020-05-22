package com.bigranch.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel: ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var cheatsUsed = 0
    var currentIndex = 0
    var correctAnswers = 0
    var questionBankSize = questionBank.size
    val cheatingList = booleanArrayOf(false,false,false,false,false,false)
    val isCheater : Boolean
        get() = cheatingList[currentIndex]

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    fun cheater() {
        cheatingList[currentIndex] = true
    }

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBankSize
        if (currentIndex == questionBankSize) {
            correctAnswers = 0
        }
    }

    fun moveToBack() {
        currentIndex = (currentIndex - 1)
        if (currentIndex == -1)
            currentIndex = 0
    }

}