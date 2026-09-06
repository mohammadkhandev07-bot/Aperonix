package com.aperonix.ai.tests

import com.aperonix.ai.actions.ActionValidator
import com.aperonix.ai.actions.AssistantAction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionValidatorTest {
    @Test
    fun testValidateOpenApp() {
        val action = AssistantAction.OpenApp(packageName = "com.google.android.youtube")
        assertTrue(ActionValidator.validate(action))
    }

    @Test
    fun testValidateOpenAppEmpty() {
        val action = AssistantAction.OpenApp(packageName = "")
        assertFalse(ActionValidator.validate(action))
    }

    @Test
    fun testValidateOpenUrl() {
        val action = AssistantAction.OpenUrl(url = "https://www.google.com")
        assertTrue(ActionValidator.validate(action))
    }

    @Test
    fun testValidateOpenUrlInvalid() {
        val action = AssistantAction.OpenUrl(url = "ftp://example")
        assertFalse(ActionValidator.validate(action))
    }
}
