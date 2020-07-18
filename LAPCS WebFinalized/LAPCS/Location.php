<?php
session_start();
if ( !isset($_SESSION["userEmail"]))
    header( "Location: login.php" );
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
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <title>Parent Dashboard</title>
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>
    <link href="css/styles.css" rel="stylesheet" />
    <link href="https://cdn.datatables.net/1.10.20/css/dataTables.bootstrap4.min.css" rel="stylesheet" crossorigin="anonymous" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js" crossorigin="anonymous"></script>
    <link href="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/css/bootstrap4-toggle.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.1/css/toastr.css" rel="stylesheet"/>



    <script async defer src="https://maps.googleapis.com/maps/api/js?key=zaSyB9di4Di943uRAHaU1ZKkmXPXNe-hA_YGM&libraries=places"
            type="text/javascript"></script>
    <!-- RSVP -->
    <script src="https://cdn.jsdelivr.net/npm/rsvp@4/dist/rsvp.min.js"></script>
    <!-- Firebase -->
    <script src="https://cdn.firebase.com/js/client/1.0.17/firebase.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-app.js"></script>
    <!-- Firebase Database-->
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-database.js"></script>
    <!-- GeoFire -->
    <script src="https://cdn.firebase.com/libs/geofire/2.0.0/geofire.min.js"></script>

    <script src="js/loadFirebase.js"></script>

    <style>
        .pac-card {
            margin: 10px 10px 0 0;
            border-radius: 2px 0 0 2px;
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            outline: none;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
            background-color: #fff;
            font-family: Roboto;
        }

        #pac-container {
            padding-bottom: 12px;
            margin-right: 12px;
        }

        .pac-controls {
            display: inline-block;
            padding: 5px 11px;
        }

        .pac-controls label {
            font-family: Roboto;
            font-size: 13px;
            font-weight: 300;
        }

        #pac-input {
            background-color: #fff;
            font-family: Roboto;
            font-size: 15px;
            font-weight: 300;
            margin-left: 500px;
            padding: 0 11px 0 13px;
            text-overflow: ellipsis;
            width: 400px;
            height: 30px;
            border-radius: 5px;
        }

        #pac-input:focus {
            border-color: #4d90fe;
        }

        #title {
            color: #fff;
            background-color: #4d90fe;
            font-size: 25px;
            font-weight: 500;
            padding: 6px 12px;
        }
        #target {
            width: 345px;
        }

         .breadcrumb {
             margin-top: 25px !important;
         }

    </style>

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
                    <li class="breadcrumb-item"><a href="index.php">Dashboard</a></li>
                    <li class="breadcrumb-item active">Location</li>
                </ol>
                <div class="row">
                    <div class='col-md-12'>
                        <div class="form-check form-check-inline">
                            <label for="trackingToggle" class="form-check-label"> <h3 >Tracking</h3></label>&nbsp;&nbsp;&nbsp;
                            <input id="trackingToggle" class="form-check-label" type="checkbox" data-toggle="toggle" data-style="mr-1" data-onstyle="success" data-offstyle="danger" checked>


                        <input id="pac-input" class="controls" type="text" placeholder="Search Box" >
                        </div>
                        <br>
                        <br>
                        <div id="googleMap" style="width:100%;height:400px;"></div>
                        <br>
                        <div class='list-group' id='LocationList'>
                            <strong class='text-center'><a class='disabled'>No Data</a></strong>
                        </div>

                                                <br>
                        <button class='btn btn-primary btn-block' type='button' onclick='setProximityAlert()'>Set Proximity Alert</button>
                        <br>
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js" crossorigin="anonymous"></script>
<script src="assets/demo/chart-area-demo.js"></script>
<script src="assets/demo/chart-bar-demo.js"></script>
<script src="assets/demo/chart-pie-demo.js"></script>
<script src="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/js/bootstrap4-toggle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.1/js/toastr.js"></script>

<script>

    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "3000",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    var switchStatus = true;
    $("#trackingToggle").on('change', function() {
        if ($(this).is(':checked')) {
            switchStatus = $(this).is(':checked');
            //alert(switchStatus);// To verify

            var msg = 'Tracking is ON!!!';
            toastr.success(msg);
        }
        else {
            switchStatus = $(this).is(':checked');
            //alert(switchStatus);// To verify
            var msg = 'Tracking is OFF!!!';
            toastr.error(msg);
        }
    });
