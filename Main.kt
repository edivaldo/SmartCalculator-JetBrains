package calculator

import java.math.BigInteger
import kotlin.math.pow

fun operator(_plus: Boolean, signs: String):Boolean?{
    var plus = _plus
    for(c in signs){
        if(c == '-'){
            plus = !plus
        }else if(c == '+'){
            plus = plus
        }else{
            return null
        }
    }
    return plus
}/*
fun operate(numbers:MutableList<String>){
    var operations = 0
    var plus = true
    var output = 0
    for(i in 0 until numbers.size) {
        when {
            numbers[i].toIntOrNull() != null -> {
                if(plus){
                    output += numbers[i].toBigInteger()
                }else{
                    output -= numbers[i].toBigInteger()
                }
                plus = true
            }
            numbers[i].contains("+") || numbers[i].contains("-") ->{
                ++operations
                val plusOrNull = operator(plus, numbers[i])
                if(plusOrNull == null){
                    println("Invalid expression")
                    return
                }else{
                    plus = plusOrNull
                }
            }//todo add * / ^
            else ->{
                println("Invalid expression")
                return
            }
        }
    }
    if(operations != 0) {
        println("$output")
    }else{
        println("Invalid expression")
    }
}*/
fun hasHigherPrecedence(element: String,element2: String): Boolean{
    return when{
        element == "^" -> true
        (element == "*" || element == "/") && (element2 == "+" || element2 =="-") -> true
        else -> false
    }
}
fun infixToPostfix(expression: List<String>):MutableList<String>?{
    val result = mutableListOf<String>()
    var number:BigInteger?
    val stack = mutableListOf<String>()
    var aux:String
    for(element in expression){
        number = element.toBigIntegerOrNull()
        when{
            number != null -> result.add(element)
            "[a-zA-Z]+".toRegex().matches(element) -> result.add(element)
            (stack.isEmpty() || stack.last() == "(") -> stack.add(element)
            element == "(" -> stack.add(element)
            element == ")"-> {
                while(stack.isNotEmpty() && stack.last() != "("){
                    result.add(stack.removeLast())
                }
                if(stack.isEmpty()){
                    return null
                }
                if(stack.last() == "("){
                    stack.removeLast()
                }
            }
            hasHigherPrecedence(element,stack.last()) -> stack.add(element)
            else -> {
                while(stack.isNotEmpty() && stack.last() != "(" && !hasHigherPrecedence(element, stack.last())){
                    result.add(stack.removeLast())
                }
                stack.add(element)
            }
        }
    }
    while(stack.isNotEmpty()){
        aux = stack.removeLast()
        if(aux == "("){
            return null
        }
        result.add(aux)
    }
    return result
}
fun postFixCalculate(infixNumbers:List<String>): String?{
    var stack = mutableListOf<String>()
    var auxResult:BigInteger
    var num1:BigInteger
    var num2:BigInteger
    for(element in infixNumbers) {
        when {
            element.toBigIntegerOrNull() != null -> stack.add(element)
            element == "+" -> {
                num2 = stack.removeLast().toBigInteger()
                num1 = stack.removeLast().toBigInteger()
                auxResult =  num1 + num2
                stack.add(auxResult.toString())
            }
            element == "-" -> {
                num2 = stack.removeLast().toBigInteger()
                num1 = stack.removeLast().toBigInteger()
                auxResult =  num1 - num2
                stack.add(auxResult.toString())
            }
            element == "*" -> {
                num2 = stack.removeLast().toBigInteger()
                num1 = stack.removeLast().toBigInteger()
                auxResult =  num1 * num2
                stack.add(auxResult.toString())
            }
            element == "/" -> {
                num2 = stack.removeLast().toBigInteger()
                num1 = stack.removeLast().toBigInteger()
                auxResult =  num1 / num2
                stack.add(auxResult.toString())
            }
            element == "^" -> {
                num2 = stack.removeLast().toBigInteger()
                num1 = stack.removeLast().toBigInteger()
                auxResult = num1.toDouble().pow(num2.toDouble()).toLong().toBigInteger()
                stack.add(auxResult.toString())
            }else -> {
                return null
            }
        }
    }
    return stack.removeLast()
}

