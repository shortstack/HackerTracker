package com.shortstack.hackertracker.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.nes_controler.view.*

class NESControllerView(context: Context) : LinearLayout(context) {

    val PRESS_A = 0
    val PRESS_B = 1

    val PRESS_START = 2
    val PRESS_SELECT = 3

    val PRESS_UP = 4
    val PRESS_RIGHT = 5
    val PRESS_DOWN = 6
    val PRESS_LEFT = 7

    val BUFFER_LENGTH = 10
    val pressArray = IntArray(BUFFER_LENGTH)
    var currPos = 0


    val CODE_1 = "0000000000"


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.nes_controler, null)
        addView(view)

        nes_a.setOnClickListener { pressButton(nes_a) }
        nes_b.setOnClickListener { pressButton(nes_b) }

        nes_select.setOnClickListener { pressButton(nes_select) }
        nes_start.setOnClickListener { pressButton(nes_start) }

        nes_up.setOnClickListener { pressButton(nes_up) }
        nes_right.setOnClickListener { pressButton(nes_right) }
        nes_down.setOnClickListener { pressButton(nes_down) }
        nes_left.setOnClickListener { pressButton(nes_left) }
    }

    private fun pressButton(view: View) {

        val button = when (view) {
            nes_a -> PRESS_A
            nes_b -> PRESS_B

            nes_select -> PRESS_SELECT
            nes_start -> PRESS_START

            nes_up -> PRESS_UP
            nes_right -> PRESS_RIGHT
            nes_down -> PRESS_DOWN
            nes_left -> PRESS_LEFT

            else -> PRESS_A
        }

        pressArray[currPos++] = button

        if (currPos == BUFFER_LENGTH) {
            printArray()
            currPos = 0

            if (getString() == CODE_1) {
                nes_skull.setImageDrawable(context.getDrawable(R.drawable.skull_25))
            }


        }
    }

    private fun printArray() {
        var result = getString()

        Logger.d(result)
    }

    private fun getString(): String {
        var result = ""
        pressArray.forEach {
            result += it.toString()
        }
        return result
    }


}