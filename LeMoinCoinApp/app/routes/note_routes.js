module.exports = function(app, db) {
  app.post('/notes', (req, res) => {
    res.send('Hello')
  })

  app.get('/getnote', (req, res) => {
    res.send("This did actually work!")
    //res.send('{"account":{"balance":12,"id":1234,"name":"Marlon"}}');
  })
}
