package com.tahir.bowlingscorecalculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tahir.bowlingscorecalculator.ui.viewmodels.ScoreCardViewModel
import com.tahir.bowlingscorecalculator.utils.MAX_VALUE
import com.tahir.bowlingscorecalculator.utils.clear
import com.tahir.bowlingscorecalculator.utils.getData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val rolls: MutableList<Int> = mutableListOf()
    private val scoreCardViewModel: ScoreCardViewModel by viewModels()
    private var frameNo: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        result.movementMethod = ScrollingMovementMethod()


        /**
        Currently no validation is in place, in order to use it properly
        we need to give the inputs precisely, tests can be performed from :
        https://www.bowlinggenius.com/
         */
        submit.setOnClickListener {
            rolls.add(turn1.getData())

            if (turn1.getData() < MAX_VALUE || frameNo == MAX_VALUE - 1) {
                rolls.add(turn2.getData())
            }
            // this is the last frame so lets the take the input from the third edit text
            if (frameNo >= MAX_VALUE - 1) {
                val sum = turn1.getData() + turn2.getData()
                if (turn1.getData() == MAX_VALUE || turn2.getData() == MAX_VALUE || sum == MAX_VALUE) {
                    rolls.add(turn3.getData())
                }
            }
            // firing the event on the view model
            scoreCardViewModel.calculateFrameScoreUpdate(rolls)


        }

        reset.setOnClickListener {
            resetAll()
        }

        scoreCardViewModel.bowlingGameLive.observe(this) {
            result.text = it?.joinToString("\n")
            frameNo = it?.size ?: 1
            frame.text = "FRAME $frameNo"
            clearAll()
        }
    }

    private fun clearAll() {
        rolls.clear()
        turn1.clear()
        turn2.clear()
        turn3.clear()
    }

    private fun resetAll() {
        clearAll()
        frameNo = 1
        scoreCardViewModel.reset()
        result.text = ""
    }
}
