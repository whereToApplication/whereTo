"use strict";
var express = require('express');
var router = express.Router({mergeParams:true});
let mongoose = require('mongoose');

let feedbackModel =require('../models/feedback.js')
let userModel  = require('../models/user.js')
/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index');
});

router.post('/api/feedback', function(req, res, next) {
  console.log(req.body)
  if(!req.body.feedback || req.body.feedback.length < 5) {
    console.log('Hello!')
    return res.status(401).send({
      error: 'Missing feedback or feedback was less than 5 characters'
    })
  }
  let feedback_text = req.body.feedback;
  let user = 'You';
  let feedback = new feedbackModel({
    user,
    feedback: feedback_text
  });
  console.log('testings')
  feedback.save(function(err, response) {
    if (err) {
      return res.status(502).json({
        error: 'Could not save feedback'
      })
    }
    return res.status(200).json({
      success: 'Feedback was a success! ',
      response
    })
  })
});

router.post('/register', function(req, res, next) {
  console.log('get into feedback');
  let fname = req.body.fname.toLowerCase();
  let lname = req.body.lname.toLowerCase();
  let email = req.body.email.toLowerCase();
  let os = req.body.ospicker;
  let user = new userModel({
    firstname: fname,
    lastname: lname,
    email,
    os
  });
  console.log(fname, lname, email, os);
  userModel.find({email:email}, function(err, res2) {
    if(res2.length>0) {
      console.log(err);
      res.render('index', {title: 'You have already registered! Thank you!'})
    } else {
      user.save(function(err, res3) {
        if (err) return console.error(err);
        console.log(res3)
        res.render('index', {title: 'Thank you so much for Registering!'});
      })
    }
  })
});


module.exports = router;
