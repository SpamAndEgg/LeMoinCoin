const express      = require('express');
var bodyParser   = require('body-parser');
var path           = require('path');

var app            = express();

// Parsing and encoding for reading HTML post request bodys.
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true
}));

// View Engine
// Views are stored in folder 'views'.
//app.set('views', path.join(__dirname, 'views'));
app.set('views', './app/views');
// Loading HTML render engine.
app.engine('html', require('./htmlEngine'));
// Set view engine to HTML.
app.set('view engine', 'html');

// Set folders for further use.
app.use('/cssFiles', express.static(__dirname + '/app/public/css'));
app.use('/nodeModules', express.static(__dirname + '/node_modules'))

const port = 8000;

require('./app/routes')(app, {});
app.listen(port, () => {
  console.log("We are live on " + port);
});
