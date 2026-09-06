package com.aperonix.ai.actions

sealed class AssistantAction(val type: String) {
    data class OpenApp(val packageName: String) : AssistantAction("OPEN_APP")
    data class OpenUrl(val url: String) : AssistantAction("OPEN_URL")
    object OpenSettings : AssistantAction("OPEN_SETTINGS")
}

sealed class ActionResult {
    object Success : ActionResult()
    data class Failure(val reason: String) : ActionResult()
}
