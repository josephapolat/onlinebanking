const API_BASE = 'http://localhost:8080/api/online-banking';
let jwt;
var userName;
var userNameTest;
let accNum;
var accNum1;
var accNum2;
var accNum3;
var accNum4;
var primarySigner;
var secondarySigner;

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('login-button').addEventListener('click', login);


});

function login(){
    userName = document.getElementById('username-box').value;
    let password = document.getElementById('password-box').value;
    localStorage.setItem("storageName",userName);
    if(userName != undefined && password != undefined){
        fetch(API_BASE + '/login', {
            method: 'POST',
            cache: 'no-cache',
            headers: {
               'Content-Type': 'application/json'
           },
           body: JSON.stringify({username: userName, password: password})
       })
           .then((response) => {
               if( response.ok ) {
                   response.json().then((responseDecode) => {
                        jwt = responseDecode.token;

                        window.location.replace("/final-project/module-2/week-9/final-project/src/main/resources/static/user.html");
                   });



               }
               else{
                alert('Login no good!');
               }
           })
           .catch((err) => {
               console.error(err);
               alert('Could not login!');
           });
        }
    }
    function loadUserData(){
        var userName = localStorage.getItem("storageName");
        document.getElementById("transfer-background").style.visibility = 'hidden';
                document.getElementById("transfer-container").style.visibility = 'hidden';
                document.getElementById("from-account-dropdown").style.visibility = 'hidden';
                document.getElementById("to-account-dropdown").style.visibility = 'hidden';
        fetch(API_BASE + '/accounts', {
                    method: 'POST',
                    cache: 'no-cache',
                    headers: {
                       'Content-Type': 'application/json'
                   },
                   body: JSON.stringify({username: userName})
               })
                   .then((response) => {

                           response.json().then((responseDecode) => {
                                for (let i = 0; i < responseDecode.length; i++) {

                                         document.getElementById("account" + i).className = "account-box";
                                         localStorage.setItem("accountNum" + i,responseDecode[i].accountNumber);
                                         localStorage.setItem("acc" + i,responseDecode[i].accountNumber);
                                         localStorage.setItem("primSignAcc" + i, responseDecode[i].primarySigner);
                                         localStorage.setItem("secSignAcc" + i, responseDecode[i].secondarySigner);
                                         if(responseDecode[i].accountNickname !== null ){

                                                document.getElementById("accTit" + i).innerHTML = responseDecode[i].accountNickname;


                                         }else{
                                            document.getElementById("accTit" + i).innerHTML = "xxxx" + responseDecode[i].accountNumber.slice(-4);
                                         }
                                         document.getElementById("viewDetBut" + i).style.visibility = 'visible';
                                         document.getElementById("main-balance" + i).innerHTML = "$" + responseDecode[i].balance;
                                         document.getElementById("from-account-dropdown").options[i].innerHTML = "xxxx" + responseDecode[i].accountNumber.slice(-4) + " - $"+ responseDecode[i].balance;
                                         document.getElementById("to-account-dropdown").options[i].innerHTML = "xxxx" + responseDecode[i].accountNumber.slice(-4) + " - $"+ responseDecode[i].balance;
                                    }
                                //accNum = responseDecode[0].balance;


                           });




                   })


    }
    function openTransferWindow(){

        document.getElementById("transfer-background").style.visibility = 'visible';
        document.getElementById("transfer-container").style.visibility = 'visible';
        document.getElementById("from-account-dropdown").style.visibility = 'visible';
        document.getElementById("to-account-dropdown").style.visibility = 'visible';
        document.getElementById("cancel-button").style.visibility = 'visible';
        document.getElementById("submit-button").style.visibility = 'visible';
        document.getElementById("transfer-from-text").style.visibility = 'visible';
        document.getElementById("transfer-to-text").style.visibility = 'visible';
        document.getElementById("amount-text").style.visibility = 'visible';
        document.getElementById("amount-to-transfer-input").style.visibility = 'visible';


    }
    function transfer(){
        var fromAccount = document.getElementById("from-account-dropdown").value;
                var toAccount = document.getElementById("to-account-dropdown").value;
                var amount = document.getElementById("amount-to-transfer-input").value;

                fetch(API_BASE + '/transfer', {
                                    method: 'PUT',
                                    cache: 'no-cache',
                                    headers: {
                                       'Content-Type': 'application/json'
                                   },
                                   body: JSON.stringify({fromAccount: localStorage.getItem(fromAccount), toAccount: localStorage.getItem(toAccount), transferAmount: amount}),
                               })




                closeTransferWindow();
    }
    function closeTransferWindow(){
        document.getElementById("transfer-background").style.visibility = 'hidden';
        document.getElementById("transfer-container").style.visibility = 'hidden';
        document.getElementById("from-account-dropdown").style.visibility = 'hidden';
        document.getElementById("to-account-dropdown").style.visibility = 'hidden';
        document.getElementById("cancel-button").style.visibility = 'hidden';
        document.getElementById("submit-button").style.visibility = 'hidden';
        document.getElementById("transfer-from-text").style.visibility = 'hidden';
        document.getElementById("transfer-to-text").style.visibility = 'hidden';
        document.getElementById("amount-text").style.visibility = 'hidden';
        document.getElementById("amount-to-transfer-input").style.visibility = 'hidden';
        window.location.reload();
    }
