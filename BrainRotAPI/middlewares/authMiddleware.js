const admin = require('firebase-admin');

exports.verifyToken = async (req, res, next) => {
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
