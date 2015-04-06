<?php
ini_set('display_errors', 'On');
header("Content-Type: text/plain");

if (!isset($_POST['newUsername']) || !isset($_POST['newPassword1']) ||
    !isset($_POST['newPassword2'])) {
    header("Location: login.php");
} else if ($_POST['newUsername'] == NULL) {
    echo 'You must enter a username.';
} else if ($_POST['newPassword1'] == NULL) {
    echo 'You must enter a password.';
} else if ($_POST['newPassword1'] !== $_POST['newPassword2']) {
    echo 'The passwords you entered don\'t match';
} else {
    include 'storedInfo.php';
    
    $mysqli = new mysqli("oniddb.cws.oregonstate.edu", "hammarj-db",
                              $myPassword, "hammarj-db");
    if ($mysqli->connect_errno) {
        echo "Failed to connect to MySQL:(" . $mysqli->connect-errno .
             ") " . $mysqli->connect_error;
    }
    
    if (!($stmt = $mysqli->prepare("SELECT username FROM 
                                    Smb_accounts WHERE username=?"))) {
        echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
    }
    
    if (!$stmt->bind_param("s", $_POST['newUsername'])) {
        echo "Binding parameters failed: (" . $stmt->errno . ") " . $stmt->error;
    }
    
    if (!$stmt->execute()) {
        echo "Execute failed: (" . $mysqli->errno . ") " . $mysqli->error;
    }
    
    $outName = NULL;
    if (!$stmt->bind_result($outName)) {
        echo "Binding output parameters failed: (" . $stmt->errno . ") " 
              . $stmt->error;
    }
    
    if ($stmt->fetch()) {
        echo 'That username is already taken. Please choose another.';
    } else {
        $stmt->close();
        
        if (!($stmt = $mysqli->prepare("INSERT INTO Smb_accounts(username, 
                                        password) VALUES (?, ?)"))) {
            echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
        }
        
        $newUsername = $_POST['newUsername'];
        $newPassword1 = $_POST['newPassword1'];
        
        if (!$stmt->bind_param("ss", $newUsername, $newPassword1)) {
            echo "Binding parameters failed: (" . $stmt->errno . ") "
                 . $stmt->error;
        }
        
        if (!$stmt->execute()) {
            echo "Execute failed: (" . $mysqli->errno . ") " . $mysqli->error;
        }
        
        session_start();
        $_SESSION['username'] = $_POST['newUsername'];
        echo 'Account Creation Successful';
    }
    
    $stmt->close();
}
?>