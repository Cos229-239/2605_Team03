package com.nightowlcrew.nudgie.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig

class GeminiService {
    private val apiKey = "YOUR_API_KEY_HERE"


    private val systemInstruction = content {
        text("""
            You are Nudgie, a fun, gamified habit-tracking pet. 
            Your personality changes based on user input. 
            Always provide short, encouraging comments when a user completes a task. 
            If the user asks for a specific personality (like Grumpy, Energetic, or Sleepy), 
            adopt that persona immediately and stay in character.
        """.trimIndent())
    }

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,
            systemInstruction = systemInstruction,
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }


    private val chat = generativeModel.startChat()

    suspend fun generateResponse(prompt: String): String {
        return try {
            // Send the prompt through the chat session instead of generateContent
            val response = chat.sendMessage(prompt)
            response.text ?: "..."
        } catch (e: Exception) {
            android.util.Log.e("NUDGIE_AI_ERROR", "API call failed", e)
            "I'm having trouble thinking: ${e.message}"
        }
    }
}
