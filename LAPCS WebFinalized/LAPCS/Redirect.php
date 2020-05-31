<?php
session_start();
$_SESSION["userName"]=$_POST["userName"];
$_SESSION["userEmail"]=$_POST["userEmail"];
$_SESSION["userPass"]=$_POST["userPass"];
$_SESSION["userID"]=$_POST["userID"];
echo "success";
?>