</script>
<script>

    var lineCoords = [];
    var firebaseRef = new Firebase("https://lapcs-24113.firebaseio.com/geofire/");
    var geoFire = new GeoFire(firebaseRef);

    // Create a new GeoFire key under users Firebase location
    //var geoFire = new GeoFire(firebaseRef.child(“geofire”));

    function logout() {
        $.post("endSession.php", function (data) {

            if (data == "success")
                window.location.href = "login.html";
            else
            {
                window.alert("Try Again");
            }
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

    var listOfMobilesNames = [];
    var database = firebase.database();

    //var selectedDeviceIs =parseInt(document.cookie.charAt(0));
    var selectedDeviceIs=-1;

    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID);

    SelectedDeviceRef.once('value').then(function (data) {
      //  alert("UserDevices val from db --> "+data.val());
        selectedDeviceIs =parseInt(data.val());
    });


    //loading current moblie
    //var ref = database.ref('/Users/' + userID);
    var ref = database.ref('/ChildMetaData/' + userID);
    ref.on('value', function (data) {
        //ref.on('value', function (data) {
       // alert("ref --> Users");
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
                   // alert("ref --> Mobiles");
                    var details1 = data.val();
                    var nameMobile = "";
                    nameMobile = nameMobile + details1 + " ";
                    listOfMobilesNames.push(nameMobile);
                    document.getElementById("mobiles").innerHTML = getOldData + "<a class='dropdown-item' href='#'  onclick='setCurrentMobile(" + a + ")'>" +
                        "<i class='fas fa-mobile'></i>&nbsp&nbsp" + listOfMobilesNames[a] + "</a>";
                    getOldData = document.getElementById("mobiles").innerHTML;
                    a++;

                    if(listOfMobilesNames.length != 0 && listOfMobilesNames.length>=selectedDeviceIs+1)
                    {
                        setCurrentMobile(selectedDeviceIs);

                    }
                });
            }
        }
    });

    var geoQuery;
    var map;
    var center;
    var circle=null;

    var proxmityalertRadius;
    var proxmityalertLAT;
    var proxmityalertLONG;
    var proxmityalertOldCenter;

    var onKeyEnteredRegistration;
    var onKeyExitedRegistration;

    var geoCodedAddress;

    var geocoder;
    var markers = [];

    function setCurrentMobile(data) {
        var database = firebase.database();

        if(data != -1)
        {
            document.getElementById("SelectedDevice").innerHTML = listOfMobilesNames[data];
        }
        //document.cookie = data;
        var ref = database.ref().child('SelectedDevice').child(userID).set(data);      //one parent multiple pc at a time <-- selection overlap



        //storing IMEI in selected device
        localStorage.setItem("SelectedDevice", listOfMobiles[data]);
        //alert('listOfMobiles[data]=>'+listOfMobiles[data]);
     //   toastr.info('listOfMobiles[data]=>'+listOfMobiles[data]);



        ///////////////////////////////////////////////////////////////////
        var proxmityalertRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('proxmityalert');
        proxmityalertRef.on('value', function (snapshot) {
           // alert("ref --> proxmityalert");
            if (snapshot.exists()) {
                snapshot.forEach(function(childSnapshot) {
                    var childKey = childSnapshot.key;
                    var childData = childSnapshot.val();
                  //  toastr.info(childKey +" : "+ childData);


                    if(new String(childKey).valueOf() == new String("radius").valueOf())
                    {
                        proxmityalertRadius = childData;
                       // toastr.info("proxmityalertRadius : "+ childData);
                    }
                    if(new String(childKey).valueOf() == new String("lat").valueOf())
                    {
                        proxmityalertLAT = childData;
                       // toastr.info("proxmityalertLAT : "+ childData);
                    }
                    if(new String(childKey).valueOf() == new String("lng").valueOf())
                    {
                        proxmityalertLONG = childData;
                      //  toastr.info("proxmityalertLONG : "+ childData);
                    }
                });


                geocoder = new google.maps.Geocoder;
                geocoder.geocode({'location': new google.maps.LatLng(proxmityalertLAT, proxmityalertLONG)}, function(results, status) {
                    if (status === 'OK') {
                        if (results[0]) {
                            geoCodedAddress = results[0].formatted_address;
                         //   toastr.info("geoCodedAddress=>"+geoCodedAddress);
                        } else {
                            //window.alert('No results found');
                          toastr.error('No results found');
                        }
                    } else {
                       // toastr.error('Geocoder failed due to: ' + status);
                    }
                });

            }
            else
            {
                toastr.error("Circle Don't Exist");
                circle = null;
            }
        });

        ///////////////////////////////////////////////////////////////////

        var filesFetched;
        //Check for Location updates
        var ref = database.ref('/Users/' + userID + '/' + listOfMobiles[data]+'/getLocations');

        ref.on('value', function (data) {
           // alert("ref --> getLocations");
            document.getElementById('LocationList').innerHTML = "<strong class='text-center'><a>No Data</a></strong>";
            var details = data.val();
            var valData = Object.values(details);
            if (details.startsWith("//LAPCS//requestLocation//LAPCS//")) {
                filesFetched = details.split("//LAPCS//");
                var oldData = "";


                for (var i = 2; i < filesFetched.length - 1; i++) {

                    var latlng = filesFetched[i].split(',');

                    if(switchStatus == true)
                    {
                        //alert('localStorage.getItem("SelectedDevice")=>'+localStorage.getItem("SelectedDevice"));

                        var geolocate = new google.maps.LatLng(parseFloat(latlng[0]) , parseFloat(latlng[1]));
                        center = geolocate;
                        geoFire.set(localStorage.getItem("SelectedDevice"), [parseFloat(latlng[0]) , parseFloat(latlng[1])]).then(function() {
                            console.log("Provided key has been added to GeoFire");
                            toastr.info("Provided key has been added to GeoFire at Node:" + geoFireRef);
                        }, function(error) {
                            console.log("Error: " + error);
                        });
                        lineCoords.push(geolocate);

                        var lineCoordinatesPath = new google.maps.Polyline({
                            path: lineCoords,
                            geodesic: true,
                            strokeColor: '#FF0000'
                        });

                        var mapProp= {
                            center:geolocate,
                            zoom:17,
                        };

                        map = new google.maps.Map(document.getElementById("googleMap"),mapProp);

                        var icon_marker = "https://drive.google.com/uc?export=view&id=1ZaXiCvtTk5_xlIvY1bDMtgLm4vNxATj3";
                        var icon_marker_2 = "https://drive.google.com/uc?export=view&id=1_KI5ZX3VlI0MerYPOim18-UXK5kkk2oM";
                        var icon_marker_3 = "https://drive.google.com/uc?export=view&id=1CqAyvW80om1oJY5jFG_fhSi0lY_-VdnN";


                        ////////////////////////////////////////////////////////////////////////////////

                        // Create the search box and link it to the UI element.
                        var input = document.getElementById('pac-input');
                        //input.hidden = false;
                        var searchBox = new google.maps.places.SearchBox(input);
                        //map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

                        // Bias the SearchBox results towards current map's viewport.
                        map.addListener('bounds_changed', function() {
                            searchBox.setBounds(map.getBounds());
                        });

                        // Listen for the event fired when the user selects a prediction and retrieve
                        // more details for that place.
                        searchBox.addListener('places_changed', function() {
                            var places = searchBox.getPlaces();

                            if (places.length == 0) {
                                return;
                            }

                            // Clear out the old markers.
                            markers.forEach(function(marker) {
                                marker.setMap(null);
                            });
                            markers = [];

                            // For each place, get the icon, name and location.
                            var bounds = new google.maps.LatLngBounds();
                            places.forEach(function(place) {
                                if (!place.geometry) {
                                    console.log("Returned place contains no geometry");
                                    return;
                                }



                                // Create a marker for each place.
                                markers.push(new google.maps.Marker({
                                    map: map,
                                    icon: icon_marker,
                                    title: place.name,
                                    position: place.geometry.location
                                }));

                                if (place.geometry.viewport) {
                                    // Only geocodes have viewport.
                                    bounds.union(place.geometry.viewport);
                                } else {
                                    bounds.extend(place.geometry.location);
                                }
                            });
                            map.fitBounds(bounds);
                        });





                        var geocoder = new google.maps.Geocoder;




                        const marker = new google.maps.Marker({
                            map: map,
                            position: geolocate,
                            icon: icon_marker_3

                        });

                        google.maps.event.addListener(marker, 'click', function() {
                            var infowindow = new google.maps.InfoWindow({
                                position: geolocate
                            });

                            geocoder.geocode({'location': geolocate}, function(results, status) {
                                if (status === 'OK') {
                                    if (results[0]) {
                                        infowindow.setContent(
                                            '<div>'+
                                            '    <div><i class="fas fa-info-circle"></i> <h5>Child Current Location!</h5></div>'+
                                            '    <div >'+
                                            '        <a class=\'disabled\'>Latitude:' + geolocate.lat() + '</a>'+ '<br/>'+
                                            '    </div>'+
                                            '    <div >'+
                                            '        <a class=\'disabled\'>Longitude:' + geolocate.lng() + '</a>'+'<br/>'+
                                            '    </div>'+
                                            '    <div>'+
                                            '        <a class=\'disabled\'>Address:' + results[0].formatted_address + '</a>'+'<br/>'+
                                            '    </div>'+
                                            '</div>'
                                        );
                                        //alert(results[0].formatted_address);
                                    } else {
                                        //window.alert('No results found');
                                        toastr.error('No results found');
                                    }
                                } else {
                                    //window.alert('Geocoder failed due to: ' + status);
                                    toastr.error('Geocoder failed due to: ' + status);
                                }
                            });
                            infowindow.open(map, marker);
                        });

                        map.setCenter(geolocate);
                        lineCoordinatesPath.setMap(map);


                      //  toastr.info("proxmityalertRadius (getLocations): "+ proxmityalertRadius);
                      //  toastr.info("proxmityalertOldCenter (getLocations): "+ proxmityalertLAT);
                      //  toastr.info("proxmityalertOldCenter (getLocations): "+ proxmityalertLONG);

                        if(proxmityalertRadius==null || proxmityalertLAT==null || proxmityalertLONG==null )
                        {
                            toastr.error("proxmityalertRadius is Not Defined or proxmityalertLAT is Not Defined or proxmityalertLONG is Not Defined");
                        }
                        else
                        {
                            proxmityalertOldCenter = new google.maps.LatLng(proxmityalertLAT, proxmityalertLONG);
                            circle = new google.maps.Circle({
                                center: proxmityalertOldCenter,
                                map: map,
                                radius: proxmityalertRadius,          // IN METERS.
                                fillColor: '#FF6600',
                                fillOpacity: 0.3,
                                strokeColor: "#FF0000",
                                strokeWeight: 3,
                                editable: true
                            });

                            var radInKM = circle.getRadius()/1000.0;
                          //  toastr.info("radInKM: "+radInKM);
                            geoQuery = geoFire.query({
                                center: [circle.getCenter().lat() , circle.getCenter().lng()],
                                radius: radInKM //kilometers
                            });

                            google.maps.event.addListener(circle, 'click', function()
                            {
                                var infoWindowProxmityAlert = new google.maps.InfoWindow({
                                    position: circle.getCenter()
                                });



                                var geoCodedAddressOfCircle = "";

                                geocoder.geocode({'location': new google.maps.LatLng(circle.getCenter().lat(), circle.getCenter().lng())}, function(results, status) {
                                    if (status === 'OK') {
                                        if (results[0]) {
                                            geoCodedAddressOfCircle = results[0].formatted_address;
                                            infoWindowProxmityAlert.setContent(
                                                '<div >'+
                                                '    <div ><i class="fas fa-info-circle"></i> <h5>Proximity Alert Info</h5></div>'+
                                                '    <div >'+
                                                '        <a class=\'disabled\'>Center Latitude:' + circle.getCenter().lat() + '</a>'+ '<br/>'+
                                                '    </div>'+
                                                '    <div >'+
                                                '        <a class=\'disabled\'>Center Longitude:' + circle.getCenter().lng() + '</a>'+'<br/>'+
                                                '    </div>'+
                                                '    <div >'+
                                                '        <a class=\'disabled\'>Radius:' + circle.getRadius() + '</a>'+'<br/>'+
                                                '    </div>'+
                                                '    <div >'+
                                                '        <a class=\'disabled\'>Address:' + geoCodedAddressOfCircle + '</a>'+'<br/>'+
                                                '    </div>'+
                                                '</div>'
                                            );
                                            //alert("geoCodedAddressOfCircle=>"+geoCodedAddressOfCircle);
                                        } else {
                                            window.alert('No results found');
                                        }
                                    } else {
                                        window.alert('Geocoder failed due to: ' + status);
                                    }
                                });



                                infoWindowProxmityAlert.open(map);

                            });
                            google.maps.event.addListener(circle, 'center_changed', function()
                            {
                                   toastr.info('Circle Center Changed!');
                                // toastr.info('radius= '+circle.getRadius()+' Meters');
                                // toastr.info('lat= '+circle.getCenter().lat());
                                // toastr.info('lng= '+circle.getCenter().lng());


                                geocoder.geocode({'location': new google.maps.LatLng(proxmityalertLAT, proxmityalertLONG)}, function(results, status) {
                                    if (status === 'OK') {
                                        if (results[0]) {
                                            geoCodedAddress = results[0].formatted_address;
                                            //   toastr.info("geoCodedAddress=>"+geoCodedAddress);
                                        } else {
                                            //window.alert('No results found');
                                            toastr.error('No results found');
                                        }
                                    } else {
                                        // toastr.error('Geocoder failed due to: ' + status);
                                    }
                                });


                            });
                            google.maps.event.addListener(circle, 'radius_changed', function()
                            {
                                   toastr.info('Circle Radius Changed!');
                                // toastr.info('radius= '+circle.getRadius()+' Meters');
                                // toastr.info('lat= '+circle.getCenter().lat());
                                // toastr.info('lng= '+circle.getCenter().lng());

                            });
                        }

                        setProximityAlert();
                    }

                    oldData = oldData + "<a class='list-group-item list-group-item-action bg-primary text-white text-center'>" + filesFetched[i] + "</a>"
                    document.getElementById('LocationList').innerHTML = oldData;
                }
            }
        });

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
                //alert(triggerRefSplit[triggerRefSplit.length-1]);

                if(listOfMobiles.length>0)
                {
                    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set("0");      //setting 0 for next reload
                }
                else
                {
                    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set("-1");      //setting -1 because last device is removed and no more devices left
                }

                window.location.reload(false);
            }

        });
    }



    function setProximityAlert() {

        //toastr.info("document.getElementById(\"SelectedDevice\").innerHTML ==> "+document.getElementById("SelectedDevice").innerHTML);

        if (localStorage.getItem("SelectedDevice") == "NA")
            window.alert("No Smartphone Selected");
        else
        {
            // toastr.info(center);
            // toastr.info(map);
            // toastr.info(circle);

            if(center==null || map==null || circle==null )
            {
                toastr.error("Map is Not Loaded or Center is Not Defined or circle is Not Defined");
                //circle creation
                if(map!=null && center!=null)
                {
                    toastr.info("Creating circle with Fixed Radius!");
                    circle = new google.maps.Circle({
                        center: center,
                        map: map,
                        radius: 100,          // IN METERS.
                        fillColor: '#FF6600',
                        fillOpacity: 0.3,
                        strokeColor: "#FF0000",
                        strokeWeight: 3,
                        editable: true
                    });

                    var radInKM = circle.getRadius()/1000.0;
                  //  toastr.info("radInKM: "+radInKM);
                    geoQuery = geoFire.query({
                        center: [circle.getCenter().lat() , circle.getCenter().lng()],
                        radius: radInKM //kilometers
                    });

                    google.maps.event.addListener(circle, 'click', function()
                    {
                        var infoWindowProxmityAlert = new google.maps.InfoWindow({
                            position: circle.getCenter()
                        });



                        var geoCodedAddressOfCircle = "";

                        geocoder.geocode({'location': new google.maps.LatLng(circle.getCenter().lat(), circle.getCenter().lng())}, function(results, status) {
                            if (status === 'OK') {
                                if (results[0]) {
                                    geoCodedAddressOfCircle = results[0].formatted_address;
                                    infoWindowProxmityAlert.setContent(
                                        '<div >'+
                                        '    <div ><i class="fas fa-info-circle"></i> <h5>Proximity Alert Info</h5></div>'+
                                        '    <div >'+
                                        '        <a class=\'disabled\'>Center Latitude:' + circle.getCenter().lat() + '</a>'+ '<br/>'+
                                        '    </div>'+
                                        '    <div >'+
                                        '        <a class=\'disabled\'>Center Longitude:' + circle.getCenter().lng() + '</a>'+'<br/>'+
                                        '    </div>'+
                                        '    <div >'+
                                        '        <a class=\'disabled\'>Radius:' + circle.getRadius() + '</a>'+'<br/>'+
                                        '    </div>'+
                                        '    <div >'+
                                        '        <a class=\'disabled\'>Address:' + geoCodedAddressOfCircle + '</a>'+'<br/>'+
                                        '    </div>'+
                                        '</div>'
                                    );
                                    //alert("geoCodedAddressOfCircle=>"+geoCodedAddressOfCircle);
                                } else {
                                    window.alert('No results found');
                                }
                            } else {
                                window.alert('Geocoder failed due to: ' + status);
                            }
                        });


                        infoWindowProxmityAlert.open(map);

                    });
                    google.maps.event.addListener(circle, 'center_changed', function()
                    {
                        toastr.info('Circle Center Changed!');
                        // toastr.info('radius= '+circle.getRadius()+' Meters');
                        // toastr.info('lat= '+circle.getCenter().lat());
                        // toastr.info('lng= '+circle.getCenter().lng());


                    });
                    google.maps.event.addListener(circle, 'radius_changed', function()
                    {
                        toastr.info('Circle Radius Changed!');
                        // toastr.info('radius= '+circle.getRadius()+' Meters');
                        // toastr.info('lat= '+circle.getCenter().lat());
                        // toastr.info('lng= '+circle.getCenter().lng());

                    });


                    var proxmityalertRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('proxmityalert');
                    proxmityalertRef.child('radius').set(circle.getRadius());
                    proxmityalertRef.child('lat').set(circle.getCenter().lat());
                    proxmityalertRef.child('lng').set(circle.getCenter().lng());


                }
            }
            else
            {
                //toastr.info("center: "+center+" map: "+map);

                var proxmityalertRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('proxmityalert');
                proxmityalertRef.once('value').then(function (snapshot) {
                    if(snapshot.exists())
                    {
                        proxmityalertRef.child('radius').set(circle.getRadius());
                        proxmityalertRef.child('lat').set(circle.getCenter().lat());
                        proxmityalertRef.child('lng').set(circle.getCenter().lng());
                    }
                    else
                    {
                        toastr.error("proxmityalert Node not exist in DB!");
                    }

                });


                var radInKM = circle.getRadius()/1000.0;

                geoQuery.updateCriteria({
                    center: [circle.getCenter().lat() , circle.getCenter().lng()],
                    radius: radInKM //kilometers
                });

                var DeviceName = document.getElementById("SelectedDevice").innerHTML;

                onKeyEnteredRegistration = geoQuery.on("key_entered", function(key, location, distance) {
                    var msg = "Your Child  " + DeviceName + " is entering " + location +" (" + distance + " km away)";
                    if(geoCodedAddress != null)
                    {
                        msg = "Your Child  " + DeviceName + " is entering " + location +" ("+geoCodedAddress+")"+" (" + distance + " km away)";
                    }
                    console.log(msg);
                    toastr.success(msg);
                    //send Push Notification here
                    var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set("NA");
                    var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set(localStorage.getItem("SelectedDevice") + "}proxmityalert}"+msg);

                });

                onKeyExitedRegistration = geoQuery.on("key_exited", function(key, location, distance) {
                    var msg = "Your Child  " + DeviceName + " is  Leaving " + location +" (" + distance + " km away)";
                    if(geoCodedAddress != null)
                    {
                        msg = "Your Child  " + DeviceName + " is Leaving " + location +" ("+geoCodedAddress+")"+" (" + distance + " km away)";
                    }

                    console.log(msg);
                    toastr.success(msg);
                    //send Push Notification here
                    var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set("NA");
                    var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set(localStorage.getItem("SelectedDevice") + "}proxmityalert}"+msg);

                });

                toastr.info("Proximity Alert is Generated");



            }
        }

    }


</script>
</body>
</html>