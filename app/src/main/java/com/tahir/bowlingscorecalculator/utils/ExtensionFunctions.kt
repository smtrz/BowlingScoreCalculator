package com.tahir.bowlingscorecalculator.utils

import android.widget.EditText

val TOTAL_POSSIBLE_FRAMES = 10
val MAX_VALUE = 10
fun List<Int>.validate(frameNo: Int): Boolean {
    if (sum() <= MAX_VALUE && frameNo != MAX_VALUE && size <= 2) {
        return true
    }
    if (frameNo == MAX_VALUE) {

        return true
    }
    return false
}

fun EditText.clear() {
    text.clear()
}

fun EditText.getData(): Int {
    if (text.toString().equals("")) {

        return 0
    }
    return text.toString().toInt()
}
