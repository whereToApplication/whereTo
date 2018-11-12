const mongoose = require('mongoose');

let Schema = mongoose.Schema;

let user_schema = new Schema({
  firstname: String,
  lastname: String,
  email: String,
  os: String,
});

module.exports = mongoose.model('User', user_schema);
