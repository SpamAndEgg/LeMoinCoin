const routes = require('./routes');
const mainPage = require('./main_page');

module.exports = function(app, db) {
  routes(app, db);
  mainPage(app, db);
}
