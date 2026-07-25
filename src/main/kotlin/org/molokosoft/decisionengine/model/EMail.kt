package org.molokosoft.decisionengine.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class EMail private constructor(val eMail: String) {
    companion object {
        private val EMAIL_REGEX = Regex(
            "^[A-Za-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@" +
                    "(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+" +
                    "[A-Za-z]{2,}$"
        )

        operator fun invoke(raw: String): EMail {
            require(EMAIL_REGEX.matches(raw)) {
                "Invalid E-Mail-Address $raw"
            }

            return EMail(raw)
        }

        fun tryCreate(raw: String): EMail? =
            if (EMAIL_REGEX.matches(raw))
                EMail(raw)
            else
                null
    }

    override fun toString(): String = eMail
}