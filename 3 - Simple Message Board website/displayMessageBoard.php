<?php
include 'storedInfo.php';

$mysqli = new mysqli("oniddb.cws.oregonstate.edu", "hammarj-db",
                      $myPassword, "hammarj-db");
if ($mysqli->connect_errno) {
    echo "Failed to connect to MySQL:(" . $mysqli->connect-errno .
         ") " . $mysqli->connect_error;
}

if (!($stmt = $mysqli->prepare("SELECT heading, body, author FROM 
                                Smb_messageBoard"))) {
    echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
}

if (!$stmt->execute()) {
    echo "Execute failed: (" . $mysqli->errno . ") " . $mysqli->error;
}

$outHeading = NULL;
$outBody = NULL;
$outAuthor = NULL;
if (!$stmt->bind_result($outHeading, $outBody, $outAuthor)) {
    echo "Binding output parameters failed: (" . $stmt->errno . ") " 
          . $stmt->error;
}

echo '<br>';

while ($stmt->fetch()) {
    echo '<fieldset>';
    echo '<legend><h1>' . "$outHeading" . '</h1></legend>';
    echo '<p>' . "$outBody" . '</p>';
    echo '<p>Posted by <strong>' . "$outAuthor" . '</strong></p>';
    /*echo '<input type="button" value="Edit"';
    if (!isset($_SESSION['username']) || $_SESSION['username'] !== $outAuthor) {
        echo 'disabled';
    }*/
    echo '</fieldset><br>';
}

$stmt->close();


?>