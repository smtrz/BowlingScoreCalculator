package com.tahir.bowlingscorecalculator.models

import android.util.Log
import com.tahir.bowlingscorecalculator.utils.MAX_VALUE
import com.tahir.bowlingscorecalculator.utils.ShotType

/*
* A model to keep state of each frame
 */
data class Frame(
    val pinsKnockedList: List<Int>,
    var totalScore: Int = 0,
    val frameScore: Int = 0,
    var type: ShotType = ShotType.NORMAL
) {
    companion object {
        private val TAG = Frame::class.qualifiedName

        fun getShotTypeFromRolls(rolls: List<Int>): ShotType {
            return when {
                rolls.any { it == MAX_VALUE } -> {
                    Log.d(TAG, "Strike recorded.")
                    ShotType.STRIKE
                }

                rolls.sum() >= MAX_VALUE && rolls.size > 1 -> {
                    Log.d(TAG, "Spare recorded.")
                    ShotType.SPARE
                }

                else -> ShotType.NORMAL
            }

        }
    }

    /**
     * calculate the bonus for the strike
     * @param gameFrame,index
     * @return the pair with the bonus
     */

    fun calculateStrikeBonus(gameFrame: MutableList<Frame>, index: Int): Pair<Boolean, Int> {
        var applyBonus = false
        var bonus = 0
        if (gameFrame.lastIndex >= index + 1) {
            if (gameFrame[index + 1].type == ShotType.NORMAL || gameFrame.get(
                    index + 1
                ).type == ShotType.SPARE
            ) {
                applyBonus = true
                bonus = gameFrame.get(index + 1).pinsKnockedList.take(2).sum()
            } else {
                // it is again a strike
                if (gameFrame.getOrNull(index + 2) != null) {
                    applyBonus = true
                    bonus =
                        gameFrame[index + 1].pinsKnockedList.sum() + gameFrame.get(
                            index + 2
                        ).pinsKnockedList.get(
                            0
                        )
                }


            }
        }
        return Pair(applyBonus, bonus)
    }

}