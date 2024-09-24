const express = require('express');
const admin = require('firebase-admin');
const bodyParser = require('body-parser');

// Initialize Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const app = express();
app.use(bodyParser.json());

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

// Middleware to verify Firebase auth token
const verifyToken = async (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1]; // Assuming Bearer token

  if (!token) {
    return res.status(401).send('Unauthorized: No token provided.');
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.uid = decodedToken.uid;
    next();
  } catch (error) {
    res.status(401).send('Unauthorized: Invalid token.');
  }
};

// User Profile Management (PUT to update user profile)
app.put('/users/:uid', verifyToken, async (req, res) => {
  const uid = req.params.uid;
  const { username, tier, image, totalGamesPlayed, badges } = req.body;

  try {
    await db.collection('userProfiles').doc(uid).update({
      username,
      tier,
      image,
      totalGamesPlayed,
      badges,
    });
    res.status(200).send('User profile updated successfully.');
  } catch (error) {
    res.status(500).send('Error updating profile: ' + error.message);
  }
});

// Game Score Management (POST to submit score)
app.post('/scores', verifyToken, async (req, res) => {
  const { gameName, uid, score } = req.body;

  try {
    const userDoc = db.collection('leaderboard').doc(`${gameName}-${uid}`);
    const userData = await userDoc.get();

    if (!userData.exists || userData.data().highScore < score) {
      await userDoc.set({ gameName, username: uid, highScore: score });
      res.status(200).send('Score submitted and leaderboard updated.');
    } else {
      res.status(200).send('New score is not higher than the current high score.');
    }
  } catch (error) {
    res.status(500).send('Error submitting score: ' + error.message);
  }
});

// Fetch Leaderboard (GET for a specific game)
app.get('/leaderboard/:gameName', async (req, res) => {
  const gameName = req.params.gameName;

  try {
    const leaderboardRef = db.collection('leaderboard');
    const snapshot = await leaderboardRef.where('gameName', '==', gameName)
      .orderBy('highScore', 'desc')
      .get();

    if (snapshot.empty) {
      res.status(404).send('No leaderboard found.');
      return;
    }

    const leaderboard = [];
    snapshot.forEach(doc => {
      leaderboard.push(doc.data());
    });

    res.status(200).json(leaderboard);
  } catch (error) {
    res.status(500).send('Error fetching leaderboard: ' + error.message);
  }
});

// Fetch Daily Challenges (GET to retrieve challenges)
app.get('/challenges', async (req, res) => {
  try {
    const challengesRef = db.collection('challenges');
    const snapshot = await challengesRef.get();

    const challenges = [];
    snapshot.forEach(doc => {
      challenges.push(doc.data());
    });

    res.status(200).json(challenges);
  } catch (error) {
    res.status(500).send('Error fetching challenges: ' + error.message);
  }
});

// Update Challenge Status for User (PUT to update user challenge progress)
app.put('/challenges/:uid', verifyToken, async (req, res) => {
  const uid = req.params.uid;
  const { challengeId, completionStatus } = req.body;

  try {
    const userChallengeDoc = db.collection('userChallenges').doc(`${uid}-${challengeId}`);
    await userChallengeDoc.set({
      challengeId,
      completionStatus,
    });
    res.status(200).send('Challenge status updated.');
  } catch (error) {
    res.status(500).send('Error updating challenge status: ' + error.message);
  }
});

// Global error handler for uncaught errors
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Something went wrong!');
});