fun main() {
    var auxContMinus:Int
    var input: String
    val regexVariableInsertion = "\\s*[a-zA-Z]+\\s*=\\s*-?([a-zA-Z]+|\\d+)\\s*".toRegex()
    val regexVariableInsertionValue = "[a-zA-Z]+\\s*=\\s*-?\\d+".toRegex()
    val regexVariableInsertionVariable = "[a-zA-Z]+\\s*=\\s*[a-zA-Z]+".toRegex()
    val regexVariable = "[a-zA-Z]+".toRegex()

    val regexValueFinal = "-?\\d+\\b".toRegex()
    val regexInvalidIdentifier = "([a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+)".toRegex()
    val regexAddSeparator = "[a-zA-Z]+|[0-9]+|\\*|\\++|-+|/|\\^|\\(|\\)".toRegex()
    val regexSoleNumber = "([+|-])?[0-9a-zA-Z]+".toRegex()
    val regexInvalidExpression ="\\*{2,}|/{2,}|\\^{2,}".toRegex()
    //val regexInvalidIdentifierGeneric = "([a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+)".toRegex()
    val listVariable = mutableMapOf<String,String>()
    newInput@ do {
        input = readln()
        if (input == "/exit"){
            println("Bye!")
            return
        }else if(input == "/help"){
            println("The program calculates the sum, multiplication, power, division or subtraction of numbers")
            continue
        }else if (input.isEmpty()){
            continue
        }
        else if(input.startsWith('/')){
            println("Unknown command")
            continue
        }else if((regexInvalidIdentifier.find(input) != null) && ((input.indexOf("=") == -1) || (input.indexOf("=") > input.indexOf(regexInvalidIdentifier.find(input)!!.value)))/* || regexInvalidIdentifierGeneric.find(input) != null*/){
            println("Invalid identifier")
            continue
        }else if(regexInvalidExpression.containsMatchIn(input)){
            println("Invalid expression")
            continue
        } else if(input.contains("=")){
            val assignment = regexVariableInsertion.find(input)
            if(assignment == null || assignment.value != input || regexInvalidIdentifier.find(input) != null){
                println("Invalid assignment")
                continue
            }
            //e.g: a = 10
            if(regexVariableInsertionValue.find(input) != null/* && regexVariableInitial.find(input) != null && regexValueFinal.find(input)!!.value.toIntOrNull() != null*/){
                listVariable[regexVariable.find(input)!!.value] = regexValueFinal.find(input)!!.value
            }//e.g: a = b
            else if(regexVariableInsertionVariable.find(input) != null/* && (regexVariableInitial.find(input) != null) && (regexVariableFinal.find(input) != null)*/){
                if(!listVariable.contains(regexVariable.findAll(input).last().value)){
                    println("Unknown variable")
                    continue
                }
                listVariable[regexVariable.find(input)!!.value] = listVariable.getValue(regexVariable.findAll(input).last().value)
            }
            continue
        }
        /*//replace variable to number
        for(localVar in regexVariable.findAll(input)){
            val value = listVariable[localVar.value]
            if(value == null){
                println("Unknown variable")
                continue@newInput
            }
            input = input.replace(localVar.value,value)
        }*/

        //operate
        //just 1 character
        /*if (input.trim().split(regexAddSeparator).size == 1 && input.trim().split(" ")[0].toIntOrNull() != null){
            println(input.trim().split(" ")[0].toBigInteger())
        }*/
        if(regexSoleNumber.matches(input)){
            for(localVar in regexVariable.findAll(input)){
                val value = listVariable[localVar.value]
                if(value == null){
                    println("Unknown variable")
                    continue@newInput
                }
                input = input.replace(localVar.value,value)
            }
            println(regexSoleNumber.find(input)!!.value)
        }
        else {
            input = input.replace("\\s+".toRegex(),"")// remove spaces
            val numbers = mutableListOf<String>()
            val seq = regexAddSeparator.findAll(input)
            for( v in seq) {
                numbers.add(v.value)
            }

            //trata +++++ e ------
            for(i in 0 until numbers.size){
                if("\\++".toRegex().matches(numbers[i])){
                    numbers[i] = "+"
                }else if("-+".toRegex().matches(numbers[i])){
                    auxContMinus = numbers[i].length
                    numbers[i] = if(auxContMinus % 2 == 0) "+" else "-"
                }
            }
            val infixNumbers = infixToPostfix(numbers)
            if(infixNumbers == null){
                println("Invalid expression")
                continue
            }
            //replace variable to number
            for(i in infixNumbers.indices){
                if(!regexVariable.matches(infixNumbers[i])){
                    continue
                }
                val value = listVariable[infixNumbers[i]]
                if(value == null){
                    println("Unknown variable")
                    continue@newInput
                }
                infixNumbers[i] = value
            }
            //println("infix = $infixNumbers")
            val output = postFixCalculate(infixNumbers)
            if(output == null){
                println("Invalid expression")
            }else{
                println(output)
            }
            //operate(numbers) -> this is to infix
        }
    }while(true)
}
