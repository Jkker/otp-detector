package com.example.otpdetector

import org.junit.Assert.assertEquals
import org.junit.Test

class OtpRegexTest {

    @Test
    fun testPhoneNumberFalsePositive() {
        val title = "(332) 288-1234"
        val text = "New message"
        val bigText = "New message from (332) 288-1234"
        
        // This SHOULD be null, but currently it will match "1234"
        // I will assert null to demonstrate the failure (or I can assert 1234 to confirm current behavior then fail later)
        // Let's assert EXPECTED behavior (null) so it fails now.
        assertEquals(null, OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testValidOtpSimple() {
        val title = "Google"
        val text = "Your code is 123456"
        val bigText = ""
        assertEquals("123456", OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testValidOtpWithSentence() {
        val title = "Service"
        val text = "Login code: 998877. Don't share it."
        val bigText = ""
        assertEquals("998877", OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testShortCodeWithKeyword() {
        val title = "Bank"
        val text = "Your pin is 4521"
        val bigText = ""
        assertEquals("4521", OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testShortCodeWithoutKeyword() {
        val title = "Order"
        val text = "Order #4521 shipped"
        val bigText = ""
        // No keyword, length 4, should be ignored
        assertEquals(null, OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testIpAddress() {
        val title = "System"
        val text = "Login from 192.168.1.1"
        val bigText = ""
        // Should ignore IP parts
        assertEquals(null, OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testPhonePartSuffix() {
        val title = "Phone"
        val text = "Call 555-0199"
        val bigText = ""
        // 0199 is 4 digits. Preceded by -. Should be ignored.
        assertEquals(null, OtpExtractor.extractOtp(title, text, bigText))
    }

    @Test
    fun testPhonePartPrefix() {
        val title = "Phone"
        val text = "Call 0199-555"
        val bigText = ""
        // 0199 is 4 digits. Followed by -. Should be ignored.
        assertEquals(null, OtpExtractor.extractOtp(title, text, bigText))
    }
}
