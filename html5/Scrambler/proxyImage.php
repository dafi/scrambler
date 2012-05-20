<?php

$url = $_GET['imageUrl'];
echo file_get_contents($url);
?>