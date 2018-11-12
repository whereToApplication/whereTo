const mongoose = require('mongoose');

let Schema = mongoose.Schema;

let feedback_schema = new Schema({
  user: String,
  feedback: String
});

module.exports = mongoose.model('Feedback', feedback_schema);
