pragma solidity ^0.4.0;

contract LeMoinCoin {

    uint total_supply;

    mapping (address => uint) account;

    address creator;

    function LeMoinCoin() public {
        creator = msg.sender;
        total_supply = 1000;
        account[msg.sender] = total_supply;

    }

    event new_balance(uint balance);

    function this_add() public view returns (address) {
        return msg.sender;
    }


    function transfer (address receiver, uint amount) public returns (bool){
        if (account[msg.sender] >= amount) {
            account[msg.sender] -= amount;
            account[receiver] += amount;
            // Trigger event to update the account balance field in JavaScript.
            new_balance(account[msg.sender]);
            return true;
        } else {
            return false;
        }

    }

    function show_me_my_balance() public view returns (uint) {
        return account[msg.sender];
    }

    function test_output() public pure returns (uint) {
        return 500;
    }

    function totalSupply() public constant returns (uint) {
        return total_supply;
    }


    function balanceOf(address tokenOwner) public constant returns (uint balance) {
        return account[tokenOwner];
    }


    //function allowance(address tokenOwner, address spender) public constant returns (uint remaining);

    //function approve(address spender, uint tokens) public returns (bool success);
    //function transferFrom(address from, address to, uint tokens) public returns (bool success);


}
