var crazy_coin = artifacts.require("./crazy_coin.sol");

module.exports = function(deployer) {
  deployer.deploy(crazy_coin);
};
