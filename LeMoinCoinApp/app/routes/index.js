const noteRoutes = require('./note_routes');
const mainPage = require('./main_page');

module.exports = function(app, db) {
  noteRoutes(app, db);
  mainPage(app, db);
}
