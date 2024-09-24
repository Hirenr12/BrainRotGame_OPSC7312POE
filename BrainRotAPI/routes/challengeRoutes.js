const express = require('express');
const router = express.Router();
const { getChallenges, updateChallengeStatus } = require('../controllers/challengeController');

router.get('/', getChallenges);
router.put('/:uid', updateChallengeStatus);

module.exports = router;
