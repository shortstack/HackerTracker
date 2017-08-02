package com.shortstack.hackertracker.View

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.TextView

class HackerTextView(context: Context, attributeSet: AttributeSet) : TextView(context, attributeSet) {
    val list = listOf("$", "#", "@", "!", "^", "//", "*")
    val delay: Long = 45

    var finalText: String = text.toString()

    var pos = 0

    var currentRandomCount = 0
    var maxRandomCount = 5


    init {
        setTimer()
    }

    private fun setTimer() {
        Handler().postDelayed({
            var text: String

            if (currentRandomCount++ <= maxRandomCount && finalText[pos] != ' ') {
                text = finalText.substring(0, pos)
                text += getRandomCharacter()
            } else {
                text = finalText.substring(0, ++pos)
                currentRandomCount = 0
                maxRandomCount = 2 + (Math.random() * 2).toInt()
            }

            this.text = text

            if (pos < finalText.length) setTimer()
        }, delay)
    }

    private fun getRandomCharacter() = list[(Math.random() * list.size).toInt()]

}