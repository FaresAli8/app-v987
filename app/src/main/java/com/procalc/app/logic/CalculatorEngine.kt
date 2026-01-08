package com.procalc.app.logic

import java.util.*
import kotlin.math.*

/**
 * Robust Math Engine handling basic operations, powers, roots, and order of operations.
 */
object CalculatorEngine {

    fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        if (tokens.isEmpty()) return 0.0
        return parseExpression(LinkedList(tokens))
    }

    private fun tokenize(expr: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        val cleanExpr = expr.replace(" ", "").replace("×", "*").replace("÷", "/")
        
        while (i < cleanExpr.length) {
            val c = cleanExpr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < cleanExpr.length && (cleanExpr[i].isDigit() || cleanExpr[i] == '.')) {
                        sb.append(cleanExpr[i++])
                    }
                    result.add(sb.toString())
                    continue
                }
                c == '√' -> result.add("sqrt")
                c in "+-*/^()" -> result.add(c.toString())
            }
            i++
        }
        return result
    }

    private fun parseExpression(tokens: Queue<String>): Double {
        val values = Stack<Double>()
        val ops = Stack<String>()

        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3, "sqrt" to 4)

        while (tokens.isNotEmpty()) {
            val token = tokens.poll() ?: break
            
            when {
                token.toDoubleOrNull() != null -> values.push(token.toDouble())
                token == "(" -> ops.push(token)
                token == ")" -> {
                    while (ops.peek() != "(") {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.pop()
                }
                token == "sqrt" -> ops.push(token)
                precedence.containsKey(token) -> {
                    while (ops.isNotEmpty() && ops.peek() != "(" && precedence[ops.peek()]!! >= precedence[token]!!) {
                        val op = ops.pop()
                        if (op == "sqrt") {
                            values.push(sqrt(values.pop()))
                        } else {
                            values.push(applyOp(op, values.pop(), values.pop()))
                        }
                    }
                    ops.push(token)
                }
            }
        }

        while (ops.isNotEmpty()) {
            val op = ops.pop()
            if (op == "sqrt") {
                values.push(sqrt(values.pop()))
            } else {
                values.push(applyOp(op, values.pop(), values.pop()))
            }
        }
        return values.pop()
    }

    private fun applyOp(op: String, b: Double, a: Double): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else 0.0
            "^" -> a.pow(b)
            else -> 0.0
        }
    }
}