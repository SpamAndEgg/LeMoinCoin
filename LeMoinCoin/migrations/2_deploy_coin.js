var LeMoinCoin = artifacts.require("./LeMoinCoin.sol");

module.exports = function(deployer) {
  deployer.deploy(LeMoinCoin);
};
