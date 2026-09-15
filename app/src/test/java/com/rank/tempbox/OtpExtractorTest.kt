package com.rank.tempbox

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OtpExtractorTest {

    @Test
    fun extractsVerificationCode() {
        assertEquals("482910", OtpExtractor.extract("Your verification code is 482910"))
    }

    @Test
    fun extractsOtpLabel() {
        assertEquals("123456", OtpExtractor.extract("OTP: 123456"))
    }

    @Test
    fun extractsCodeColon() {
        assertEquals("847291", OtpExtractor.extract("Enter the code: 847291 to continue"))
    }

    @Test
    fun extractsReversedWording() {
        assertEquals("551029", OtpExtractor.extract("551029 is your code"))
    }

    @Test
    fun ignoresBareYear() {
        assertNull(OtpExtractor.extract("See you in 2024 for the event"))
    }

    @Test
    fun ignoresZipCode() {
        assertNull(OtpExtractor.extract("Deliver to 90210 tomorrow"))
    }

    @Test
    fun extractsFromHtml() {
        assertEquals("847291", OtpExtractor.extract("<p>Your code is 847291</p>", null))
    }
}
