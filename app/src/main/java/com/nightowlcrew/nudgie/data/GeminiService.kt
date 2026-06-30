package com.nightowlcrew.nudgie.data

import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig

class GeminiService {

    private val systemInstruction = content {
        text("""
            You are Nudgie, a fun, gamified habit-tracking pet. 
            Your personality changes based on user input. 
            Always provide short, encouraging comments when a user completes a task. 
            If the user asks for a specific personality (like Grumpy, Energetic, or Sleepy), 
            adopt that persona immediately and stay in character.
        """.trimIndent())
    }

    // Initialize using Firebase Vertex AI instead of the raw client
    private val generativeModel by lazy {
        Firebase.vertexAI.generativeModel(
            modelName = "gemini-1.5-flash",
            systemInstruction = systemInstruction,
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    private val chat = generativeModel.startChat()

    suspend fun generateResponse(prompt: String): String {
        return try {
            val response = chat.sendMessage(prompt)
            response.text ?: "..."
        } catch (e: Exception) {
            android.util.Log.e("NUDGIE_AI_ERROR", "Firebase Vertex API call failed", e)
            "I'm having trouble thinking: ${e.message}"
        }
    }
}
