const admin = require('firebase-admin');
const db = admin.firestore();

exports.getLeaderboard = async (req, res) => {
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
};
