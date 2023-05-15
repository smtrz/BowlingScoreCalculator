package com.tahir.bowlingscorecalculator.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahir.bowlingscorecalculator.models.Frame
import com.tahir.bowlingscorecalculator.utils.MAX_VALUE
import com.tahir.bowlingscorecalculator.utils.TOTAL_POSSIBLE_FRAMES
import kotlinx.coroutines.launch

class ScoreCardViewModel : ViewModel() {
    private val TAG = ScoreCardViewModel::class.qualifiedName

    private val gameFrame: MutableList<Frame> = mutableListOf()

    private val _gameFrameLive: MutableLiveData<List<Frame>?> =
        MutableLiveData()
    val gameFrameLive: MutableLiveData<List<Frame>?>
        get() = _gameFrameLive

    fun reset() {
        _gameFrameLive.value = emptyList()
        gameFrame.clear()
    }

    fun calculateFrameScoreUpdate(frameInFocus: List<Int>) {

        viewModelScope.launch {
            if (gameFrame.size <= TOTAL_POSSIBLE_FRAMES) {
                val frame =
                    Frame(
                        attemptScore = frameInFocus.sum(),
                        pinsKnockedList = frameInFocus.toMutableList()
                    )

                // it is a strike set the frame type STRIKE
                if (frameInFocus.any { it == MAX_VALUE }) {
                    Log.d(TAG, "Strike recorded.")
                    frame.type = Frame.Type.STRIKE

                }
                // it is a spare and set the frame type to SPARE
                else if (frameInFocus.sum() == MAX_VALUE) {
                    Log.d(TAG, "Spare recorded.")
                    frame.type = Frame.Type.SPARE

                }

                gameFrame.add(frame)
                gameFrame.forEachIndexed { index, dataframe ->
                    Log.d(TAG, "Iterating over the frame.")
// if it is a strike
                    if (dataframe.type == Frame.Type.STRIKE && dataframe.totalScore == 0 && index < TOTAL_POSSIBLE_FRAMES - 1) {

                        if (gameFrame.lastIndex >= index + 1) {
                            val (applyBonus, bonus) = frame.calculateBonus(gameFrame, index)
                            if (applyBonus) {
                                dataframe.totalScore =
                                    gameFrame.maxOf { it.totalScore } + gameFrame[index].pinsKnockedList.sum() + bonus
                            }


                        }

                    } else if (dataframe.totalScore == 0 && dataframe.type == Frame.Type.SPARE && index < TOTAL_POSSIBLE_FRAMES - 1) {
                        if (gameFrame.lastIndex >= index + 1) {
                            dataframe.totalScore =
                                gameFrame.maxOf { it.totalScore } + gameFrame[index].pinsKnockedList.sum() + gameFrame[index + 1].pinsKnockedList[0]

                        }
                    } else {
                        if (dataframe.totalScore == 0 && index < TOTAL_POSSIBLE_FRAMES - 1) {
                            dataframe.totalScore =
                                dataframe.attemptScore + (gameFrame.maxByOrNull { it.totalScore }?.totalScore
                                    ?: 0)

                        } else {
                            // reached the end of the frame..
                            if (dataframe.totalScore == 0 && index == TOTAL_POSSIBLE_FRAMES - 1) {
                                dataframe.totalScore =
                                    gameFrame.maxOf { it.totalScore } + gameFrame[index].pinsKnockedList.sum()
                            }
                        }


                    }

                }

            }
            Log.d(TAG, gameFrame.toString())
            _gameFrameLive.postValue(gameFrame)

        }


    }


}