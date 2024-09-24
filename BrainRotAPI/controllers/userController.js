const admin = require('firebase-admin');
const db = admin.firestore();

exports.updateUserProfile = async (req, res) => {
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
};
