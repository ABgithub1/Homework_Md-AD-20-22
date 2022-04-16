package com.example.homework_md_ad_20_22.homework_1

import java.util.*
import kotlin.collections.HashMap

interface AbstractStringIterator {
    operator fun hasNext(): Boolean
    operator fun next(): String?
}

interface AbstractOperatorsPriorities {

    fun getPriority(operator: String?): Int?

    fun addOperator(operator: String?, priority: Int)

    fun deleteOperator(operator: String?)
}

internal class OperatorsPriorities : AbstractOperatorsPriorities {

    private var map: MutableMap<String?, Int>

    constructor(map: HashMap<String?, Int>) {
        this.map = map
    }

    constructor() {
        map = HashMap()
    }

    override fun getPriority(operator: String?): Int? {
        return map[operator]
    }

    override fun addOperator(operator: String?, priority: Int) {
        map[operator] = priority
    }

    override fun deleteOperator(operator: String?) {
        map.remove(operator)
    }
}

internal class StringIterator(string: String) : AbstractStringIterator {
    private val string: String?
    private var currentIndex: Int

    override fun hasNext(): Boolean {
        return string != null &&
                !string.isEmpty() && currentIndex < string.length
    }

    override fun next(): String? {
        val operand = StringBuilder()
        var element = string!![currentIndex]
        while (Character.isDigit(element) || element == '.' || currentIndex == 0 && element == '-' ||
            currentIndex > 0 && string[currentIndex - 1] == '(' && element == '-'
        ) {
            operand.append(element)
            currentIndex++
            if (currentIndex == string.length) return operand.toString()
            element = string[currentIndex]
        }
        if (operand.length != 0) return operand.toString()
        currentIndex++
        return Character.toString(element)
    }

    init {
        this.string = string.replace(" ", "")
        currentIndex = 0
    }
}

internal class Calculator(
    private val priorities: AbstractOperatorsPriorities,
    private val iterator: AbstractStringIterator
) {
    private val numbers: Stack<Double?>
    private val operators: Stack<String?>

    @get:Throws(NullPointerException::class, EmptyStackException::class)
    val result: Double?
        get() {
            while (iterator.hasNext()) {
                val element = iterator.next()
                try {
                    val number = element!!.toDouble()
                    numbers.push(number)
                } catch (e: NumberFormatException) {
                    if (element == "(") {
                        operators.push(element)
                    } else if (element == ")") {
                        while (operators.peek() != "(") calculate()
                        operators.pop()
                    } else {
                        if (operators.empty()) {
                            operators.push(element)
                        } else {
                            val priority = priorities.getPriority(element)
                            while (!operators.empty() && operators.peek() != "(" && operators.peek() != ")" && priority!! <= priorities.getPriority(
                                    operators.peek()
                                )!!
                            ) {
                                calculate()
                            }
                            operators.push(element)
                        }
                    }
                }
            }
            while (!operators.empty()) {
                calculate()
            }
            return numbers.pop()
        }

    @Throws(EmptyStackException::class)
    private fun calculate() {
        val operator = operators.pop()
        val n2 = numbers.pop()
        val n1 = numbers.pop()
        var result: Double? = null
        when (operator) {
            "+" -> result = n1!! + n2!!
            "-" -> result = n1!! - n2!!
            "*" -> result = n1!! * n2!!
            "/" -> result = n1!! / n2!!
        }
        numbers.push(result)
    }

    init {
        numbers = Stack()
        operators = Stack()
    }
}

fun main() {
    println("Введите выражение, например (5+5)/3+7*9")
    val expression: String = readLine().toString()
    if (expression.isEmpty()) {
        println("Ошибка ввода")
        return
    }
    val priorities: AbstractOperatorsPriorities = OperatorsPriorities()
    priorities.addOperator("+", 1)
    priorities.addOperator("-", 1)
    priorities.addOperator("*", 2)
    priorities.addOperator("/", 2)
    val iterator: AbstractStringIterator = StringIterator(expression)
    val calculator = Calculator(priorities, iterator)
    val result: Double? = calculator.result
    println("Ответ $result")
}