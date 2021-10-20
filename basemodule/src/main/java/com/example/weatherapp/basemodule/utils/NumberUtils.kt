package com.example.weatherapp.basemodule.utils

import java.math.BigDecimal
import java.math.BigInteger

object NumberUtils {

    fun createNumber(str: String?): Number? {
        if (str == null) {
            return null
        }
        if (str.isBlank()) {
            throw NumberFormatException("A blank string is not a valid number")
        }
        // Need to deal with all possible hex prefixes here
        val hex_prefixes = arrayOf("0x", "0X", "-0x", "-0X", "#", "-#")
        var pfxLen = 0
        for (pfx in hex_prefixes) {
            if (str.startsWith(pfx)) {
                pfxLen += pfx.length
                break
            }
        }
        if (pfxLen > 0) { // we have a hex number
            var firstSigDigit = 0.toChar() // strip leading zeroes
            for (i in pfxLen until str.length) {
                firstSigDigit = str[i]
                if (firstSigDigit == '0') { // count leading zeroes
                    pfxLen++
                } else {
                    break
                }
            }
            val hexDigits = str.length - pfxLen
            if (hexDigits > 16 || hexDigits == 16 && firstSigDigit > '7') { // too many for Long
                return createBigInteger(str)
            }
            return if (hexDigits > 8 || hexDigits == 8 && firstSigDigit > '7') { // too many for an int
                createLong(str)
            } else createInteger(str)
        }
        val lastChar = str[str.length - 1]
        val mant: String
        val dec: String?
        val exp: String?
        val decPos = str.indexOf('.')
        val expPos = str.indexOf('e') + str.indexOf('E') + 1 // assumes both not present
        // if both e and E are present, this is caught by the checks on expPos (which prevent IOOBE)
        // and the parsing which will detect if e or E appear in a number due to using the wrong offset
        if (decPos > -1) { // there is a decimal point
            dec = if (expPos > -1) { // there is an exponent
                if (expPos < decPos || expPos > str.length) { // prevents double exponent causing IOOBE
                    throw NumberFormatException("$str is not a valid number.")
                }
                str.substring(decPos + 1, expPos)
            } else {
                str.substring(decPos + 1)
            }
            mant = getMantissa(str, decPos)
        } else {
            mant = if (expPos > -1) {
                if (expPos > str.length) { // prevents double exponent causing IOOBE
                    throw NumberFormatException("$str is not a valid number.")
                }
                getMantissa(str, expPos)
            } else {
                getMantissa(str)
            }
            dec = null
        }
        if (!Character.isDigit(lastChar) && lastChar != '.') {
            exp = if (expPos > -1 && expPos < str.length - 1) {
                str.substring(expPos + 1, str.length - 1)
            } else {
                null
            }
            //Requesting a specific type..
            val numeric = str.substring(0, str.length - 1)
            val allZeros = isAllZeros(mant) && isAllZeros(exp)
            when (lastChar) {
                'l', 'L' -> {
                    if (dec == null && exp == null && (!numeric.isEmpty() && numeric[0] == '-' && isDigits(
                            numeric.substring(1)
                        ) || isDigits(numeric))
                    ) {
                        try {
                            return createLong(numeric)
                        } catch (nfe: NumberFormatException) { // NOPMD
                            // Too big for a long
                        }
                        return createBigInteger(numeric)
                    }
                    throw NumberFormatException("$str is not a valid number.")
                }
                'f', 'F' -> {
                    try {
                        val f = createFloat(str)
                        if (!(f!!.isInfinite() || f.toFloat() == 0.0f && !allZeros)) {
                            //If it's too big for a float or the float value = 0 and the string
                            //has non-zeros in it, then float does not have the precision we want
                            return f
                        }
                    } catch (nfe: NumberFormatException) { // NOPMD
                        // ignore the bad number
                    }
                    try {
                        val d = createDouble(str)
                        if (!(d!!.isInfinite() || d.toFloat().toDouble() == 0.0 && !allZeros)) {
                            return d
                        }
                    } catch (nfe: NumberFormatException) { // NOPMD
                        // ignore the bad number
                    }
                    try {
                        return createBigDecimal(numeric)
                    } catch (e: NumberFormatException) { // NOPMD
                        // ignore the bad number
                    }
                    throw NumberFormatException("$str is not a valid number.")
                }
                'd', 'D' -> {
                    try {
                        val d = createDouble(str)
                        if (!(d!!.isInfinite() || d.toFloat().toDouble() == 0.0 && !allZeros)) {
                            return d
                        }
                    } catch (nfe: NumberFormatException) {
                    }
                    try {
                        return createBigDecimal(numeric)
                    } catch (e: NumberFormatException) {
                    }
                    throw NumberFormatException("$str is not a valid number.")
                }
                else -> throw NumberFormatException("$str is not a valid number.")
            }
        }
        //User doesn't have a preference on the return type, so let's start
        //small and go from there...
        exp = if (expPos > -1 && expPos < str.length - 1) {
            str.substring(expPos + 1)
        } else {
            null
        }
        if (dec == null && exp == null) { // no decimal point and no exponent
            //Must be an Integer, Long, Biginteger
            try {
                return createInteger(str)
            } catch (nfe: NumberFormatException) { // NOPMD
                // ignore the bad number
            }
            try {
                return createLong(str)
            } catch (nfe: NumberFormatException) { // NOPMD
                // ignore the bad number
            }
            return createBigInteger(str)
        }

        //Must be a Float, Double, BigDecimal
        val allZeros = isAllZeros(mant) && isAllZeros(exp)
        try {
            val f = createFloat(str)
            val d = createDouble(str)
            if (!f!!.isInfinite()
                && !(f.toFloat() == 0.0f && !allZeros)
                && f.toString() == d.toString()
            ) {
                return f
            }
            if (!d!!.isInfinite() && !(d.toDouble() == 0.0 && !allZeros)) {
                val b = createBigDecimal(str)
                return if (b!!.compareTo(BigDecimal.valueOf(d.toDouble())) == 0) {
                    d
                } else b
            }
        } catch (nfe: NumberFormatException) { // NOPMD
            // ignore the bad number
        }
        return createBigDecimal(str)
    }

