package com.bigranch.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_INDEX1 = "index1"
private const val KEY_INDEX2 = "index2"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatsTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX,0) ?:0
        quizViewModel.correctAnswers = savedInstanceState?.getInt(KEY_INDEX1,0) ?:0
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        backButton = findViewById(R.id.back_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatsTextView = findViewById(R.id.cheats_left)

        trueButton.setOnClickListener {view: View ->
            checkAnswer(true)
            if (quizViewModel.currentIndex == (quizViewModel.questionBankSize-1)) {
                val percent:Double = (quizViewModel.correctAnswers.toDouble()/quizViewModel.questionBankSize.toDouble()) * 100
                quizViewModel.correctAnswers = 0
                val t = Toast.makeText(this, "You got $percent% correct", Toast.LENGTH_SHORT)
                t.setGravity(Gravity.TOP,0,175)
                t.show()
            }
        }

        falseButton.setOnClickListener {view: View ->
            checkAnswer(false)
            if (quizViewModel.currentIndex == (quizViewModel.questionBankSize-1)) {
                val percent:Double = (quizViewModel.correctAnswers.toDouble()/quizViewModel.questionBankSize.toDouble()) *100
                quizViewModel.correctAnswers = 0
                val t = Toast.makeText(this, "You got $percent% correct", Toast.LENGTH_SHORT)
                t.setGravity(Gravity.TOP,0,175)
                t.show()
            }
        }

        cheatButton.setOnClickListener {view: View ->
            if (quizViewModel.cheatsUsed == 3){
                cheatButton.isClickable = false
                cheatButton.isEnabled = false
                val t = Toast.makeText(this, "You have used the maximum number of cheats",Toast.LENGTH_SHORT)
                t.setGravity(Gravity.TOP,0,175)
                t.show()
            } else {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val options =
                        ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                } else {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                }
                quizViewModel.cheatsUsed++
                cheatsTextView.setText("Cheats Used: ${quizViewModel.cheatsUsed}/3")
            }
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        backButton.setOnClickListener {
            quizViewModel.moveToBack()
            updateQuestion()
        }
        cheatsTextView.setText("Cheats Used: ${quizViewModel.cheatsUsed}/3")
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            val cheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false) ?:false
            if (cheater){
                quizViewModel.cheater()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_INDEX1, quizViewModel.correctAnswers)
        savedInstanceState.putBooleanArray(KEY_INDEX2, quizViewModel.cheatingList)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        changeButton(true)
    }

    private fun checkAnswer(userAnswer:Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        if (userAnswer == correctAnswer) quizViewModel.correctAnswers++
        val t = Toast.makeText(this, messageResId,Toast.LENGTH_SHORT)
        t.setGravity(Gravity.TOP,0,175)
        t.show()
        changeButton(false)
    }

    private fun changeButton(bool : Boolean) {
        trueButton.isEnabled = bool
        trueButton.isClickable = bool
        falseButton.isEnabled = bool
        falseButton.isClickable = bool
    }
}