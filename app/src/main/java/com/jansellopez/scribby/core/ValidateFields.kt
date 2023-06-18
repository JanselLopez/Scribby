package com.jansellopez.scribby.core

import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
    val pattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(email)
    return matcher.matches()
}