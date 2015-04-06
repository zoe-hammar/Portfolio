<?php
ini_set('display_errors', 'On');
header("Content-Type: text/plain");
if (!isset($_POST['heading']) || !isset($_POST['body'])) {
    header("Location: main.php");
} else if ($_POST['heading'] == NULL) {
    echo 'You must enter a message heading.';
    //echo '{"insert":"fail","message":"You must enter a message heading."}';
} else if ($_POST['body'] == NULL) {
    echo 'You must enter a message body.';
    //echo '{"insert":"fail","message":"You must enter a message body."}';
} else {
    session_start();
    
    include 'storedInfo.php';
    
    $mysqli = new mysqli("oniddb.cws.oregonstate.edu", "hammarj-db",
                              $myPassword, "hammarj-db");
    if ($mysqli->connect_errno) {
        echo "Failed to connect to MySQL:(" . $mysqli->connect-errno .
             ") " . $mysqli->connect_error;
    }
        
    if (!($stmt = $mysqli->prepare("INSERT INTO Smb_messageBoard(heading, 
                                    body, author) VALUES (?, ?, ?)"))) {
        echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
    }
    
    $heading = $_POST['heading'];
    $body = $_POST['body'];
    $username = $_SESSION['username'];
    
    if (!$stmt->bind_param("sss", $heading, $body, $username)) {
        echo "Binding parameters failed: (" . $stmt->errno . ") "
             . $stmt->error;
    }
    
    if (!$stmt->execute()) {
        echo "Execute failed: (" . $mysqli->errno . ") " . $mysqli->error;
    }
    
    echo 'success';
    
    //echo '{"insert":"success","author":"' . "$author" . '"}';
    
    $stmt->close();
}
?>