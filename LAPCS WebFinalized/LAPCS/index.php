<?php
session_start();
if ( !isset($_SESSION["userEmail"]))
  header( "Location: login.html" );
  else{
    $Email=$_SESSION["userEmail"];
    $Password=$_SESSION["userPass"];
    $UserName=$_SESSION["userName"];
    $UserID=$_SESSION["userID"];
  }
?>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta name="description" content="" />
        <meta name="author" content="" />
        <title>Parent Dashboard</title>
        <style>
            .breadcrumb {
                margin-top: 25px !important;
            }
         </style>

        <link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>
        <link href="css/styles.css" rel="stylesheet" />
        <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-app.js"></script>
        <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-database.js"></script>
        <script src="js/loadFirebase.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js" crossorigin="anonymous"></script>
    </head>
    <body class="sb-nav-fixed">
        <nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
            <a class="navbar-brand" > <img src="logo.jpeg" alt="lapcs logo" class="align-self-start rounded-circle" style="width:40px;">
            </a> 
            <a class="navbar-brand" href="index.php">LAPCS</a>
            
            <button class="btn btn-link order-1 order-lg-1" id="sidebarToggle" href="#"><i class="fas fa-bars"></i></button> 
          
            <ul class="navbar-nav ml-auto mr-0 mr-md-3 my-2 my-md-0">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" id="userDropdown" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                       
                        <a class="dropdown-item" href="#" onclick="logout()">Logout</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="privacyp.html" >Privacy Policy</a>
                        <a class="dropdown-item" href="tc.html" >Terms & Conditions</a>
                    </div>
                </li>
            </ul>
        </nav>
        <div id="layoutSidenav">
            <div id="layoutSidenav_nav">
                <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
                    <div class="sb-sidenav-menu">
                        <div class="nav">
                            <a class="nav-link" href="#"
                                ><div class="sb-nav-link-icon" style="color: #909294;"><i class="fas fa-user"></i></div>
                                <b id="userName" style="color: #909294;">User</b></a>
                                <li class="nav-item dropdown" >
                                    <a style="color: #909294;" class="nav-link dropdown-toggle" id="userMenu" role="button" href="#" data-toggle="dropdown" aria-haspopup="true"
                                        aria-expanded="false">
                                        <i class="fas fa-mobile"></i>
                                        <span id="SelectedDevice" style="padding-left:10px;">Select Device</span>
                                    </a>
                                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu" id="mobiles">
                                    </div>
                                 </li>
                            <div class="sb-sidenav-menu-heading">Monitoring</div>
                            <a class="nav-link" href="Messages.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-sms"></i></div>
                                Messages</a>
                                <a class="nav-link" href="Calls.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-phone"></i></div>
                                Calls</a>
                                <a class="nav-link" href="contacts.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-address-card"></i></div>
                                Contacts</a>

                                <a class="nav-link" href="SocialApp.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-user"></i></div>
                                Social Apps</a>
                            <a class="nav-link" href="SocialAppUsageStats.php"
                            ><div class="sb-nav-link-icon"><i class="fas fa-clock"></i></div>
                                Social Apps Usage</a>
                                <a class="nav-link" href="Photo.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-camera"></i></div>
                                Photos</a>
                                <a class="nav-link" href="Location.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-map-marker"></i></div>
                                Location</a>
                            <div class="sb-sidenav-menu-heading">Configurations</div>
                            <a class="nav-link" href="Screentime.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-clock"></i></div>
                                Limit Screen Time</a>
                            <a class="nav-link" href="ContactsWatchList.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-lock"></i></div>
                                Add to Watchlist</a>
                            <a class="nav-link" href="Location.php"
                                ><div class="sb-nav-link-icon"><i class="fas fa-car"></i></div>
                                Proximity Alert</a>
                        </div>
                    </div>
                </nav>
            </div>
            <div id="layoutSidenav_content">
                <main>
                    <div class="container-fluid">

                        <ol class="breadcrumb mb-4">
                            <li class="breadcrumb-item active">Location Aware Parenting Control System</li>
                        </ol>
                        <div class="row">
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-primary text-white mb-4">
                                    <div class="card-body" class="icon"><i class="fas fa-sms"></i> Messages</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <a class="small text-white stretched-link" href="Messages.php">View Details</a>
                                        <div class="small text-white"><i class="fas fa-angle-right"></i></div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-warning text-white mb-4">
                                    <div class="card-body" class="icon"><i class="fas fa-phone"></i> Calls</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <a class="small text-white stretched-link" href="Calls.php">View Details</a>
                                        <div class="small text-white"><i class="fas fa-angle-right"></i></div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-success text-white mb-4">
                                    <div class="card-body" class="icon"><i class="fas fa-address-card"></i> Contacts</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <a class="small text-white stretched-link" href="contacts.php">View Details</a>
                                        <div class="small text-white"><i class="fas fa-angle-right"></i></div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-danger text-white mb-4">
                                    <div class="card-body" class="icon"><i class="fas fa-camera"></i> Gallery</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <a class="small text-white stretched-link" href="Photo.php">View Details</a>
                                        <div class="small text-white"><i class="fas fa-angle-right"></i></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                       
                    </div>
                </main>
                <footer class="py-4 bg-light mt-auto">
                    <div class="container-fluid">
                        <div class="d-flex align-items-center justify-content-between small">
                            <div class="text-muted"><b>Copyright &copy; 2019-2020. Final Year Project By Fatima & Farwa. </b><i>All Rights Reserved.</i></div>
                            <div>
                                <a href="privacyp.html">Privacy Policy</a>
                                &middot;
                                <a href="tc.html">Terms &amp; Conditions</a>
                            </div>
                        </div>
                    </div>
                </footer>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
        <script src="js/scripts.js"></script>
        <script>
            function logout() {
      $.post("endSession.php", function (data) {

        if (data == "success")
          window.location.href = "login.html";
        else
          window.alert("Try Again");
      });
    }
    localStorage.setItem("SelectedDevice", "NA");
    var email=<?php echo json_encode($Email)?>;
    var pass=<?php echo json_encode($Password)?>;
    var userName=<?php echo json_encode($UserName)?>;
    document.getElementById("userName").innerHTML =userName;
    var userID = "<?php echo $UserID?>";
    var userHaveMobile = 0;
    var listOfMobiles = [];
            var database = firebase.database();
            //  var selectedDeviceIs =parseInt(document.cookie.charAt(0));
            var selectedDeviceIs=-1;

            var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID);

            SelectedDeviceRef.once('value').then(function (data) {
                // alert("UserDevices val from db --> "+data.val());
                selectedDeviceIs =parseInt(data.val());
            });

    var listOfMobilesNames = [];
  //  var database = firebase.database();
    //loading current moblie 
   // var ref = database.ref('/Users/' + userID);
            var ref = database.ref('/ChildMetaData/' + userID);
    ref.on('value', function (data) {
      var details = data.val();
      var a = 0;
      var keys = Object.keys(details);
      if(keys.length>0)
        userHaveMobile = 1;
      if (userHaveMobile == 0) {
        window.alert("No Mobile");
      }
      else {
        listOfMobiles = []
        listOfMobilesNames = [];
        document.getElementById("mobiles").innerHTML = "";
        var getOldData = document.getElementById("mobiles").innerHTML;
        for (var i = 0; i < keys.length; i++) {
            listOfMobiles.push(keys[i]);
            var ref = database.ref('/Mobiles/' + keys[i]);
            ref.on('value', function (data) {
              var details1 = data.val();
              var nameMobile = "";
              nameMobile = nameMobile + details1 + " ";
              listOfMobilesNames.push(nameMobile);
              document.getElementById("mobiles").innerHTML = getOldData + "<a class='dropdown-item' href='#'  onclick='setCurrentMobile(" + a + ")'>" +
                "<i class='fas fa-mobile'></i>&nbsp&nbsp" + listOfMobilesNames[a] + "</a>";
              getOldData = document.getElementById("mobiles").innerHTML;
              a++;

              if(listOfMobilesNames.length != 0 && listOfMobilesNames.length>=selectedDeviceIs+1){

setCurrentMobile(selectedDeviceIs);
}
            });
        }
      }
    });

    function setCurrentMobile(data) {


      //document.getElementById("SelectedDevice").innerHTML = listOfMobilesNames[data];
     // document.cookie = data;
        var database = firebase.database();

        if(data != -1)
        {
            document.getElementById("SelectedDevice").innerHTML = listOfMobilesNames[data];
        }

        //document.cookie = data;
        var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set(data);
      //storing IMEI in selected device
      localStorage.setItem("SelectedDevice", listOfMobiles[data]);

        var triggerRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger');
        triggerRef.on('value', function (data) {
            var details = data.val();
            //alert(details);
            triggerRefSplit = details.split("/");
            // for (var i = 0; i < triggerRefSplit.length; i++)
            // {
            //     alert(triggerRefSplit[i]);
            // }
            if(triggerRefSplit[triggerRefSplit.length-1]=== "remove")
            {
                if(listOfMobiles.length>0)

                {
                    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set("0");      //setting 0 for next reload
                }
                else
                {
                    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set("-1");      //setting -1 because last device is removed and no more devices left
                }

                //alert(triggerRefSplit[triggerRefSplit.length-1]);
                window.location.reload(false);
            }

        });
    
    }

        </script>
    </body>
</html>
