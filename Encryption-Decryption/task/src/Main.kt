package tictactoe

import java.io.File
import java.lang.Exception

const val ENCRYPT_MODE = "enc"
const val DECRYPT_MODE = "dec"
const val SHIFT_ALGORITHM = "shift"
const val UNICODE_ALGORITHM = "unicode"
const val NO_OF_ALPHABETS = 26
const val LOWER_CASE_START = 'a'
const val UPPER_CASE_START = 'A'
const val LOWER_CASE_END = 'z'
const val UPPER_CASE_END = 'Z'

fun main(args: Array<String>) {

    var mode: String = ENCRYPT_MODE;
    var alg: String = SHIFT_ALGORITHM;
    var key: Int = 0;
    var data: String = "";
    var inputFile: String = "";
    var outputFile: String = "";

    try {
        for (i in args.indices) {
            if (args[i].contains("-mode")) {
                mode = args[i + 1].trim()
            } else if (args[i].contains("-key")) {
                key = args[i + 1].trim().toInt()
            } else if (args[i].contains("-alg")) {
                alg = args[i + 1].trim()
            } else if (args[i].contains("-data")) {
                data = args[i + 1].trim()
            } else if (args[i].contains("-in")) {
                inputFile = args[i + 1].trim()
            } else if (args[i].contains("-out")) {
                outputFile = args[i + 1].trim()
            }
        }

        val message = getMessage(data, inputFile)
        val transformer = getFunction(mode, alg)
        val result = when (mode) {
            ENCRYPT_MODE -> encrypt(message, key, transformer)
            DECRYPT_MODE -> decrypt(message, key, transformer)
            else -> "Invalid operation"
        }
        outputMessage(result, outputFile)

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

}

fun outputMessage(message: String, outputFile: String) {
    if (outputFile.isEmpty()) {
        println(message)
    } else {
        File(outputFile).writeText(message)
    }
}

fun getMessage(data: String, inputFile: String): String {
    return if (data.isEmpty() && inputFile.isEmpty()) {
        ""
    } else if (data.isNotEmpty()) {
        data
    } else {
        File(inputFile).readText()
    }
}

fun getFunction(mode: String, alg: String): (c: Char, key: Int) -> Char {
    return when {
        (mode == ENCRYPT_MODE && alg == UNICODE_ALGORITHM) -> ::encryptCharWithUnicode
        (mode == ENCRYPT_MODE && alg == SHIFT_ALGORITHM) -> ::encryptCharWithShift
        (mode == DECRYPT_MODE && alg == UNICODE_ALGORITHM) -> ::decryptCharWithUnicode
        else -> ::decryptCharWithShift
    }
}


fun encryptCharWithUnicode(c: Char, key: Int): Char = c + key
fun decryptCharWithUnicode(c: Char, key: Int): Char = c - key
fun encryptCharWithShift(c: Char, key: Int): Char {
    val newCharPos = c + key
    return when {
        (c.isLetter() && c.isLowerCase() && newCharPos > LOWER_CASE_END) -> newCharPos - NO_OF_ALPHABETS
        (c.isLetter() && c.isUpperCase() && newCharPos > UPPER_CASE_END) -> newCharPos - NO_OF_ALPHABETS
        c.isLetter() -> newCharPos
        else -> c
    }
}

fun decryptCharWithShift(c: Char, key: Int): Char {
    val newCharPos = c - key
    return when {
        (c.isLetter() && c.isLowerCase() && newCharPos < LOWER_CASE_START) -> newCharPos + NO_OF_ALPHABETS
        (c.isLetter() && c.isUpperCase() && newCharPos < UPPER_CASE_START) -> newCharPos + NO_OF_ALPHABETS
        c.isLetter() -> newCharPos
        else -> c
    }
}

fun encrypt(message: String, key: Int, fn: (c: Char, key: Int) -> Char): String {
    var encryptedMessage = ""
    for (c: Char in message) {
        encryptedMessage += fn(c, key)
    }
    return encryptedMessage
}

fun decrypt(cypherText: String, key: Int, fn: (c: Char, key: Int) -> Char): String {
    var decryptedMessage = ""
    for (c: Char in cypherText) {
        decryptedMessage += fn(c, key)
    }
    return decryptedMessage
}