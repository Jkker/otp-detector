package com.example.otpdetector

import android.app.AndroidAppHelper
import android.app.Notification
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.regex.Pattern

class OtpModule : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "OtpDetector"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("$TAG: Loaded target package ${lpparam.packageName}")

        try {
            XposedHelpers.findAndHookMethod(
                NotificationManager::class.java,
                "notify",
                String::class.java,
                Int::class.javaPrimitiveType,
                Notification::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val notification = param.args[2] as Notification
                        processNotification(notification)
                    }
                }
            )

            XposedHelpers.findAndHookMethod(
                NotificationManager::class.java,
                "notify",
                Int::class.javaPrimitiveType,
                Notification::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val notification = param.args[1] as Notification
                        processNotification(notification)
                    }
                }
            )

        } catch (e: Throwable) {
            XposedBridge.log(e)
        }
    }

    private fun processNotification(notification: Notification) {
        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        
        // Detailed logging as requested
        XposedBridge.log("$TAG: Processing Notification - Title: '$title', Text: '$text', BigText: '$bigText'")

        val otp = OtpExtractor.extractOtp(title, text, bigText)
        if (otp != null) {
            XposedBridge.log("$TAG: Found OTP: $otp")
            copyToClipboard(otp)
        }
    }

    private fun copyToClipboard(otp: String) {
        val context = AndroidAppHelper.currentApplication()
        if (context == null) {
            XposedBridge.log("$TAG: Context is null, cannot copy to clipboard")
            return
        }

        Handler(Looper.getMainLooper()).post {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText("OTP", otp)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "OTP Copied: $otp", Toast.LENGTH_SHORT).show()
                XposedBridge.log("$TAG: Copied to clipboard")
            } catch (e: Throwable) {
                XposedBridge.log(e)
            }
        }
    }
}

object OtpExtractor {
    // Regex to find 4-8 digit numbers.
    // Uses negative lookbehind (?<![-.]) to avoid suffixes of hyphenated/dotted numbers (e.g. 123-4567).
    // Uses negative lookahead (?![-.]\d) to avoid prefixes of hyphenated/dotted numbers (e.g. 1234-5678 or 1234.56),
    // but allows punctuation at the end of a sentence (e.g. "Code is 1234.").
    private val OTP_PATTERN = Pattern.compile("(?<![-.])\\b(\\d{4,8})\\b(?![.-]\\d)")
    
    // Keywords required for short codes (4-5 digits) to reduce false positives (e.g. years, order numbers).
    private val KEYWORDS = listOf("code", "otp", "pass", "pin", "verif", "secret", "login", "auth")

    fun extractOtp(title: String, text: String, bigText: String): String? {
        val fullText = "$title $text $bigText"
        val lowerText = fullText.lowercase()
        
        val matcher = OTP_PATTERN.matcher(fullText)
        while (matcher.find()) {
            val candidate = matcher.group(1) ?: continue
            if (isValidOtp(candidate, lowerText)) {
                return candidate
            }
        }
        return null
    }

    private fun isValidOtp(candidate: String, fullTextLower: String): Boolean {
        // If it's a short code (4-5 digits), we require a keyword to be present in the text.
        if (candidate.length < 6) {
            val hasKeyword = KEYWORDS.any { fullTextLower.contains(it) }
            return hasKeyword
        }
        // Longer codes (6-8 digits) are usually distinct enough to be safe without strict keyword matching,
        // but the regex lookarounds already filter out most phone number formats.
        return true
    }
}
