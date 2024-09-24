const express = require('express');
const router = express.Router();
const { updateUserProfile } = require('../controllers/userController');
const { verifyToken } = require('../middlewares/authMiddleware');

router.put('/:uid', verifyToken, updateUserProfile);

module.exports = router;
