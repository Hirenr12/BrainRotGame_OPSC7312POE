const admin = require('firebase-admin');
const db = admin.firestore();

exports.submitScore = async (req, res) => {
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
};
