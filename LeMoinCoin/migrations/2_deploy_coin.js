var crazy_coin = artifacts.require("./LeMoinCoin.sol");

module.exports = function(deployer) {
  deployer.deploy(crazy_coin);
};
