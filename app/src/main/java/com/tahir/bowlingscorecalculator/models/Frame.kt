package com.tahir.bowlingscorecalculator.models

import com.tahir.bowlingscorecalculator.utils.MAX_VALUE

/*
* A model to keep state of each frame
* and help with UI representation for the most part
 */
data class Frame(
    var pinsKnockedList: List<Int>,
    var totalScore: Int = 0,
    var attemptScore: Int = 0,
    var type: Type = Type.NORMAL
) {
    enum class Type {
        STRIKE, SPARE, NORMAL
    }

    fun calculateBonus(gameFrame: MutableList<Frame>, index: Int): Pair<Boolean, Int> {
        var applyBonus = false
        var bonus = 0

        if (gameFrame[index + 1].type == Type.NORMAL || gameFrame.get(
                index + 1
            ).type == Type.SPARE
        ) {
            applyBonus = true
            bonus = gameFrame.get(index + 1).pinsKnockedList.sum()
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
            // last one
            else if(gameFrame.size== MAX_VALUE){
                applyBonus = true
                bonus =
                    gameFrame[index + 1].pinsKnockedList.get(0)+pinsKnockedList.get(1)

            }


        }
        return Pair(applyBonus, bonus)
    }
}
