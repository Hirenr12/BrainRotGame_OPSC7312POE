package com.example.practiceapplicationbrg

import PrivacyPolicy
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacy_policy)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create an instance of PrivacyPolicy with the policy text
        val privacyPolicy = PrivacyPolicy(
            effectiveDate = "Effective Date: 21/08/2024",
            introduction = "Welcome to BrainRotGames (\"we,\" \"our,\" or \"us\"). This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application (\"App\"). Please read this policy carefully. If you do not agree with the terms of this Privacy Policy, please do not use the App.",
            informationWeCollect = """
                1. Information We Collect
                a. Personal Information
                We may collect personal information that you voluntarily provide to us, such as:
                - Name
                - Email address
                - Username
                - Profile picture
                - Payment information (if applicable)
                b. Non-Personal Information
                We may collect non-personal information about you whenever you interact with our App. Non-personal information may include your device type, operating system, browser type, and usage data.
                c. Location Information
                With your consent, we may collect information about your location to provide location-based services.
            """.trimIndent(),
            howWeUseInformation = """
                2. How We Use Your Information
                We may use the information we collect for various purposes, including:
                - To provide, maintain, and improve our App
                - To manage your account
                - To process transactions and send related information
                - To communicate with you, including sending updates and promotional materials
                - To personalize your experience with the App
                - To monitor and analyze usage and trends to improve the App
                - To detect, prevent, and address technical issues or security breaches
            """.trimIndent(),
            howWeShareInformation = """
                3. How We Share Your Information
                We do not sell, trade, or rent your personal information to others. We may share information with third parties in the following circumstances:
                - Service Providers: We may share information with third-party service providers who perform services on our behalf, such as payment processing or data analysis.
                - Legal Requirements: We may disclose your information if required to do so by law or in response to valid requests by public authorities.
                - Business Transfers: If we are involved in a merger, acquisition, or asset sale, your information may be transferred as part of that transaction.
            """.trimIndent(),
            dataSecurity = """
                4. Data Security
                We implement reasonable security measures to protect your information from unauthorized access, alteration, disclosure, or destruction. However, no method of transmission over the internet or method of electronic storage is 100% secure.
            """.trimIndent(),
            yourChoices = """
                5. Your Choices
                a. Account Information
                You may update, correct, or delete your account information at any time by accessing your account settings within the App.
                b. Marketing Communications
                You may opt out of receiving promotional communications from us by following the unsubscribe instructions provided in those communications.
                c. Location Information
                You can stop the collection of location information by changing the settings on your mobile device.
            """.trimIndent(),
            childrensPrivacy = """
                6. Children’s Privacy
                Our App is not intended for children under the age of 13, and we do not knowingly collect personal information from children under 13. If we become aware that we have inadvertently collected personal information from a child under 13, we will delete such information promptly.
            """.trimIndent(),
            changesToPolicy = """
                7. Changes to This Privacy Policy
                We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page. You are advised to review this Privacy Policy periodically for any changes.
            """.trimIndent(),
            contactUs = """
                8. Contact Us
                If you have any questions about this Privacy Policy, please contact us at:
                - Email: [Insert Contact Email]
                - Address: [Insert Physical Address]
            """.trimIndent()
        )

        // Get the TextView and populate it with the policy text
        val privacyTextView: TextView = findViewById(R.id.tvPrivacyPolicyContent)
        val fullPolicyText = """
            ${privacyPolicy.effectiveDate}
            
            ${privacyPolicy.introduction}
            
            ${privacyPolicy.informationWeCollect}
            
            ${privacyPolicy.howWeUseInformation}
            
            ${privacyPolicy.howWeShareInformation}
            
            ${privacyPolicy.dataSecurity}
            
            ${privacyPolicy.yourChoices}
            
            ${privacyPolicy.childrensPrivacy}
            
            ${privacyPolicy.changesToPolicy}
            
            ${privacyPolicy.contactUs}
        """.trimIndent()

        privacyTextView.text = fullPolicyText
    }
}
