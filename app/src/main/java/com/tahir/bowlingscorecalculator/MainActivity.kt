package com.tahir.bowlingscorecalculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tahir.bowlingscorecalculator.ui.viewmodels.ScoreCardViewModel
import com.tahir.bowlingscorecalculator.utils.MAX_VALUE
import com.tahir.bowlingscorecalculator.utils.clear
import com.tahir.bowlingscorecalculator.utils.getData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.login.frame
import kotlinx.android.synthetic.main.login.reset
import kotlinx.android.synthetic.main.login.result
import kotlinx.android.synthetic.main.login.submit
import kotlinx.android.synthetic.main.login.turn1
import kotlinx.android.synthetic.main.login.turn2
import kotlinx.android.synthetic.main.login.turn3

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var rolls: MutableList<Int> = mutableListOf()
    private val scoreCardViewModel: ScoreCardViewModel by viewModels()
    private var frameNo: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
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
            if (frameNo == MAX_VALUE - 1) {
                // checking if we need to take the input from third roll in case of last frame.
                if (turn1.getData() == MAX_VALUE || turn2.getData() == MAX_VALUE || turn1.getData() + turn1.getData() == MAX_VALUE) {
                    rolls.add(turn3.getData())


                }
            }
            // firing the event on the view model
            scoreCardViewModel.calculateFrameScoreUpdate(rolls)
// clearing all fields and rolls array
            clearAll()
        }
        reset.setOnClickListener {
            resetAll()
        }
        scoreCardViewModel.gameFrameLive.observe(this) {
            result.text = it.toString()
            frameNo = it?.size ?: 1
            frame.text = "FRAME $frameNo"

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
        turn3.isEnabled = false

    }
}

