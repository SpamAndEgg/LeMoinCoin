// Import web3
const LeMoinCoinContract = require('../../LeMoinCoinContract');
const sign = require('ethjs-signer').sign;
const BN = require('bignumber.js');
var Tx = require('ethereumjs-tx');
var eth_add_from_pk = require('../../eth_add_from_pk');

// Load the LeMoinCoin Contract data.
var LeMoinCoin = LeMoinCoinContract.LeMoinCoin;
var track_n_transaction = null;

module.exports = function(app, db) {
  app.post('/notes', (req, res) => {
    res.send('Hello')
  })

  app.get('/getnote', (req, res) => {
    res.send("This did actually work!")
    //res.send('{"account":{"balance":12,"id":1234,"name":"Marlon"}}');
  })

  // Get the token balance of a given account.
  app.post('/get_balance', (req, res) => {
    // The only input needed is the public key of the account.
    var pub_key = req.body.pub_key;
    // Get the balance.
    var balance = LeMoinCoin.balanceOf(pub_key);
    console.log("Balance was fetched of acc " + pub_key + ", it is " + balance);
    // Create a JSON object containing the balance as server response.
    var balance_json = {
      "balance": balance
    };
    // Respond with JSON object.
    res.send(balance_json);
  })


  // TRANSFER COINS
  app.post('/transfer', (req, res) => {

    console.log("Transfer Coins");
    const pri_key = req.body.pri_key;
    const send_to_add = req.body.send_to;
    const amount = req.body.amount;

    // Get the ethereum address (derived from the public key).
    var pub_add = eth_add_from_pk(pri_key);
    var n_transaction = 0;
    var this_n_transaction = web3.eth.getTransactionCount(pub_add);
    if (track_n_transaction == null) {
      n_transaction = this_n_transaction;
    } else if (track_n_transaction >= this_n_transaction) {
      // Case: Transactio number in chain the same as tracked number. This
      // means, the last transaction was not logged yet.
      n_transaction = track_n_transaction + 1;
    } else {
      n_transaction = this_n_transaction;
    }
    track_n_transaction = n_transaction;

    console.log(`Number of this transaction: ${n_transaction}`);

    // Get the nonce of the sender.

    var gas_price = LeMoinCoinContract.web3.eth.gasPrice;
    const gasPriceHex = web3.toHex(gas_price);
    const gasLimitHex = web3.toHex(100000);

    var raw_transaction = {
      //from: pub_key,
      to: LeMoinCoin.address,
      value: 0,
      gas: gasLimitHex,
      gasPrice: gasPriceHex,
      //nonce: web3.eth.getTransactionCount(pub_add),
      nonce: n_transaction,
      data: LeMoinCoin.transfer.getData(send_to_add, amount, {from: pub_add}),
    };

    var transaction = new Tx(raw_transaction);

    // console.log(transaction);
    var pri_key_buffer = new Buffer(pri_key, 'hex');
    transaction.sign(pri_key_buffer);

    var serializedTx = transaction.serialize();

    web3.eth.sendRawTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
        if (!err) {
            console.log(`Transaction will be send, the hash of the transaction is: ${hash}`);}
        else {
            console.log(err);}
    });
    var transfer_status_json = {
      "status": "Transaction complete"
    };
    res.send(transfer_status_json);
  })

}
