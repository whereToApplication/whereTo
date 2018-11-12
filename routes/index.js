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
