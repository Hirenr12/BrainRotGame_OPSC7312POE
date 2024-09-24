const admin = require('firebase-admin');
const db = admin.firestore();

exports.getChallenges = async (req, res) => {
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
};

exports.updateChallengeStatus = async (req, res) => {
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
};