function viewAccountDetails0(){
document.getElementById("close-account-button").style.visibility = 'visible';
        document.getElementById("edit-button").style.visibility = 'visible';
    document.getElementById("transfer-background").style.visibility = 'visible';
    document.getElementById("transfer-container").style.visibility = 'visible';
    document.getElementById("close-button").style.visibility = 'visible';
    document.getElementById("accNumLabel").style.visibility = 'visible';
    document.getElementById("primSignLabel").style.visibility = 'visible';
    document.getElementById("secSignLabel").style.visibility = 'visible';
    document.getElementById("primSign").style.visibility = 'visible';
    document.getElementById("secSign").style.visibility = 'visible';
    document.getElementById("accNick").style.visibility = 'visible';
    document.getElementById("cancel-button").style.visibility = 'hidden';
    document.getElementById("submit-button").style.visibility = 'hidden';
    document.getElementById("transfer-from-text").style.visibility = 'hidden';
    document.getElementById("transfer-to-text").style.visibility = 'hidden';
    document.getElementById("amount-text").style.visibility = 'hidden';
    document.getElementById("amount-to-transfer-input").style.visibility = 'hidden';

    document.getElementById("accNum0").style.visibility = 'visible';
    document.getElementById("accNum0").innerHTML = localStorage.getItem("accountNum0");
    var signerTwo;
    if(localStorage.getItem("secSignAcc0") === null){
        signerTwo = null;
    }else{
        signerTwo = localStorage.getItem("secSignAcc0")
    }
    fetch(API_BASE + '/account-details', {
                        method: 'POST',
                        cache: 'no-cache',
                        headers: {
                           'Content-Type': 'application/json'
                       },
                       body: JSON.stringify({primarySigner: localStorage.getItem("primSignAcc0"), secondarySigner: signerTwo, accountNumber: localStorage.getItem("accountNum0")})
                   })
                       .then((response) => {

                               response.json().then((responseDecode) => {

                                    document.getElementById("primSign").innerHTML = responseDecode.primarySigner;
                                    if(!responseDecode.hasOwnProperty('secondarySigner')){
                                        document.getElementById("secSign").innerHTML = "None";
                                        if(responseDecode.accountNickname === null){

                                            document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum0");
                                        }else{
                                            document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                        }
                                    }else{
                                        document.getElementById("secSign").innerHTML = responseDecode.secondarySigner;
                                        if(responseDecode.accountNickname === null || responseDecode.accountNickname === ''){

                                            document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum0");
                                        }else{
                                            document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                        }
                                    }




                        });
    })
}
function viewAccountDetails1(){
    document.getElementById("close-account-button").style.visibility = 'visible';
            document.getElementById("edit-button").style.visibility = 'visible';
        document.getElementById("transfer-background").style.visibility = 'visible';
        document.getElementById("transfer-container").style.visibility = 'visible';
        document.getElementById("close-button").style.visibility = 'visible';
        document.getElementById("accNumLabel").style.visibility = 'visible';
        document.getElementById("primSignLabel").style.visibility = 'visible';
        document.getElementById("secSignLabel").style.visibility = 'visible';
        document.getElementById("primSign").style.visibility = 'visible';
                document.getElementById("secSign").style.visibility = 'visible';
                document.getElementById("accNick").style.visibility = 'visible';
        document.getElementById("cancel-button").style.visibility = 'hidden';
        document.getElementById("submit-button").style.visibility = 'hidden';
        document.getElementById("transfer-from-text").style.visibility = 'hidden';
        document.getElementById("transfer-to-text").style.visibility = 'hidden';
        document.getElementById("amount-text").style.visibility = 'hidden';
        document.getElementById("amount-to-transfer-input").style.visibility = 'hidden';

        document.getElementById("accNum1").style.visibility = 'visible';
        document.getElementById("accNum1").innerHTML = localStorage.getItem("accountNum1");

        var signerTwo;
        if(localStorage.getItem("secSignAcc1") === null){
            signerTwo = null;
        }else{
            signerTwo = localStorage.getItem("secSignAcc1")
        }
        signerTwo = null;
        fetch(API_BASE + '/account-details', {
                                    method: 'POST',
                                    cache: 'no-cache',
                                    headers: {
                                       'Content-Type': 'application/json'
                                   },
                                   body: JSON.stringify({primarySigner: localStorage.getItem("primSignAcc1"), secondarySigner: signerTwo, accountNumber: localStorage.getItem("accountNum1")})
                               })
                                   .then((response) => {

                                           response.json().then((responseDecode) => {

                                                document.getElementById("primSign").innerHTML = responseDecode.primarySigner;
                                                if(!responseDecode.hasOwnProperty('secondarySigner')){
                                                    document.getElementById("secSign").innerHTML = "None";
                                                    if(responseDecode.accountNickname === null || responseDecode.accountNickname === ''){

                                                        document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum1");
                                                    }else{
                                                        document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                                    }
                                                }else{
                                                    document.getElementById("secSign").innerHTML = responseDecode.secondarySigner;
                                                    if(responseDecode.accountNickname === null){

                                                        document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum1");
                                                    }else{
                                                        document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                                    }
                                                }



                                    });
                })


    }
