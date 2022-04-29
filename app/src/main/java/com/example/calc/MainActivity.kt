package com.example.calc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calc.databinding.LayoutMainBinding
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var accumulator = 0.0
    private var buffer: String = "0"
    private var sign: String = ""
    private var flag: Boolean = false
    private val dec = DecimalFormat("#,###.########")

    private lateinit var binding: LayoutMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numeralButtons = arrayOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )
        for (button in numeralButtons) button.setOnClickListener {
            addToBuffer(it)
            flag = false
        }

        fun process(button: Button, block: MainActivity.() -> Unit) {
            if (!flag) {
                writeToAccumulator(buffer)
            }
            clearBuffer()
            block()
            flag = false
            setActiveColor(button)
        }

        binding.addButton.setOnClickListener {
            process(it as Button) { setPlus() }
        }

        binding.subtractButton.setOnClickListener {
            process(it as Button) { setMinus() }
        }

        binding.multiplyButton.setOnClickListener {
            process(it as Button) { setMultiply() }
        }

        binding.divideButton.setOnClickListener {
            process(it as Button) { setDivide() }
        }

        binding.percentButton.setOnClickListener {
            calcPercent(buffer)
            writeToOutput(buffer)
        }

        binding.equalButton.setOnClickListener {
            if (buffer == "0") {
                buffer = accumulator.toString()
                setAllBtnDisabled()
            }
            val result = execute(accumulator, buffer.toDouble())
            if (result.isNaN()) {
                writeError()
                return@setOnClickListener
            } else writeToAccumulator(result.toString())

            if (ceil(result) == floor(result)) {
                writeToOutput(result.toInt().toString())
            } else writeToOutput(result.toString())

            flag = true
        }

        binding.clearButton.setOnClickListener {
            clear(binding.clearButton.text == "AC")
            writeToOutput(buffer)
        }

        binding.dotButton.setOnClickListener { addToBuffer(it) }

        binding.numberSignButton.setOnClickListener {
            if (flag) {
                buffer = accumulator.toString()
                flag = false
            }
            buffer = if (buffer[0] == '-') {
                buffer.drop(1)
            } else "-$buffer"
            writeToOutput(buffer)
        }
    }

    private fun addToBuffer(view: View?) {
        binding.clearButton.text = "C"
        setAllBtnDisabled()
        if (flag) clearBuffer()
        if (buffer.length < 10) {
            buffer = buffer.plus((view as Button).text)
            writeToOutput(buffer)
        }
    }

    private fun writeToOutput(output: String) {
        binding.textView2.apply {
            text = dec.format(output.toDouble()).toString()
        }
    }

    private fun writeError() {
        binding.textView2.apply {
            text = "Error"
        }
    }

    private fun writeToAccumulator(value: String) {
        accumulator = value.toDouble()
    }

    private fun clearBuffer() {
        buffer = "0"
    }

    private fun clear(all: Boolean) {
        buffer = "0"
        if (all) {
            accumulator = 0.0
            sign = ""
            setAllBtnDisabled()
        }
        binding.clearButton.text = "AC"
    }

    private fun calcPercent(value: String) {
        if (!flag) accumulator = value.toDouble()
        accumulator /= 100
        buffer = accumulator.toString()
    }

    private fun setActiveColor(btn: Button) {
        btn.backgroundTintList = ColorStateList.valueOf(getColor(R.color.white))
        btn.setTextColor(getColor(R.color.orange))
    }

    private fun setAllBtnDisabled() {
        setDisableColor(binding.addButton)
        setDisableColor(binding.subtractButton)
        setDisableColor(binding.multiplyButton)
        setDisableColor(binding.divideButton)
    }

    private fun setDisableColor(btn: Button) {
        btn.backgroundTintList = ColorStateList.valueOf(getColor(R.color.orange))
        btn.setTextColor(getColor(R.color.white))
    }

    private fun setPlus() {
        sign = "+"
    }

    private fun setMinus() {
        sign = "-"
    }

    private fun setMultiply() {
        sign = "*"
    }

    private fun setDivide() {
        sign = "/"
    }

    private fun execute(a: Double, b: Double): Double {
        return when (sign) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> {
                if (b == 0.0) {
                    clear(true)
                    return Double.NaN
                } else return a / b
            }
            else -> throw RuntimeException()
        }
    }
}