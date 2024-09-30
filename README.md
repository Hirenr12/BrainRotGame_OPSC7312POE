
# BrainRotGames

## Overview



## Table of Contents

- [Overview](#overview)
- [YouTube Link](#youtube-link)
- [Features](#features)
- [Games](#games)
- [Prerequisites For Developer Usage](#Prerequisites-for-developer-usage)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Code Attribution](#code-attribution)
## YouTube Link Has Audio
## YouTube Link


## Features

### Homepage:
-

### Registration:
-

### Login:
-

### GamePortal:
-

### Favorites:
-

### Mystery Game:
-

### Players Journal:
-

### Player Profile
- Displays the user’s information such as Username, Tier, and total games played.
- Profile information is stored in Firestore and is unique to each user.

### Player Profile Customization
- Users can change their profile avatar, username, and view their gaming badges.
- Avatar customization is tied to user tiers, and points determine which avatars are available for selection.

### Global LeaderBoard:
- Showcases the overall rankings of all users, based on the scores earned from playing different games.
- Scores are calculated, retrieved, and updated in real time through the app’s REST API, with all data seamlessly stored in Firebase Firestore.
- Users can view their current rankings, and the global leaderboard is dynamically refreshed to reflect live performance across all games.

### Private LeaderBoard
- Private Leaderboard: Allows users to create exclusive leaderboards to compete with selected friends or specific players.
- Users can add others from the global leaderboard, which will automatically include them in their private leaderboard.
- Each private leaderboard operates independently from the global rankings and is fully customizable to feature only chosen participants.
- All leaderboard functionalities, including fetching data from Firestore, adding users, and updating scores, are efficiently managed through our REST API, ensuring seamless real-time synchronization and personalized leaderboard experiences.

### API:
- A RESTful API, hosted on Google Cloud, is responsible for managing leaderboard data and high score calculations.
- The API interacts with Firebase Firestore to retrieve, update, and store user high scores, which are used to determine each user's ranking on the leaderboard

Endpoints include:

    Score Routes

    POST /scores/scoress
        Submits a user's game score to the server and updates the leaderboard accordingly.

    GET /leaderboard/global/allGames
        Fetches a list of all available games in the global leaderboard.

    GET /leaderboard/private/privateGames/:currentUser
        Retrieves all private games for the currently logged-in user.

    GET /leaderboard/global/:gameName
        Fetches the global leaderboard for a specific game by its name.

    POST /leaderboard/private/:gameName/:currentUser/:selectedUser
        Adds a selected user to the currently logged in user's private leaderboard for a specified game.

    GET /leaderboard/private/:gameName/:username
        Retrieves the private leaderboard for a specific game and user.
        

### Firebase Cloud Messaging:
- Firebase Cloud Messaging (FCM) is leveraged to deliver real-time notifications to users, ensuring instant communication and updates. FCM allows the app to send timely alerts even when the app is running in the background or closed. This enhances user engagement and ensures that important information is promptly received.


### Support AI
- The app integrates AI-powered support through Gemini, which powers a support chatbot.
- The chatbot assists users by troubleshooting common issues, answering general questions, and providing guidance on how to use the app's features.
- The AI chatbot is available 24/7, enhancing the user experience by offering instant help and support.

### Community and Feedback
The app includes a Community and Feedback page where users can:

    Converse with other players, share tips, strategies, and opinions about the games.
    Submit feedback to the development team, which is stored in Firebase Firestore.
    The feedback mechanism allows us to receive direct user input on improving the app, which helps guide future updates and feature improvements.

### Privacy Policy
- The app includes a Privacy Policy to ensure transparency regarding how user data is collected, stored, and used.
- The privacy policy outlines the use of Firebase services, the collection of data (such as usernames, points, and leaderboard entries), and how this information is protected.


### Settings
-


## Games

### TicTacToe
- Description: TicTacToe is a classic two-player game where the objective is to get three of your marks in a row (horizontally, vertically, or diagonally) on a 3x3 grid while preventing your opponent from doing the same.
- Features:
  - Player vs Player mode
  - Simple and intuitive UI
  - Instant win or draw detection
  - Reset and play again functionality
- How to Play:
  1. Players take turns placing their marks (X or O) in empty squares.
  2. The first player to align three marks in a row wins.
  3. If all squares are filled and no player has won, the game ends in a draw.

### Floppy Bird
- A fun and engaging game inspired by the classic Flappy Bird, where players guide a bird through a series of obstacles, earning points based on their progression.
- Players accumulate points throughout the game, which are added to their account after each session, reflecting their performance.
- Scores are updated on the global leaderboard in real time, with high score validations and updates seamlessly managed by the app’s API, ensuring accurate ranking and recognition of player achievements.

### Colour Matcher
- A quick-thinking game where the player must determine whether the font color matches the word's spelling.
- Players are shown words with different font colors and must answer True or False depending on whether the displayed font color matches the color the word spells out.
- Points are awarded based on accuracy and speed, and these points contribute to both the global and private leaderboards.

## Getting Started

To get started with BrainRotGames, follow these steps:

1. Clone or download the repository to your local machine:
   ```bash
   git clone [https://github.com/]

3. Application Note:
   ```bash
  



## Prerequisites For Developer Usage

Before you begin, ensure that you have the following requirements:

- **Android Studio**: Make sure you have the latest version of Android Studio installed. You can download it from the [official website](https://developer.android.com/studio). Android Studio is required to build and run the application on your local environment.
  
- **Java Development Kit (JDK)**: Ensure you have JDK 17 installed, as it's the version used in this project. You can download it from [AdoptOpenJDK](https://adoptopenjdk.net/) or [Oracle's official website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).

- **Firebase Account**: You need to set up a Firebase project to enable Firebase Authentication, Firestore, and Google Sign-In. You can create a Firebase project at the [Firebase Console](https://console.firebase.google.com/).

- **Google Services Configuration**: Add the `google-services.json` file to your Android project, which you can download from your Firebase console once you've set up Firebase Authentication and Firestore DB.

- **CircleCI Account**: Set up a CircleCI account and connect it to your GitHub repository for continuous integration. Visit the [CircleCI website](https://circleci.com) for more details.

- **SonarQube Setup**: Install and configure SonarQube for code quality checks. You can either use the [SonarQube cloud service](https://sonarcloud.io) or set it up locally.

- **Git**: Ensure that you have Git installed to clone the repository and manage version control. Download it from [Git SCM](https://git-scm.com/).

- **Gradle**: Make sure Gradle is installed and configured on your system. You can follow the instructions on the [official Gradle website](https://gradle.org/install/).

Once these prerequisites are set up, you should be ready to clone the project and start working with the code.


   
## Usage




## Testing

We have implemented automated testing in the CI/CD pipeline for this project using GitHub Actions. This testing process ensures code quality and functionality before any code is merged into the master branch. Here's how the testing is set up:

****1. Unit Tests with Gradle****

As part of the build process, the project includes unit tests that are executed using Gradle. Each push to the master branch triggers the CI pipeline, where these tests are run automatically.

- **Gradle Test Command**: 

- **Test Results**: If any tests fail, the pipeline is stopped, and the issues are reported in the GitHub Actions log, preventing the code from being merged.

****2. Continuous Integration (CI) Pipeline****

The pipeline is configured in `.github/workflows/build.yml` and includes the following key steps:

- **Set up JDK 17**: Ensures that the correct Java version is used.
  
- **Run Unit Tests**: Executes the tests via the `./gradlew test` command.
  
- **Build Project**: Builds the project using `./gradlew build` after successful testing.


****3. Test Workflow and Github Actions****
```yaml

The YML can be found under actions in the repository
BrainRotGame_OPSC7312POE\.github\workflows\generate-apk-aab-debug-release.yml

```

### Code Quality and Coverage

The project is integrated with SonarQube to measure code quality, coverage, and potential duplications. This integration helps ensure that the code adheres to best practices and maintains high-quality standards.

- **SonarQube Report**: After each build, a SonarQube analysis is triggered to ensure that the code meets our quality gate. This process involves evaluating various metrics such as code coverage, the presence of bugs, and potential security vulnerabilities. If any of these metrics fall below acceptable thresholds, the quality gate will prevent the code from progressing through the CI/CD pipeline.

### Artifacts

After passing all tests, the APKs and AAB (App Bundle) files are generated and uploaded as artifacts for further manual testing. You can find the generated APKs and AAB files in the GitHub Actions artifacts section after the build is complete. This allows for easy access to build outputs for deployment or additional testing processes.

In summary, integrating SonarQube into our workflow not only enhances code quality but also ensures that we maintain high standards throughout our development process. The use of artifacts facilitates efficient testing and deployment practices within our CI/CD pipeline.




## Contributing
We thank the following contributors for their valuable input to this project:

- [Hiren Rahul Thulasaie ST10053063](https://github.com/Hirenr12)
- [Ethan Swanepoel ST10198049](https://github.com/Ethan-Swanepoel)
- [Ameer Inder Kajee ST10063110](https://github.com/AI-Kajee)
- [Nehal Singh ST10184628](https://github.com/st10184628)



  
## License

This project is licensed under the **MIT License**. 

### Third-Party Services and Resources

This project uses various third-party services and resources, each of which may have its own licensing terms. Please review the following licenses for each service or resource utilized:

- **Android Studio**: Android Studio is an IDE developed by Google and is subject to the [Android Software Development Kit (SDK) License Agreement](https://developer.android.com/studio/terms).
  
- **CircleCI**: CircleCI is a continuous integration and delivery platform. You can review its [Terms of Service](https://circleci.com/terms-of-service) and its [Privacy Policy](https://circleci.com/privacy).
  
- **SonarQube**: SonarQube is used for continuous code quality and security inspection. SonarQube is available under the [GNU Lesser General Public License v3.0](https://www.gnu.org/licenses/lgpl-3.0.html).

- **Google Services** (including Firebase Authentication, Firestore DB, and Google Sign-In):
  - Firebase Authentication and Firestore DB are part of Google Firebase and are subject to the [Firebase Terms of Service](https://firebase.google.com/terms) and the [Google Cloud Platform Terms of Service](https://cloud.google.com/terms).
  - Google Sign-In is subject to the [Google API Terms of Service](https://developers.google.com/terms).

- **Google Cloud** Google Cloud: The project’s RESTful API is hosted on Google Cloud and is responsible for managing:
  - leaderboard data
  - high score calculations
  - and facilitating communication with Firebase Firestore for real-time updates. The API is subject to the Google Cloud Platform Terms of Service.

Please ensure compliance with the respective licenses of these third-party services when using, modifying, or distributing this project.

## Acknowledgments
- **The Firebase Cloud**: Messaging (FCM) setup and notification functionality were implemented by following the official documentation provided by Firebase for setting up FCM on Android. The setup and code examples were adapted from the Firebase Cloud Messaging for Android (Kotlin) guide: [https://firebase.google.com/docs/cloud-messaging/android/client#kotlin+ktx_1]
- **RESTFUL API**: The API deployment for this project was completed using Google Cloud Platform, which provides API management services and allows secure, scalable API hosting on Google Cloud. The process for deploying the API was based on the guidelines and instructions provided in the YouTube tutorial from Fireship: [https://www.youtube.com/watch?v=-MTSQjw5DrM&t=274s]
- **The Gemini AI**: support chatbot was integrated based on the Gemini API Quickstart for Android. For implementation details, see: Gemini API Quickstart.

## Code Attribution
>
>__________ **Code Attribution** __________     
> 
>The following method was taken from Firebase:   
>Author: Firebase
>Link: [https://firebase.google.com/docs/cloud-messaging/android/client#kotlin+ktx_1]  
>
>     class MyFirebaseMessagingService : FirebaseMessagingService() {
>         override fun onMessageReceived(remoteMessage: RemoteMessage) {
>             // Log the sender of the message
>             Log.d(TAG, "From: ${remoteMessage.from}")
>
>             // Check if the message contains a notification payload
>             remoteMessage.notification?.let {
>                 Log.d(TAG, "Message Notification Body: ${it.body}")
>                 sendNotification(it.body)
>             }
>
>             // Also display a toast with the message details
>             sendToast(remoteMessage.from, remoteMessage.notification?.body)
>         }
>
>         private fun sendToast(from: String?, body: String?) {
>             // Ensure the body is not null
>             body?.let {
>                Handler(Looper.getMainLooper()).post {
>                     // Display a toast message with the sender and body
>                     Toast.makeText(applicationContext, "$from -> $body", Toast.LENGTH_SHORT).show()
>                 }
>             }
>         }
>
>         private fun sendNotification(messageBody: String?) {
>             // Ensure the message body is not null
>             messageBody?.let {
>                 val intent = Intent(this, MainActivity::class.java)
>                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
>
>                 // Using FLAG_IMMUTABLE for PendingIntent to ensure compatibility with API 31+
>                 val pendingIntent = PendingIntent.getActivity(
>                     this,
>                     0, // requestCode
>                     intent,
>                     PendingIntent.FLAG_IMMUTABLE
>                 )
>
>                 val channelId = "My Channel ID"
>                 val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
>                 val notificationBuilder = NotificationCompat.Builder(this, channelId)
>                     .setSmallIcon(R.drawable.ic_stat_notification)
>                     .setContentTitle("My New Notification")
>                     .setContentText(messageBody)
>                     .setAutoCancel(true)
>                     .setSound(defaultSoundUri)
>                     .setContentIntent(pendingIntent)
>
>                 val notificationManager =
>                     getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
>
>                 // Since Android Oreo (API 26), a notification channel is required
>                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
>                     val channel = NotificationChannel(
>                         channelId,
>                         "Channel human-readable title",
>                         NotificationManager.IMPORTANCE_DEFAULT
>                     )
>                     notificationManager.createNotificationChannel(channel)
>                 }
>
>                 val notificationId = 0
>                 notificationManager.notify(notificationId, notificationBuilder.build())
>             }
>         }
>
>         companion object {
>             private const val TAG = "MyFirebaseMsgService"
>         }
>     }
>        __________ **end** __________
>
>
>__________ **Code Attribution** __________     
> 
>The following method was taken from Firebase:   
>Author: Firebase
>Link: [https://firebase.google.com/docs/cloud-messaging/android/client#kotlin+ktx_1] 
>
>     //Firebase Messaging
>             FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
>                 if (!task.isSuccessful) {
>                     Log.w(TAG, "Fetching FCM registration token failed", task.exception)
>                     return@OnCompleteListener
>                 }
>
>                 // Get new FCM registration token
>                 val token = task.result
>
>                 // Log and toast
>                 val msg = getString(R.string.msg_token_fmt, token)
>                 Log.d(TAG, msg)
>                 Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
>             })
>        __________ **end** __________
>
>
>__________ **Code Attribution** __________     
> 
>The following method was taken from The Stack Overflow Blog:   
>Author 1: John Au-Yeung
>Link: [https://stackoverflow.blog/author/john-au-yeung/]
>Author 2: Ryan Donovan
>Link: [https://stackoverflow.blog/author/rdonovan/]
>
>Link to website: [https://stackoverflow.blog/2020/03/02/best-practices-for-rest-api-design/]
>
>     const express = require('express');
>     const bodyParser = require('body-parser');
>
>     const app = express();
>
>     // employees data in a database
>     const employees = [
>       { firstName: 'Jane', lastName: 'Smith', age: 20 },
>       //...
>       { firstName: 'John', lastName: 'Smith', age: 30 },
>       { firstName: 'Mary', lastName: 'Green', age: 50 },
>
>
>     app.use(bodyParser.json());
>
>     app.get('/employees', (req, res) => {
>       const { firstName, lastName, age } = req.query;
>       let results = [...employees];
>       if (firstName) {
>         results = results.filter(r => r.firstName === firstName);
>       }
>
>       if (lastName) {
>         results = results.filter(r => r.lastName === lastName);
>       }
>
>       if (age) {
>         results = results.filter(r => +r.age === +age);
>       }
>       res.json(results);
>     });
>
>     app.listen(3000, () => console.log('server started'));
>        __________ **end** __________
>
>



