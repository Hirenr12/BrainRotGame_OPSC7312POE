
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


### Player Profile:
-

### Player Profile Customization:
-

### LeaderBoard:
-

### Personalised LeaderBoard
-

### Support and Feedback
-

### Settings
-


## Games


## Getting Started

To get started with BrainRotGames, follow these steps:

1. Clone or download the repository to your local machine:
   ```bash
   git clone [https://github.com/]

3. Application Note:
   ```bash
  


  
   
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


****2. Test Workflow and Github Actions****
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

Please ensure compliance with the respective licenses of these third-party services when using, modifying, or distributing this project.

## Acknowledgments


## Code Attribution

