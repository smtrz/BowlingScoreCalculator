package com.tahir.bowlingscorecalculator.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tahir.bowlingscorecalculator.models.Frame
import com.tahir.bowlingscorecalculator.utils.ShotType
import com.tahir.bowlingscorecalculator.utils.TOTAL_POSSIBLE_FRAMES
import kotlinx.coroutines.launch

class ScoreCardViewModel : ViewModel() {
    private val TAG = ScoreCardViewModel::class.qualifiedName

    private val bowlingGame: MutableList<Frame> = mutableListOf()
    private val _bowlingGameLive: MutableLiveData<List<Frame>?> = MutableLiveData()
    val bowlingGameLive: LiveData<List<Frame>?> = _bowlingGameLive

    fun reset() {
        _bowlingGameLive.value = emptyList()
        bowlingGame.clear()
    }

    private fun createFrame(frameInFocus: List<Int>): Frame {
        val frameType = Frame.getShotTypeFromRolls(frameInFocus)
        return Frame(
            frameScore = frameInFocus.sum(),
            pinsKnockedList = frameInFocus.toMutableList(),
            type = frameType
        )
    }

    fun calculateFrameScoreUpdate(frameInFocus: List<Int>) {
        viewModelScope.launch {
            if (bowlingGame.size <= TOTAL_POSSIBLE_FRAMES) {
                val currentFrame = createFrame(frameInFocus)
                bowlingGame.add(currentFrame)
                bowlingGame.forEachIndexed { index, currentFrame ->
                    Log.d(TAG, "Iterating over all the frames.")
                    // Checking if the ShotType of current frame is STRIKE and the score is not yet calculated
                    if (currentFrame.type == ShotType.STRIKE && currentFrame.totalScore == 0
                        && index < TOTAL_POSSIBLE_FRAMES - 1) {
                        // calculate the bonus for the STRIKE
                            val (applyBonus, bonus) = currentFrame.calculateStrikeBonus(bowlingGame, index)
                            if (applyBonus) {
                                currentFrame.totalScore =
                                    bowlingGame.maxOf { it.totalScore } +
                                            bowlingGame[index].pinsKnockedList.sum() + bonus
                            }


                    }
                    // Checking if the ShotType of current frame is SPARE and the score is not yet calculated
                    else if (currentFrame.totalScore == 0 && currentFrame.type == ShotType.SPARE && index < TOTAL_POSSIBLE_FRAMES - 1) {
                        if (bowlingGame.lastIndex >= index + 1) {
                            // calculate and add the bonus for SPARE
                            currentFrame.totalScore =
                                bowlingGame.maxOf { it.totalScore } +
                                        bowlingGame[index].pinsKnockedList.sum() +
                                        bowlingGame[index + 1].pinsKnockedList[0]
                        }
                    }
                    // Checking if the ShotType of current frame is NORMAL and the score is not yet calculated
                    else {
                        if (currentFrame.totalScore == 0 && index < TOTAL_POSSIBLE_FRAMES - 1) {
                            currentFrame.totalScore =
                                currentFrame.frameScore + (bowlingGame.maxByOrNull { it.totalScore }?.totalScore
                                    ?: 0)
                        } else {
                            // 10th Frame and score needs to be calculated
                            if (currentFrame.totalScore == 0 && index == TOTAL_POSSIBLE_FRAMES - 1) {
                                currentFrame.totalScore =
                                    bowlingGame.maxOf { it.totalScore } + bowlingGame[index].pinsKnockedList.sum()
                            }
                        }
                    }
                }
            }
            Log.d(TAG, bowlingGame.toString())
            _bowlingGameLive.postValue(bowlingGame)
        }


    }

}