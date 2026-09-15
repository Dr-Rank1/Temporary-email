package com.rank.tempbox

object OtpExtractor {

    private val KEYWORD_THEN_CODE = Regex(
        """(?i)(?:one[\s-]?time\s+(?:pass(?:word|code)?|code)|verification(?:\s+code)?|security\s+code|\botp\b|passcode|\bpin\b|\bcode)\s*(?:is|:)?\s*([A-Z0-9]{4,8})\b""",
    )

    private val CODE_THEN_KEYWORD = Regex(
        """(?i)\b([A-Z0-9]{4,8})\s+(?:is\s+)?(?:your\s+)?(?:otp|code|passcode|pin)\b""",
    )

    fun extract(text: String): String? {
        if (text.isBlank()) return null
        KEYWORD_THEN_CODE.find(text)?.groupValues?.getOrNull(1)?.let { if (isPlausible(it)) return it }
        CODE_THEN_KEYWORD.find(text)?.groupValues?.getOrNull(1)?.let { if (isPlausible(it)) return it }
        return null
    }

    fun extract(html: String?, text: String?): String? {
        text?.let { extract(it) }?.let { return it }
        html?.let { extract(stripHtml(it)) }?.let { return it }
        return null
    }

    private fun isPlausible(candidate: String): Boolean {
        if (candidate.length !in 4..8) return false
        if (!candidate.any { it.isDigit() }) return false
        if (candidate.length == 4 && candidate.all { it.isDigit() }) {
            val year = candidate.toInt()
            if (year in 1900..2099) return false
        }
        return true
    }

    private fun stripHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
