package com.nocomerciales.app

/**
 * Comprueba si un número corresponde a la nueva numeración comercial española:
 * 400 XXX XXX (9 dígitos en total, empezando por 400).
 */
object PhoneNumberMatcher {

    private val COMMERCIAL_REGEX = Regex("^400\\d{6}$")

    /** Deja solo dígitos y quita prefijos de país de España (+34 / 0034). */
    fun normalize(rawNumber: String): String {
        var digits = rawNumber.filter { it.isDigit() }
        if (digits.length == 13 && digits.startsWith("0034")) {
            digits = digits.substring(4)
        } else if (digits.length == 11 && digits.startsWith("34")) {
            digits = digits.substring(2)
        }
        return digits
    }

    fun isCommercialNumber(rawNumber: String?): Boolean {
        if (rawNumber.isNullOrBlank()) return false
        return COMMERCIAL_REGEX.matches(normalize(rawNumber))
    }
}
