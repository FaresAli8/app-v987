package com.procalc.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.procalc.app.logic.CalculatorEngine

data class HistoryItem(val expression: String, val result: String)

class CalculatorViewModel : ViewModel() {
    var display by mutableStateOf("")
        private set

    var resultPreview by mutableStateOf("")
        private set

    var history by mutableStateOf(listOf<HistoryItem>())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> {
                display += action.number
                calculatePreview()
            }
            is CalculatorAction.Operator -> {
                display += " ${action.op} "
            }
            is CalculatorAction.Clear -> {
                display = ""
                resultPreview = ""
            }
            is CalculatorAction.Delete -> {
                if (display.isNotEmpty()) {
                    display = display.dropLast(1).trim()
                    calculatePreview()
                }
            }
            is CalculatorAction.Calculate -> {
                performFinalCalculation()
            }
            is CalculatorAction.Special -> {
                display += action.symbol
            }
        }
    }

    private fun calculatePreview() {
        if (display.isEmpty()) {
            resultPreview = ""
            return
        }
        try {
            val res = CalculatorEngine.evaluate(display)
            resultPreview = formatResult(res)
        } catch (e: Exception) {
            resultPreview = ""
        }
    }

    private fun performFinalCalculation() {
        if (display.isEmpty()) return
        try {
            val res = CalculatorEngine.evaluate(display)
            val formattedRes = formatResult(res)
            history = listOf(HistoryItem(display, formattedRes)) + history
            display = formattedRes
            resultPreview = ""
        } catch (e: Exception) {
            resultPreview = "Error"
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) value.toLong().toString() else "%.4f".format(value).trimEnd('0').trimEnd('.')
    }
}

sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operator(val op: String) : CalculatorAction()
    data class Special(val symbol: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
}