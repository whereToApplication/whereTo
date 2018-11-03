"use strict";
var express = require('express');
var router = express.Router({mergeParams:true});
let mongoose = require('mongoose');

let feedbackModel  = require('../models/feedback.js')

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/feedback', function(req, res, next) {
  console.log('get into feedback');
  console.log(req.body);
  let feedback_text = req.body.feedback;
  console.log('after feeback_text'); 
  let user = 'You';
  let feedback = new feedbackModel({
    user,
    feedback: feedback_text
  });
  feedback.save(function(err, res) {
    if (err) return console.error(err);
    console.log(res);
  })
  res.render('index', {title: 'After submit'});

});

module.exports = router;
