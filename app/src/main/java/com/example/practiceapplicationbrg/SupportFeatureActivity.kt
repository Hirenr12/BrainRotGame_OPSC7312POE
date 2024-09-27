package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupportFeatureActivity : AppCompatActivity() {
    private lateinit var questionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var responseTextView: TextView
    private lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_feedback)

        questionEditText = findViewById(R.id.questionEditText)
        submitButton = findViewById(R.id.submitButton)
        responseTextView = findViewById(R.id.responseTextView)

        setupGenerativeModel()

        submitButton.setOnClickListener {
            val question = questionEditText.text.toString()
            if (question.isNotEmpty()) {
                // Fetch AI response and store the question
                getAIResponse(question)

                // Clear the input field after storing the user's question
                questionEditText.text.clear()
            }
        }
    }

    private fun setupGenerativeModel() {

        val apiKey = System.getenv("GENERATIVE_MODEL_API_KEY")
            ?: throw IllegalArgumentException("API Key not found")

        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        )
    }

    private fun getAIResponse(question: String) {
        CoroutineScope(Dispatchers.Main).launch {
            responseTextView.text = "Thinking..."
            try {
                val prompt = """
                You are a helpful AI assistant for a game hub application that hosts multiple games developed by a group of indie developers. Your role is to provide support and answer questions related to the games, the app itself, or general gaming queries. Here are some guidelines:

                1. If asked about specific games, provide general helpful information but clarify that details may vary as new games are frequently added.
                2. For technical issues, offer basic troubleshooting steps and advise contacting support for persistent problems.
                3. Explain features of the game hub like how to navigate between games, update games, or manage settings.
                4. For game-specific questions, provide general gaming advice that could apply to multiple games.
                5. Be friendly and encouraging to players, especially if they're struggling with a game.
                6. If asked about upcoming features or games, express excitement but don't make promises about specific releases.
                7. For account-related questions, provide general guidance on typical account management processes.
                8. If you're unsure about something, it's okay to say so and suggest where the user might find more information.

                User Question: $question

                Please provide a helpful, friendly response based on the guidelines above.
            """.trimIndent()

                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt) // Use the correct function signature with a string argument
                }.text // Assuming the result has a 'text' field that contains the AI's response

                responseTextView.text = response ?: "I'm sorry, I couldn't generate a response. Please try asking your question in a different way."
            } catch (e: Exception) {
                responseTextView.text = "I apologize, but I encountered an error while trying to answer your question. Please try again later or contact our support team for assistance."
            }
        }
    }

}