    private fun getMantissa(str: String): String {
        return getMantissa(str, str.length)
    }

    private fun getMantissa(str: String, stopPos: Int): String {
        val firstChar = str[0]
        val hasSign = firstChar == '-' || firstChar == '+'
        return if (hasSign) str.substring(1, stopPos) else str.substring(0, stopPos)
    }

    private fun isAllZeros(str: String?): Boolean {
        if (str == null) {
            return true
        }
        for (i in str.length - 1 downTo 0) {
            if (str[i] != '0') {
                return false
            }
        }
        return !str.isEmpty()
    }

    //-----------------------------------------------------------------------
    fun createBigInteger(str: String?): BigInteger? {
        if (str == null) {
            return null
        }
        var pos = 0 // offset within string
        var radix = 10
        var negate = false // need to negate later?
        if (str.startsWith("-")) {
            negate = true
            pos = 1
        }
        if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) { // hex
            radix = 16
            pos += 2
        } else if (str.startsWith("#", pos)) { // alternative hex (allowed by Long/Integer)
            radix = 16
            pos++
        } else if (str.startsWith(
                "0",
                pos
            ) && str.length > pos + 1
        ) { // octal; so long as there are additional digits
            radix = 8
            pos++
        } // default is to treat as decimal
        val value = BigInteger(str.substring(pos), radix)
        return if (negate) value.negate() else value
    }

    fun createFloat(str: String?): Float? {
        return if (str == null) {
            null
        } else java.lang.Float.valueOf(str)
    }

    fun createDouble(str: String?): Double? {
        return if (str == null) {
            null
        } else java.lang.Double.valueOf(str)
    }

    fun createInteger(str: String?): Int? {
        return if (str == null) {
            null
        } else Integer.decode(str)
        // decode() handles 0xAABD and 0777 (hex and octal) as well.
    }

    fun createLong(str: String?): Long? {
        return if (str == null) {
            null
        } else java.lang.Long.decode(str)
    }

    fun isDigits(str: String?): Boolean {
        return str?.let { isNumeric(it) } ?: false
    }

    fun createBigDecimal(str: String?): BigDecimal? {
        if (str == null) {
            return null
        }
        // handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
        if (str.isBlank()) {
            throw NumberFormatException("A blank string is not a valid number")
        }
        return BigDecimal(str)
    }

    fun isNumeric(cs: CharSequence): Boolean {
        if (cs.isEmpty()) {
            return false
        }
        val sz = cs.length
        for (i in 0 until sz) {
            if (!Character.isDigit(cs[i])) {
                return false
            }
        }
        return true
    }
}