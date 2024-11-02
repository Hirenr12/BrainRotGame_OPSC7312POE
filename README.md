
# BrainRotGames

## Overview
**BrainRotGames** is a mobile gaming app that offers a diverse collection of games, designed to provide an immersive and engaging experience for users. By integrating Firebase services such as Authentication and Firestore, the app allows players to securely sign in, track their progress, and enjoy personalized experiences. With a strong focus on community interaction, the app encourages users to compete with others, unlock rewards, and continuously improve their skills across various games, all while ensuring data is synced in real-time for a seamless experience.

## Table of Contents

- [Overview](#overview)
- [YouTube Link Has Audio](#youtube-link-has-audio)
- [Features](#features)
- [Firebase Authentication](#firebase-authentication)
- [Firebase Firestore DB](#firebase-firestore-dB)
- [Offline Functionality Overview](#pffline-functionality-overview)
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
[OPSC7312 BRAINROTGAMES POEPART2](https://youtu.be/ZmuDhQ1Mh9M?si=ZSWDIT8Zycf2Pyte)


## Features

### Homepage:
- Welcoming interface with game title and subtitle.
- Buttons for user registration and login.
  
### Registration:
-The registration feature allows new users to create an account in the application. Below is an overview of how the registration process works.


### Login:

The login feature allows users to access their accounts using either email/password authentication or Google Single Sign-On (SSO).

#### Features
- **Email and Password Authentication**:
  - Users can enter their registered email and password to log in.
  - Validation ensures that both fields are filled before attempting to log in.

- **Google Single Sign-On (SSO)**:
  - Users can log in using their Google account, simplifying the authentication process and enhancing user experience.


### GamePortal

The **GamePortal** is a central hub within the application that allows users to explore and play various games. It features a user-friendly interface that organizes games in a grid layout, enabling easy navigation and selection. Key functionalities of the GamePortal include:

- **Game Listing:** The portal displays a list of available games, each represented by a title and an image. The current selection includes popular games like **RetroSnake**, **Tic Tac Toe**, **Flappy Bird**, and **Colour Matcher**.

- **Favorites Management:** Users can mark games as favorites, enhancing their engagement by allowing them to prioritize and quickly access their preferred games. The favorite status is persisted in Firebase Firestore, ensuring that preferences are retained across sessions.

- **Game Navigation:** Users can navigate to specific game details and gameplay activities by clicking on the respective game tiles. Each game leads to its dedicated activity, where users can engage in gameplay.

- **User Authentication:** The portal integrates Firebase Authentication, allowing users to sign in and access their personalized game experience. It also includes a sign-out functionality to ensure secure user sessions.

- **Mystery Game Unlocking:** The GamePortal features a unique element of mystery games, which can be unlocked based on user points. Users earn points through gameplay, and upon reaching specific thresholds, they can unlock special games, enhancing the excitement of discovery.

- **Toolbar Navigation:** The top toolbar provides quick access to additional features, such as user profile, settings, leaderboards, and a personal journal.

This combination of features aims to create an engaging and interactive gaming environment, encouraging users to explore and enjoy their favorite games while discovering new ones.

### Players Journal

The **Players Journal** is a feature within the application that tracks and displays user progress through a tier-based system. This interactive journal allows players to monitor their achievements and points, enhancing their engagement and motivation. Key features of the Players Journal:

- **Tier System:** Users progress through a series of tiers based on the points they accumulate. Each tier represents a level of achievement, starting from **Fresh Meat** and advancing to the **Dread Lord**. Each tier is associated with a specific point threshold and an image, providing visual representation of the user's progress.

- **Dynamic Updates:** The journal dynamically updates as users earn points. It retrieves the current user's points from Firebase Firestore and displays all unlocked tiers based on their point total. This ensures that users always have up-to-date information about their progress.

- **User Authentication:** The Players Journal integrates Firebase Authentication to ensure that each user's data is securely retrieved and displayed. Only authenticated users can access their personal journal, providing a customized experience.

- **RecyclerView for Display:** The journal uses a `RecyclerView` to efficiently display the list of unlocked tiers. This approach allows for smooth scrolling and a responsive layout, enhancing the user experience.

- **Points Management:** The system includes a method to manage user points, allowing for future enhancements like adding points through gameplay or achievements. Currently, it verifies user points upon journal access to ensure the correct tiers are displayed.

The Players Journal aims to provide users with a sense of achievement and progression, encouraging them to engage more deeply with the app's features and games. By visualizing their journey through tiers, users are motivated to continue playing and earning points.


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


## Firebase Authentication

Firebase Authentication is used to manage user sign-ins and authentication securely. It supports multiple authentication providers, allowing users to sign in via email, Google, or other identity providers.

### Authentication Providers Used:
- **Email and Password**: Users can sign up and log in using their email and password, which are securely managed by Firebase Authentication.
- **Google Sign-In**: Users can sign in using their Google accounts, which provides a seamless and secure authentication experience.

### Features:
- **Secure User Management**: Firebase Authentication handles user credentials securely. Passwords are never stored in plain text, and authentication tokens are generated for each session.
- **Firebase User Sessions**: Once authenticated, users receive an ID token that grants them access to their Firestore data, games, and leaderboards.
- **Google Sign-In Integration(SSO)**: Allows users to quickly authenticate using their existing Google account, simplifying the login process and improving user experience.
  
### Authentication Flow:
1. **Sign Up**: New users can sign up with their email and password or use Google Sign-In to register instantly.
2. **Login**: Existing users can log in using the method they registered with.
3. **Token Management**: After successful login, a token is generated, which is used for authentication throughout the app, allowing secure access to Firestore data.
4. **Session Persistence**: Firebase Authentication ensures user sessions are persistent even when the app is closed.

### Security:
- All authentication data is encrypted.
- Firebase Authentication also supports two-factor authentication for enhanced security.

For more details, refer to the official [Firebase Authentication documentation](https://firebase.google.com/docs/auth).


## Firebase Firestore DB

Firebase Firestore is used to store and sync the app's data in real time, ensuring that all user data, leaderboards, and game statistics are updated instantly.

### Usage in BrainRotGames:
- **User Data**: Stores user profiles, including usernames, avatars, and badges.
- **Leaderboard Data**: Holds the scores for global and private leaderboards, updating them in real time.
- **Game Statistics**: Records game history, player performance, and game-specific stats.
- **Feedback and Community Data**: Stores user-submitted feedback and interactions from the community page.

### Key Firestore Collections:
- `Users`: Stores the personal information of each user such as usernames, email, and profile settings.
- `tips`: Collects feedback from users regarding app performance, feature requests, and issues.

### Firestore Security:
Firestore rules ensure that user data is secure and only accessible to authorized users. Here are a few key points about the security setup:
- **Authenticated Access Only**: Data access is restricted to authenticated users.
- **Role-Based Access Control**: Leaderboard data and private user data are protected based on user roles and permissions.
- **Real-Time Updates**: All data written to Firestore is synced in real time across all devices and users.

For more information, check out the official [Firestore documentation](https://firebase.google.com/docs/firestore).

## Offline Functionality Overview

The app is designed to provide a seamless offline experience, ensuring that essential features remain accessible even without internet connectivity.

### Key Components

1. **Network State Detection**
   - The app checks the network status using `ConnectivityManager` in the Login activity.
   - Based on the `isOnline` status, it either authenticates online with Firebase or uses cached credentials for offline access.

2. **Session Management with UserSessionManager**
   - User login details (e.g., user ID, email, login type) are stored in `SharedPreferences` via the `UserSessionManager`.
   - Cached data enables users to log in offline, provided they logged in previously and remained logged in.

3. **Offline Google, Biometric Login, email & password**
   - Cached Google and and email & password biometric login credentials allow users to access the app offline if they initially logged in with Google or their biometrics or email & password.

4. **Firestore Offline Persistence for ActivityPlayersJournal**
   - Firestore’s offline persistence stores data locally, allowing access to user points and tier data offline.
   - Data retrieval first attempts to pull from the server, and if offline, falls back to cached data (`Source.CACHE`), ensuring continuity of progress.

5. **Error Handling and Notifications**
   - Clear messages inform users when data isn’t available offline (e.g., “No cached data available”).
   - Users are notified at launch if the app is operating in offline mode, setting expectations for limited functionality.

6. **Points and Tiers Update**
   - The app dynamically updates unlocked tiers based on cached points, allowing users to view their progress offline.
   - Syncs automatically when connectivity resumes, maintaining a consistent user experience.

### Benefits of Offline Functionality

This approach optimizes accessibility and user experience by:
- Providing core functionality offline, even with limited connectivity.
- Automatically updating user data once the app reconnects, ensuring progress synchronization without additional steps.


## Games

### RetroBrickBreaker

**Description:** RetroBrickBreaker is an engaging brick-breaking game where players control a paddle to bounce a ball and break bricks while aiming for a high score.

#### Features:
- Swipe or button controls for paddle movement
- Dynamic ball movement and brick generation
- Score tracking with a local high score system
- Firebase integration to submit scores and retrieve player data

#### How to Play:
1. Use swipe controls or on-screen buttons to move the paddle left and right.
2. Bounce the ball to break the bricks and score points.
3. Avoid letting the ball fall past the paddle to continue the game.
4. Press the "New Game" button to restart after a game over.

### RetroSnake

**Description:** RetroSnake is a modern take on the classic snake game where players control a growing snake that must eat food to grow without colliding into the walls or itself.

#### Features:
- Swipe or button controls for movement (up, down, left, right)
- Dynamic snake movement and food generation
- Score tracking with a local high score system
- Firebase integration to submit scores and fetch player data

#### How to Play:
1. Swipe in the direction you want the snake to move, or use the on-screen buttons.
2. Guide the snake to eat food and grow longer, but be careful to avoid hitting the walls or yourself.
3. When the snake collides with an obstacle, the game ends, and your score is recorded.


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
   ```bash
Note: Currently, there is no external usage of Firestore DB as the app has not been published yet. The database and its features are being used internally during development and testing phases.
Internal Data Stores and manages user profiles, game statistics, and leaderboards for internal testing purposes.
Future Use Upon app release, Firestore will handle real-time data for user interactions, leaderboards, and game statistics.

```


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
>
>__________ **Code Attribution** __________     
> 
>The following method was taken from The Stack Overflow Blog:   
>Author 1: John Au-Yeung
>Link: [https://stackoverflow.blog/author/john-au-yeung/]
>
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



