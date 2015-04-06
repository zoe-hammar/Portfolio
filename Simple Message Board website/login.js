function login() {
  var req = new XMLHttpRequest();
  if (!req) {
    throw 'Unable to create HttpRequest.';
  }
  
  var username = document.getElementsByName('username')[0].value;
  var password = document.getElementsByName('password')[0].value;
  
  var params = [];
  params['username'] = username;
  params['password'] = password;
  
  var encodedParams = [];
  for (var prop in params) {
    var pair = encodeURIComponent(prop) +
              '=' + encodeURIComponent(params[prop]);
    encodedParams.push(pair);
  }

  req.onreadystatechange = function() {
    if (req.readyState === 4 && req.status === 200) {
      if (req.responseText == 'Login Successful') {
        window.location.href = "main.php";
      } else {
        document.getElementById('errorMsg1').innerHTML = req.responseText;
      }
    }
  };

  req.open('POST', 'authenticate.php');
  req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  req.send(encodedParams.join('&'));
}

function createAcct() {
  var req = new XMLHttpRequest();
  if (!req) {
    throw 'Unable to create HttpRequest.';
  }
  
  var newUsername = document.getElementsByName('newUsername')[0].value;
  var newPassword1 = document.getElementsByName('newPassword1')[0].value;
  var newPassword2 = document.getElementsByName('newPassword2')[0].value;
  
  var params = [];
  params['newUsername'] = newUsername;
  params['newPassword1'] = newPassword1;
  params['newPassword2'] = newPassword2;
  
  var encodedParams = [];
  for (var prop in params) {
    var pair = encodeURIComponent(prop) +
              '=' + encodeURIComponent(params[prop]);
    encodedParams.push(pair);
  }

  req.onreadystatechange = function() {
    if (req.readyState === 4 && req.status === 200) {
      if (req.responseText == 'Account Creation Successful') {
        window.location.href = "main.php";
      } else {
        document.getElementById('errorMsg2').innerHTML = req.responseText;
      }
    }
  };

  req.open('POST', 'createAcct.php');
  req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  req.send(encodedParams.join('&'));
}