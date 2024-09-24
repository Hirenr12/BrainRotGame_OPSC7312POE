const express = require('express');
const admin = require('firebase-admin');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes');
const scoreRoutes = require('./routes/scoreRoutes');
const challengeRoutes = require('./routes/challengeRoutes');
const leaderboardRoutes = require('./routes/leaderboardRoutes');

const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const app = express();
app.use(bodyParser.json());

const PORT = process.env.PORT || 3000;

// Use routes
app.use('/users', userRoutes);
app.use('/scores', scoreRoutes);
app.use('/challenges', challengeRoutes);
app.use('/leaderboard', leaderboardRoutes);

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

// Global error handler for uncaught errors
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Something went wrong!');
});
