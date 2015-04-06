<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="smbStyle.css">
    <script src="login.js"></script>
  </head>
  
  <body>
    <div class="banner">
      Simple Messege Board
    </div>
    <div class="loginBar">
    </div>
    
    <div id="loginPadding"></div>
    
    <div id="loginCenter">
      <fieldset id="createAcct">
        <legend>Create Account</legend>
        <div>
          <label>Username:</label>
          <input type="text" name="newUsername">
        </div>
        <div>
          <label>Password:</label>
          <input type="password" name="newPassword1">
        </div>
        <div>
          <label>Retype password:</label>
          <input type="password" name="newPassword2">
        </div>
        <div>
          <input type="button" value="Create Account" onclick="createAcct()">
        </div>
        <div id="errorMsg2"><br></div>
      </fieldset>
        
      <fieldset id="login">
        <legend>Login</legend>
        <div>
          <label>Username:</label>
          <input type="text" name="username">
        </div>
        <div>
          <label>Password:</label>
          <input type="password" name="password">
        </div>
        <div>
          <input type="button" value="Login" onclick="login()">
        </div>
        <div id="errorMsg1"><br></div>
      </fieldset>
    </div>
  </body>
</html>
