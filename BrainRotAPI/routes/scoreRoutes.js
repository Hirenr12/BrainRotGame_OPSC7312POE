const express = require('express');
const router = express.Router();
const { submitScore } = require('../controllers/scoreController');
const { verifyToken } = require('../middlewares/authMiddleware');

router.post('/', verifyToken, submitScore);

module.exports = router;