function viewAccountDetails2(){
        document.getElementById("close-account-button").style.visibility = 'visible';
        document.getElementById("edit-button").style.visibility = 'visible';
        document.getElementById("transfer-background").style.visibility = 'visible';
        document.getElementById("transfer-container").style.visibility = 'visible';
        document.getElementById("close-button").style.visibility = 'visible';
        document.getElementById("accNumLabel").style.visibility = 'visible';
        document.getElementById("primSignLabel").style.visibility = 'visible';
        document.getElementById("secSignLabel").style.visibility = 'visible';
        document.getElementById("cancel-button").style.visibility = 'hidden';
        document.getElementById("submit-button").style.visibility = 'hidden';
        document.getElementById("transfer-from-text").style.visibility = 'hidden';
        document.getElementById("transfer-to-text").style.visibility = 'hidden';
        document.getElementById("amount-text").style.visibility = 'hidden';
        document.getElementById("amount-to-transfer-input").style.visibility = 'hidden';

        document.getElementById("accNum2").style.visibility = 'visible';
        document.getElementById("accNum2").innerHTML = localStorage.getItem("accountNum2");

        var signerTwo;
            if(localStorage.getItem("secSignAcc2") === null){
                signerTwo = null;
            }else{
                signerTwo = localStorage.getItem("secSignAcc2")
            }
            signerTwo = null;
        fetch(API_BASE + '/account-details', {
                                                method: 'POST',
                                                cache: 'no-cache',
                                                headers: {
                                                   'Content-Type': 'application/json'
                                               },
                                               body: JSON.stringify({primarySigner: localStorage.getItem("primSignAcc2"), secondarySigner: signerTwo, accountNumber: localStorage.getItem("accountNum2")})
                                           })
                                               .then((response) => {

                                                       response.json().then((responseDecode) => {
                                                            document.getElementById("primSign").innerHTML = responseDecode.primarySigner;
                                                            if(!responseDecode.hasOwnProperty('secondarySigner')){
                                                                document.getElementById("secSign").innerHTML = "None";
                                                            }else{
                                                                document.getElementById("secSign").innerHTML = responseDecode.secondarySigner;
                                                            }

                                                            if(responseDecode.accountNickname === NULL){

                                                                document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum2");
                                                            }else{
                                                                document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                                            }

                                                });
                            })



}
function viewAccountDetails3(){
        document.getElementById("close-account-button").style.visibility = 'visible';
                document.getElementById("edit-button").style.visibility = 'visible';
        document.getElementById("transfer-background").style.visibility = 'visible';
        document.getElementById("transfer-container").style.visibility = 'visible';
        document.getElementById("close-button").style.visibility = 'visible';
        document.getElementById("accNumLabel").style.visibility = 'visible';
        document.getElementById("primSignLabel").style.visibility = 'visible';
        document.getElementById("secSignLabel").style.visibility = 'visible';
        document.getElementById("cancel-button").style.visibility = 'hidden';
        document.getElementById("submit-button").style.visibility = 'hidden';
        document.getElementById("transfer-from-text").style.visibility = 'hidden';
        document.getElementById("transfer-to-text").style.visibility = 'hidden';
        document.getElementById("amount-text").style.visibility = 'hidden';
        document.getElementById("amount-to-transfer-input").style.visibility = 'hidden';

        document.getElementById("accNum3").style.visibility = 'visible';
        document.getElementById("accNum3").innerHTML = localStorage.getItem("accountNum3");
var signerTwo;
            if(localStorage.getItem("secSignAcc3") === null){
                signerTwo = null;
            }else{
                signerTwo = localStorage.getItem("secSignAcc3")
            }
            signerTwo = null;
        fetch(API_BASE + '/account-details', {
                                                method: 'POST',
                                                cache: 'no-cache',
                                                headers: {
                                                   'Content-Type': 'application/json'
                                               },
                                               body: JSON.stringify({primarySigner: localStorage.getItem("primSignAcc3"), secondarySigner: signerTwo, accountNumber: localStorage.getItem("accountNum3")})
                                           })
                                               .then((response) => {

                                                       response.json().then((responseDecode) => {
                                                            document.getElementById("primSign").innerHTML = responseDecode.primarySigner;
                                                            if(!responseDecode.hasOwnProperty('secondarySigner')){
                                                                document.getElementById("secSign").innerHTML = "None";
                                                            }else{
                                                                document.getElementById("secSign").innerHTML = responseDecode.secondarySigner;
                                                            }

                                                            if(!responseDecode.accountNickname){

                                                                document.getElementById("accNick").innerHTML = localStorage.getItem("accountNum3");
                                                            }else{
                                                                document.getElementById("accNick").innerHTML = responseDecode.accountNickname;
                                                            }

                                                });
                            })

}
function closeDetailsWindow(){
        document.getElementById("transfer-background").style.visibility = 'hidden';
        document.getElementById("transfer-container").style.visibility = 'hidden';
        document.getElementById("close-button").style.visibility = 'hidden';
        document.getElementById("accNumLabel").style.visibility = 'hidden';
        document.getElementById("primSignLabel").style.visibility = 'hidden';
        document.getElementById("secSignLabel").style.visibility = 'hidden';
        document.getElementById("accNum0").style.visibility = 'hidden';
        document.getElementById("accNum1").style.visibility = 'hidden';
        document.getElementById("accNum2").style.visibility = 'hidden';
        document.getElementById("accNum3").style.visibility = 'hidden';
        document.getElementById("primSign").style.visibility = 'hidden';
        document.getElementById("secSign").style.visibility = 'hidden';
        document.getElementById("accNick").style.visibility = 'hidden';
        document.getElementById("submit-nickname-button").style.visibility = 'hidden';
        document.getElementById("account-nickname-input").style.visibility = 'hidden';
        document.getElementById("close-account-button").style.visibility = 'hidden';
        document.getElementById("edit-button").style.visibility = 'hidden';
}
function editAccountNickname(){
    document.getElementById("submit-nickname-button").style.visibility = 'visible';
            document.getElementById("account-nickname-input").style.visibility = 'visible';
}
function submitNewNickname(){
    var nickname = document.getElementById("account-nickname-input").value;
    var accNum;
    if(window.getComputedStyle(document.getElementById("accNum0")).visibility === 'visible'){

        accNum = document.getElementById("accNum0").innerHTML;

    }else if(window.getComputedStyle(document.getElementById("accNum1")).visibility === 'visible'){

        accNum = document.getElementById("accNum1").innerHTML;

    }else if(window.getComputedStyle(document.getElementById("accNum2")).visibility === 'visible'){

        accNum = document.getElementById("accNum2").innerHTML;

    }else{

        accNum = document.getElementById("accNum3").innerHTML;


    }

    fetch(API_BASE + '/submit-nickname', {
                                        method: 'PUT',
                                        cache: 'no-cache',
                                        headers: {
                                           'Content-Type': 'application/json'
                                       },
                                       body: JSON.stringify({accountNumber: accNum, nickName: nickname}),
                                   })
    window.location.reload();
}
function closeAccountConfirm(){
    if(confirm("Are you sure you want to close this account?") == true){
        closeAccount();
    }else{

    }
}
function closeAccount(){
    var accNum;
    if(window.getComputedStyle(document.getElementById("accNum0")).visibility === 'visible'){

        accNum = document.getElementById("accNum0").innerHTML;

    }else if(window.getComputedStyle(document.getElementById("accNum1")).visibility === 'visible'){

        accNum = document.getElementById("accNum1").innerHTML;

    }else if(window.getComputedStyle(document.getElementById("accNum2")).visibility === 'visible'){

        accNum = document.getElementById("accNum2").innerHTML;

    }else{

        accNum = document.getElementById("accNum3").innerHTML;


    }
    fetch(API_BASE, {
                                             method: 'DELETE',
                                             cache: 'no-cache',
                                             headers: {
                                                'Content-Type': 'application/json'
                                            },
                                            body: JSON.stringify({accountNumber: accNum}),
                                        })

    window.location.reload();
}
