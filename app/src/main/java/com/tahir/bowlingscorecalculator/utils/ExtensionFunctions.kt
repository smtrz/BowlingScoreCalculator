package com.tahir.bowlingscorecalculator.utils

import android.widget.EditText

// clears the text from edit text
fun EditText.clear() {
    text.clear()
}

/* checks if the data is not there , then return 0
@return the data in the edittext
 */
fun EditText.getData(): Int {
    if (text.toString().equals("")) {
        return 0
    }
    return text.toString().toInt()
}
