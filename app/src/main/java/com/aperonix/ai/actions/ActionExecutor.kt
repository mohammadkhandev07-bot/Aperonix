package com.aperonix.ai.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object ActionValidator {
    fun validate(action: AssistantAction): Boolean {
        return when (action) {
            is AssistantAction.OpenApp -> action.packageName.isNotBlank()
            is AssistantAction.OpenUrl -> action.url.startsWith("http")
            is AssistantAction.OpenSettings -> true
            else -> false
        }
    }
}

object ActionExecutor {
    fun execute(context: Context, action: AssistantAction): ActionResult {
        if (!ActionValidator.validate(action)) {
            return ActionResult.Failure("Invalid action")
        }
        return try {
            when (action) {
                is AssistantAction.OpenApp -> {
                    val launch = context.packageManager.getLaunchIntentForPackage(action.packageName)
                    return if (launch != null) {
                        context.startActivity(launch)
                        ActionResult.Success
                    } else {
                        ActionResult.Failure("App not installed")
                    }
                }
                is AssistantAction.OpenUrl -> {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(action.url)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(i)
                    ActionResult.Success
                }
                is AssistantAction.OpenSettings -> {
                    val i = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(i)
                    ActionResult.Success
                }
            }
        } catch (e: Exception) {
            Log.e("ActionExecutor", "execute failed", e)
            ActionResult.Failure(e.localizedMessage ?: "Unknown error")
        }
    }
}
