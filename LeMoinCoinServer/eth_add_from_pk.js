var EC = require('elliptic').ec;
var BN = require('bn.js');
var ec = new EC('secp256k1');
const keccak256 = require('js-sha3').keccak256;

module.exports = get_eth_add

function get_eth_add (privateKey) {
  // Generator point
  var G = ec.g;
  // EC multiplication to determine public point
  console.log("PK IS:");
  console.log(privateKey);
  var pubPoint=G.mul(privateKey);
  //32 bit x co-ordinate of public point
  var x = pubPoint.getX().toBuffer();
   //32 bit y co-ordinate of public point
  var y = pubPoint.getY().toBuffer();

  var publicKey =Buffer.concat([x,y])

  console.log("public key::"+publicKey.toString('hex'))
  // keccak256 hash of  publicKey
  const address = keccak256(publicKey)

  const buf2 = Buffer.from(address, 'hex');
  // take lat 20 bytes as ethereum adress
  var eth_add = "0x"+buf2.slice(-20).toString('hex');
  console.log("Ethereum Adress:::"+eth_add);
  return eth_add;
}
