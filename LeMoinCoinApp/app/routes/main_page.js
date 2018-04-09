var express = require('express');
var router = express.Router();

module.exports = function(app, db) {
  // Get request for the homepage.
  // Ensure that user is authenticated to access certain pages.
  app.get('/', (req, res) => {
      res.render('main_page.html');
  });
}
