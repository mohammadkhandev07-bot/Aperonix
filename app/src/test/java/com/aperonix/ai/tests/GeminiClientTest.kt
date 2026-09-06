package com.aperonix.ai.tests

import com.aperonix.ai.ai.GeminiClient
import com.aperonix.ai.actions.AssistantAction
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiClientTest {
    @Test
    fun testExtractOpenAppAction() {
        val json = "{ \"action\": { \"type\": \"OPEN_APP\", \"target\": \"com.google.android.youtube\" } }"
        val action = GeminiClient.extractActionFromResponse(json)
        assertTrue(action is AssistantAction.OpenApp)
    }
}
