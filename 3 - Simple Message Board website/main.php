<?php
session_start();
?>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Main</title>
    <link rel="stylesheet" href="smbStyle.css">
    <script src="modifySmb.js"></script>
  </head>
  
  <body>
    <div class="banner">
      Simple Messege Board
    </div>
    <div class="loginBar">
      <?php
      if (isset($_SESSION['username'])) {
          echo 'Hi ' . "$_SESSION[username]" . '!';
          echo '&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;';
          echo '<a href="logout.php">logout</a>';
      }
      else {
          echo '<a href="login.php">Login / Create Account</a>';
      }
      ?>
      &nbsp;&nbsp;&nbsp;
    </div>
    <div class="navbar">
    </div>
    
    <div class="content">
      <?php
      include 'displayMessageBoard.php';
      
      if (isset($_SESSION['username'])) {
      ?>
      <div id="addedContent"></div>
      <div id="createMsg">
        <fieldset>
          <legend><h1>***Create A Post***</h1></legend>
          <div>
            <textarea placeholder="Message title" name="heading" rows="1" cols="88"></textarea>
          </div>
          <div>
            <textarea placeholder="Message body" name="body" rows="10" cols="88"></textarea>
          </div>
          <div>
            <input type="button" value="Create Post" onclick="createPost()">
            <div id="insertError"><br></div>
          </div>
        </fieldset>
      </div>
      <?php
      } else {
          echo '<p><strong><em>Login to create a post!</em></strong></p>';
      }
      ?>
      <br>
    </div>
  </body>
</html>
