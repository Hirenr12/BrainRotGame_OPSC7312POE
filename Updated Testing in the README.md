
# BrainRotGames

## Overview



## Table of Contents

- [Overview](#overview)
- [YouTube Link](#youtube-link)
- [Features](#features)
- [Games](#games)
- [Prerequisites](#prerequisites)
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

## Prerequisites

Before you begin, ensure that you have the following requirements:

- **Android Studio**:
- 
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

## Acknowledgments


## Code Attribution

