package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SupportFeatureActivity : AppCompatActivity() {
    private lateinit var questionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var responseTextView: TextView
    private lateinit var generativeModel: GenerativeModel
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_feedback)

        // Initialize UI elements
        questionEditText = findViewById(R.id.questionEditText)
        submitButton = findViewById(R.id.submitButton)
        responseTextView = findViewById(R.id.responseTextView)

        // Set up Generative AI and Firebase
        setupGenerativeModel()
        setupFirebase()

        // Set up the submit button click listener
        submitButton.setOnClickListener {
            val question = questionEditText.text.toString()
            if (question.isNotEmpty()) {
                // Send question to AI and store feedback in Firebase
                getAIResponseAndStoreFeedback(question)

                // Clear the input field after submission
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

    private fun setupFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun getAIResponseAndStoreFeedback(question: String) {
        CoroutineScope(Dispatchers.Main).launch {
            responseTextView.text = "Thinking..."
            try {
                val prompt = """
                    You are a helpful AI assistant for a game hub application that hosts multiple games developed by a group of indie developers. Your role is to provide support and answer questions related to the games, the app itself, or general gaming queries. 
                    
                    User Question: $question

                    Please provide a helpful, friendly response based on the guidelines above.
                """.trimIndent()

                // Send question to Gemini AI and get response
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt).text
                }

                // Display AI's response in the app
                responseTextView.text = response ?: "Sorry, couldn't generate a response."

                // Store feedback in Firebase
                storeFeedbackInFirebase(question, response)

            } catch (e: Exception) {
                responseTextView.text = "Error: Could not process the request. Please try again."
            }
        }
    }

    private fun storeFeedbackInFirebase(question: String, aiResponse: String?) {
        val feedback = hashMapOf(
            "question" to question,
            "aiResponse" to aiResponse,
            "timestamp" to Date().time
        )

        db.collection("feedback")
            .add(feedback)
            .addOnSuccessListener {
                responseTextView.append("\nFeedback stored successfully.")
            }
            .addOnFailureListener { e ->
                responseTextView.append("\nFailed to store feedback: ${e.message}")
            }
    }
}