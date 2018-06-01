// Import web3
const LeMoinCoinContract = require('../../LeMoinCoinContract');
const sign = require('ethjs-signer').sign;
const BN = require('bignumber.js');
var Tx = require('ethereumjs-tx');

// Load the LeMoinCoin Contract data.
var LeMoinCoin = LeMoinCoinContract.LeMoinCoin;

module.exports = function(app, db) {
  app.post('/notes', (req, res) => {
    res.send('Hello')
  })

  app.get('/getnote', (req, res) => {
    res.send("This did actually work!")
    //res.send('{"account":{"balance":12,"id":1234,"name":"Marlon"}}');
  })

  app.post('/get_balance', (req, res) => {
    var pub_key = req.body.pub_key;
    var balance = LeMoinCoin.balanceOf(pub_key);
    res.send(balance);
  })

  // TRANSFER COINS
  app.post('/transfer', (req, res) => {

    const pri_key = req.body.pri_key;
    const pub_key = req.body.pub_key;
    const send_to_add = req.body.send_to;
    const amount = req.body.amount;

    console.log(web3.eth.getTransactionCount(pub_key) + 1);

    // Get the nonce of the sender.

    var gas_price = LeMoinCoinContract.web3.eth.gasPrice;
    const gasPriceHex = web3.toHex(gas_price);
    const gasLimitHex = web3.toHex(3000000);

    var raw_transaction = {
      from: pub_key,
      to: LeMoinCoin.address,
      value: 5000,
      gas: "0x7458",
      gasPrice: "0x04e3b29200",
      nonce: web3.eth.getTransactionCount(pub_key),
      data: LeMoinCoin.transfer.getData(send_to_add, 255, {from: pub_key}),
    };

    var transaction = new Tx(raw_transaction);


    console.log(transaction);
    var pri_key_buffer = new Buffer(pri_key, 'hex');
    transaction.sign(pri_key_buffer);

    var serializedTx = transaction.serialize();

    web3.eth.sendRawTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
        if (!err)
            console.log(hash);
        else
            console.log(err);
    });

    res.send("Transaction complete")
  })
}
