package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class GetDialogMessage(private val preferences: Preferences) {
    operator fun invoke(): String {
        val message = preferences.getCustomMessage()
        return message.ifBlank {
            preferences.getMessagesArray().random()
        }
    }
}
