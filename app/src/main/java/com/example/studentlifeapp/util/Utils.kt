package com.example.studentlifeapp.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.studentlifeapp.data.Classification
import com.example.studentlifeapp.data.Event
import java.lang.Exception

class Utils {
    interface EventDetailClickListener{
        fun onEventClicked(tag:String, event:Event)
    }


}
fun calculateClassification(percentage:Double): Classification {
    return if(percentage> 100){
        Classification.INVALID
    }
    else if (percentage in 70.0..100.0){
        Classification.FIRST
    }
    else if (percentage>=60){
        Classification.UPPER_SECOND
    }
    else if (percentage>=50){
        Classification.LOWER_SECOND
    }
    else if (percentage>=40){
        Classification.PASS
    }
    else if (percentage<40 && percentage>=0){
        Classification.FAIL
    }
    else{
        throw Exception("Invalid percentage")
    }
}

fun EditText.addDecimalLimiter(maxLimit:Int=2){
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val str = this@addDecimalLimiter.text!!.toString()
            if(str.isEmpty()) return
            val str2 = decimalLimiter(str,maxLimit)
            if(str2!=str){
                this@addDecimalLimiter.setText(str2)
                val pos = this@addDecimalLimiter.text!!.length
                this@addDecimalLimiter.setSelection(pos)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

    var str = string
    if (str[0] == '.') str = "0$str"
    val max = str.length

    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char

    val decimalCount = str.count { ".".contains(it) }

    if (decimalCount > 1)
        return str.dropLast(1)

    while (i < max) {
        t = str[i]
        if (t != '.' && !after) {
            up++
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > MAX_DECIMAL)
                return rFinal
        }
        rFinal += t
        i++
    }
    return rFinal
}
