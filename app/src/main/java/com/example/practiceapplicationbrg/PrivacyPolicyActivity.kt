package com.example.practiceapplicationbrg

import PrivacyPolicy
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        // Create a PrivacyPolicy object with your policy text
        val privacyPolicy = PrivacyPolicy(
            effectiveDate = "Effective Date: September 29, 2024",

            introduction = """
        We are committed to protecting your privacy. This Privacy Policy outlines how we collect, use, share, 
        and protect your personal information when you use our mobile applications, including our games, 
        community pages, and feedback tools, as well as the Gemini AI feature. Please read this policy carefully 
        to understand our views and practices regarding your personal data and how we will treat it.
    """.trimIndent(),

            informationWeCollect = """
        We collect personal information that you voluntarily provide to us when you register for an account, 
        submit feedback, participate in the community, or play our games. This information includes, but is not limited to:
        - Your name
        - Email address
        - Username
        - Dates and times of interaction with the app (e.g., when you sign in, submit feedback)
        
        In addition to this, the app collects certain technical data automatically, such as:
        - Device information (e.g., model, operating system)
        - IP address
        - Usage data, including in-game activity
    """.trimIndent(),

            howWeUseInformation = """
        The personal information we collect is used to enhance your experience with our app and services. Specifically, we use your information to:
        - Create and manage your account
        - Provide access to our games and community features
        - Improve the functionality and performance of the app
        - Collect feedback to refine and improve our services
        - Send you notifications, updates, or promotional materials, if you opt-in to receive them
        
        Please note that while the app includes a Gemini AI feature, any conversations with the AI are not logged or stored by us. 
        However, the community and feedback pages do log user submissions, and we use that information to improve our services.
    """.trimIndent(),

            howWeShareInformation = """
        We do not sell, trade, or otherwise transfer your personal information to outside parties, except in the following cases:
        - To comply with legal obligations or respond to valid legal requests, such as a court order or government inquiry
        - To protect the rights, property, or safety of our company, our users, or others
        - To our trusted service providers who assist us in operating our app and services, as long as they agree to keep your information confidential
        
        All third-party providers with access to your data are required to take reasonable steps to ensure the privacy and security of your information.
    """.trimIndent(),

            dataSecurity = """
        We take the security of your personal data seriously. We implement a variety of security measures, such as encryption and secure storage, to protect your personal information against unauthorized access, alteration, disclosure, or destruction. 
        However, please understand that no security system is impenetrable, and we cannot guarantee the complete security of your data. 
        You are responsible for keeping your account credentials secure and not sharing them with others.
    """.trimIndent(),

            yourChoices = """
        You have several choices regarding the collection and use of your data:
        - You can review, update, or delete your account information at any time by accessing the settings in the app.
        - You can choose not to provide certain information, but this may limit your ability to use certain features of the app (e.g., community and feedback sections).
        - You may opt out of receiving promotional communications by following the unsubscribe instructions in the emails or updating your preferences in the app.
        - You can request to have your data deleted by contacting our support team at support@example.com.
    """.trimIndent(),

            childrensPrivacy = """
        Our app is not intended for children under the age of 13, and we do not knowingly collect personal information from children under 13. 
        If we become aware that we have inadvertently collected personal information from a child under 13, we will take steps to delete that information from our records as soon as possible.
        If you are a parent or guardian and believe that your child has provided us with personal information without your consent, please contact us at support@example.com, and we will take appropriate action.
    """.trimIndent(),

            changesToPolicy = """
        We reserve the right to modify this Privacy Policy at any time. Any changes will be effective immediately upon posting the updated policy on this page, and we will notify you via email or in-app notifications if there are significant changes. 
        We encourage you to review this policy periodically for the latest information on our privacy practices.
    """.trimIndent(),

            contactUs = """
        If you have any questions or concerns about this Privacy Policy, or if you would like to make a request regarding your personal data, 
        please contact us at:
        
        Email: superman@gmail.com
        Address: 123 Privacy St., Data City, DS 12345
    """.trimIndent()
        )

        // Set the privacy policy content in the TextView
        val privacyPolicyTextView: TextView = findViewById(R.id.tvPrivacyPolicyContent)
        val policyText = """
            ${privacyPolicy.effectiveDate}

            ${privacyPolicy.introduction}

            Information We Collect:
            ${privacyPolicy.informationWeCollect}

            How We Use Information:
            ${privacyPolicy.howWeUseInformation}

            How We Share Information:
            ${privacyPolicy.howWeShareInformation}

            Data Security:
            ${privacyPolicy.dataSecurity}

            Your Choices:
            ${privacyPolicy.yourChoices}

            Children's Privacy:
            ${privacyPolicy.childrensPrivacy}

            Changes to This Policy:
            ${privacyPolicy.changesToPolicy}

            Contact Us:
            ${privacyPolicy.contactUs}
        """.trimIndent()

        privacyPolicyTextView.text = policyText
    }
}
