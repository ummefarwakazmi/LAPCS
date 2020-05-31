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
    <style>
        .custom {
            width: 150px !important;
        }
        .datatable_selected_row_color {
            background: gold !important;
        }
        .fa-loading-bg, .fa-loading-icon-wrapper {
            position: absolute;
            left: 0;
            width: 100%;
            height: 100%
        }

        .fa-loading-bg {
            top: 0;
            background: rgba(0,0,0,.6);
            z-index: 99999
        }

        .fa-loading-icon-wrapper {
            z-index: 999999;
            top: 50%;
            text-align: center
        }

        .fa-loading-icon {
            margin-top: -2.5em;
            font-size: 5em;
            color: #000
        }

        #overlay {
            background-color: black;
            position: fixed;
            top: 0; right: 0; bottom: 0; left: 0;
            opacity: 0.2; /* also -moz-opacity, etc. */
            z-index: 10;
        }

    </style>
    <title>Parent Dashboard</title>
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>
    <link href="css/styles.css" rel="stylesheet" />
    <link href="https://cdn.datatables.net/1.10.20/css/dataTables.bootstrap4.min.css" rel="stylesheet" crossorigin="anonymous" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.1/css/toastr.css" rel="stylesheet"/>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js" crossorigin="anonymous"></script>
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-database.js"></script>
    <script src="js/loadFirebase.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js" crossorigin="anonymous"></script>



</head>
<body class="sb-nav-fixed">
<nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
    <a class="navbar-brand" > <img src="logo.jpg" alt="lapcs logo" class="align-self-start rounded-circle" style="width:40px;">
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
                <h1 class="mt-4">Contacts Watchlist</h1>
                <ol class="breadcrumb mb-4">
                    <li class="breadcrumb-item"><a href="index.php">Dashboard</a></li>
                    <li class="breadcrumb-item active">Contacts Watchlist</li>
                </ol>

                <div class="row">
                    <div class='col-md-12'>
                        <div class="card-header"><i class="fas fa-table mr-1"></i>Child Contact List</div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Contact Name</th>
                                        <th>Contact Number</th>
                                        <th>Operation</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                            <div class='list-group' id='watchListDetails'>
                            </div>
                            <br>

                            <div class='list-group' id='watchListDetailsCalls'>
                            </div>
                            <br>
                            <div class='list-group' id='watchListDetailsSMS'>
                            </div>
                        </div>
                    </div>
                    <div class='col-md-12'>

                        <br>
                        <button class='btn btn-primary btn-block' type='button' onclick='LoadWatchedAndBlockedContacts()'>Load Watched and Blocked Contacts</button>
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
<script src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js" crossorigin="anonymous"></script>
<script src="https://cdn.datatables.net/1.10.20/js/dataTables.bootstrap4.min.js" crossorigin="anonymous"></script>
<script src="assets/demo/datatables-demo.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.1/js/toastr.js"></script>

<link rel="stylesheet" href="faloading/jquery.faloading.css">
<script src="faloading/jquery.faloading.js"></script>




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


    var BlockedContactsNodeValue = "";
    var WatchedContactsNodeValue = "";

    var filesFetched = [];

    // Array holding selected row IDs
    var rows_selected = [];

    var table = $('#dataTable').DataTable( {
        "autoWidth": false,
        "columnDefs": [
            { "width": "5%", "targets": 0 }
        ]
    } );


    $('#dataTable tbody').on( 'click', 'tr', function (e) {
        //alert("TR clicked");

        var $row = $(this).closest('tr');

        // Get row data
        var data = table.row($row).data();
        //alert("row data=>"+data);

        // Get row ID
        var rowId = data[0];

        document.getElementById('watchListDetails').innerHTML = "<strong class='text-center'><a class='disabled'>Watch List Detail of Selected Contact</a></strong>"+
            "<a class='list-group-item list-group-item-action bg-info text-white'> " +
            "Contact ID: <font color='black'> "+data[0]+"</font>" + "<br/>"+
            "Contact Name: <font color='black'> "+data[1]+"</font>" + "<br/>"+
            "Contact Number: <font color='black'> "+data[2]+"</font></a>";

        // Determine whether row ID is in the list of selected row IDs
        var index = $.inArray(rowId, rows_selected);

        if(index === -1)    // If row ID is not in list of selected row IDs
        {
            if(rows_selected.length > 0)    //means some other row exist in the list. so remove it and remove the bg color
            {
                var lastInsertedIndex = rows_selected[0];
                //alert("lastInsertedIndex=>"+lastInsertedIndex);
                rows_selected.splice(0,  rows_selected.length); // clearing the whole array

                //alert("last inserted row data ==>"+table.row(lastInsertedIndex-1).data());
                var rowIndex = ":eq("+parseInt(lastInsertedIndex-1, 10)+")";
                table.rows(rowIndex)
                    .nodes()
                    .to$()
                    .toggleClass('datatable_selected_row_color');

            }

            rows_selected.push(rowId);
            //            //alert("pushed row number=>"+rowId);
            $(this).toggleClass('datatable_selected_row_color');

            //Fetching Watchlist Details of current selected row.

            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set("NA");

            var watchedListContactsDetailsRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child("watchListDetails").child(data[1].trim());   //data[1]-> Contact Name
            watchedListContactsDetailsRef.once('value').then(function (snapshot) {

                if (snapshot.exists()) {

                    var callsData = "";
                    var smsData = "";
                    var isCallHeaderSet = false;
                    var isSMSHeaderSet = false;

                    snapshot.forEach(function(childSnapshot) {

                        var childKey = childSnapshot.key;
                        var childData = childSnapshot.val();
                        //alert(childKey +" : "+ childData);

                        if(childData.length>0)
                        {
                            var obj = JSON.parse(childData);
                        }

                        for (i in obj)
                        {
                            //alert(obj[i]);
                            if(new String(childKey).valueOf() == new String("calls").valueOf())
                            {
                                if(childData.length>0)
                                {
                                    if(isCallHeaderSet == false)
                                    {
                                        callsData = "<strong class='text-center'><a class='disabled'>CALL DATA</a></strong>";
                                        isCallHeaderSet = true;
                                    }

                                    callsData = callsData +
                                        "<a class='list-group-item list-group-item-action bg-primary text-white'> " +
                                        "Number			 :	<font color='black'> "+obj[i].Number+"</font>" + "<br/>"+
                                        "OutGoingCall    :	<font color='black'> "+obj[i].OutGoingCall+"</font>" + "<br/>"+
                                        "DateTime        :	<font color='black'> "+obj[i].DateTime+"</font>" + "<br/>"+
                                        "DateTimeOnly    :	<font color='black'> "+obj[i].DateTimeOnly+"</font>" + "<br/>"+
                                        "Name            :	<font color='black'> "+obj[i].Name+"</font>" + "<br/>"+
                                        "Type            :	<font color='black'> "+obj[i].Type+"</font></a>"+ "<br/>";
                                }
                                else
                                {
                                    callsData = callsData + "<strong class='text-center'><a class='disabled'>NO CALL DATA</a></strong>";
                                }

                            }
                            else if(new String(childKey).valueOf() == new String("sms").valueOf())
                            {
                                if(childData.length>0)
                                {

                                    if(isSMSHeaderSet == false)
                                    {
                                        smsData = "<strong class='text-center'><a class='disabled'>SMS DATA</a></strong>";
                                        isSMSHeaderSet = true;
                                    }

                                    smsData = smsData +
                                        "<a class='list-group-item list-group-item-action bg-secondary text-white'> " +
                                        "Body: <font color='black'> "+obj[i].Body+"</font>" + "<br/>"+
                                        "Contact Name: <font color='black'> "+obj[i].ContactName+"</font>" + "<br/>"+
                                        "Contact Number: <font color='black'> "+obj[i].ContactNumber+"</font>" + "<br/>"+
                                        "Time: <font color='black'> "+obj[i].Time+"</font>" + "<br/>"+
                                        "Type: <font color='black'> "+obj[i].Type+"</font></a>"+ "<br/>";
                                }
                                else
                                {
                                    smsData = smsData + "<strong class='text-center'><a class='disabled'>NO SMS DATA</a></strong>";
                                }
                            }
                        }
                    });

                    document.getElementById('watchListDetailsCalls').innerHTML = callsData;
                    document.getElementById('watchListDetailsSMS').innerHTML = smsData;
                }
                else
                {
                    toastr.info("No Data Exist For This CONTACT !!");

                    document.getElementById('watchListDetailsCalls').innerHTML = "";
                    document.getElementById('watchListDetailsSMS').innerHTML = "";
                }
            });

        }
        else if (index !== -1)        // Otherwise, if row ID is in list of selected row IDs
        {
            //alert("Removed Row Number=>"+rows_selected[index]);
            rows_selected.splice(index, 1); //row.remove(index)
            $(this).toggleClass('datatable_selected_row_color');
            document.getElementById('watchListDetails').innerHTML = "<strong class='text-center'><a class='disabled'>Watch List Detail of Selected Contact</a></strong>"+
                "<a class='list-group-item list-group-item-action bg-danger text-white'> " +
                "No Row Selected</a>";

            document.getElementById('watchListDetailsCalls').innerHTML = "";
            document.getElementById('watchListDetailsSMS').innerHTML = "";

        }

    } );



    function LoadBlockedContactsFromDB() {
        var blockedContactsRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('blockedContacts');
        blockedContactsRef.once('value').then(function (snapshot) {
            BlockedContactsNodeValue = snapshot.val();
            //alert("BlockedContactsNodeValue: "+BlockedContactsNodeValue);
        });
    }

    function LoadWatchedContactsFromDB() {

        var watchedContactsRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('watchedContacts');
        watchedContactsRef.once('value').then(function (snapshot) {
            WatchedContactsNodeValue = snapshot.val();
            //alert("WatchedContactsNodeValue: "+WatchedContactsNodeValue);
        });
    }


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


    //var selectedDeviceIs =parseInt(document.cookie.charAt(0));
    var selectedDeviceIs=-1;

    var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID);

    SelectedDeviceRef.once('value').then(function (data) {
       // alert("UserDevices val from db --> "+data.val());
        selectedDeviceIs =parseInt(data.val());
    });


    var listOfMobilesNames = [];

    //loading current moblie
    //var ref = database.ref('/Users/' + userID);
    var ref = database.ref('/ChildMetaData/' + userID);

    ref.on('value', function (data) {                   // reading of IMEIs
        //alert("ref --> Users");
     //   alert("ref --> ChildMetaData");
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
                 //   alert("ref --> Mobiles");
                    var details1 = data.val();
                    var nameMobile = "";
                    nameMobile = nameMobile + details1 + " ";
                    listOfMobilesNames.push(nameMobile);
                    document.getElementById("mobiles").innerHTML = getOldData + "<a class='dropdown-item' href='#'  onclick='setCurrentMobile(" + a + ")'>" +
                        "<i class='fas fa-mobile'></i>&nbsp&nbsp" + listOfMobilesNames[a] + "</a>";
                    getOldData = document.getElementById("mobiles").innerHTML;
                    a++;

                    if(listOfMobilesNames.length != 0 && listOfMobilesNames.length>=selectedDeviceIs+1)     //check missing for if dropdown has a selected index
                    {
                        setCurrentMobile(selectedDeviceIs);
                    }
                });
            }
        }
    });

    function setCurrentMobile(data) {
        table.clear().draw();
        var database = firebase.database();

        if(data != -1)
        {
            document.getElementById("SelectedDevice").innerHTML = listOfMobilesNames[data];
        }

        //document.cookie = data;
        var SelectedDeviceRef = database.ref().child('SelectedDevice').child(userID).set(data);      //one parent multiple pc at a time <-- selection overlap
                                                                                                    //if device removes then ?

        //storing IMEI in selected device
        localStorage.setItem("SelectedDevice", listOfMobiles[data]);

        LoadBlockedContactsFromDB();
        LoadWatchedContactsFromDB();


        var ref = database.ref('/Users/' + userID + '/' + listOfMobiles[data]+'/getContacts');

        filesFetched = [];
        ref.on('value', function (data) {
          //  alert("ref --> getContacts");
            //alert("testtest");
            //document.getElementById('contactsList').innerHTML = "<a>No Data</a>";
            var details = data.val();
            var valData = Object.values(details);
            if (details.startsWith("//LAPCS//requestContacts")) {
                var names = details.split("//LAPCS//");
                $.each(names, function(i, el){
                    if($.inArray(el, filesFetched) === -1)
                        filesFetched.push(el);
                });

                toastr.success("Child Contacts has been Loaded! Press Load to See List");

                //var oldData = "";
                // for (var i = 2; i < filesFetched.length; i++) {
                //     //oldData = oldData + "<a class='list-group-item list-group-item-action bg-primary text-white'>" + filesFetched[i] + "</a>"
                // }

                //document.getElementById('contactsList').innerHTML = oldData;
            }
        });
        var triggerRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger');
        triggerRef.on('value', function (data) {
           // alert("ref --> trigger");
            var details = data.val();
            //alert(details);
            triggerRefSplit = details.split("/");
            // for (var i = 0; i < triggerRefSplit.length; i++)
            // {
            //     alert(triggerRefSplit[i]);
            // }

            if(triggerRefSplit.length > 0)
            {
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
                else if(triggerRefSplit[1]=== "block")
                {
                    //alert(triggerRefSplit[triggerRefSplit.length-1]);
                    toastr.error(triggerRefSplit[triggerRefSplit.length-1]);
                }
            }

        });
    }

    function getContacts() {
        //document.getElementById('contactsList').innerHTML = "<a>No Data</a>";
        if (localStorage.getItem("SelectedDevice") == "NA")
            window.alert("No Smartphone Selected");
        else {
            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set("NA");
            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set(localStorage.getItem("SelectedDevice") + "/contacts");
        }
    }

    $('#dataTable').on('click', 'button', function() {

        console.log($(this).data('id'));
        //alert($(this).data('id'));

        var splitContactID= $(this).data('id');
        var operationType  = splitContactID.split("-").shift().trim();
        //alert(operationType);
        var ContactID      = splitContactID.split("-").pop().trim();
        //alert(ContactID);
        var ContactName = ContactID.split("||").shift().trim();
        ContactName = ContactName.replace(/:+/g, " ");
        //alert(ContactName);
        var ContactNumber = ContactID.split("||").pop().trim();
        //alert(ContactNumber);

        if(operationType === 'watch')
        {
            //alert('clicked on watch button!');

            var NewSplitContactID;
            var watchedContactsRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('watchedContacts');

            // watchedContactsRef.on('value',function (snapshot) {
            watchedContactsRef.once('value').then(function (snapshot) {

                var nodeValue = snapshot.val();
                //alert("nodeValue= " + nodeValue);

                var ContactToInsert = "Name:"+ContactName+" Number: "+ContactNumber;
                //alert(ContactToInsert);

                updateWatchedContactsString(ContactToInsert, nodeValue);

                window.location.reload(false);

            });

        }
        else if(operationType === 'block')
        {
            //alert('clicked on block button!');

            var NewSplitContactID;
            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('blockedContacts');

            // ref.on('value',function (snapshot) {
            ref.once('value').then(function (snapshot) {

                var nodeValue = snapshot.val();
                //alert("nodeValue= " + nodeValue);

                var ContactToInsert = "Name:"+ContactName+" Number: "+ContactNumber;
                //alert(ContactToInsert);

                updateBlockedContactsString(ContactToInsert, nodeValue);

                window.location.reload(false);

            });

        }

    });

    function updateBlockedContactsString(contactNo, BlockedContactsDBStr) {

        var newNodeVal;
        if(BlockedContactsDBStr.length!=0) // Blocked Contacts Exist
        {
            //alert(BlockedContactsDBStr);

            BlockedContactsDBStr = BlockedContactsDBStr.replace(/ +/g, "=");
            //alert(BlockedContactsDBStr);

            var ContactNumberToCompare = "//LAPCS//"+contactNo;
            ContactNumberToCompare = ContactNumberToCompare.replace(/ +/g, "=");
            //alert(ContactNumberToCompare);

            if(BlockedContactsDBStr.includes(ContactNumberToCompare))   //Found in List means Number is Blocked. Now Unblocking
            {
                //alert("ContactNumberToCompare==>"+ContactNumberToCompare);
                ContactNumberToCompare = ContactNumberToCompare.replace(/[+]/g,"}");
                //alert("After + Operation in ContactNumberToCompare="+ContactNumberToCompare);


                toastr.success("Blocked Number. Now Unblocking!");
                //alert("Before Replacing="+BlockedContactsNodeValue);

                BlockedContactsDBStr = BlockedContactsDBStr.replace(/[+]/g,"}");
                //alert("After + Operation="+BlockedContactsDBStr);

                var re = new RegExp(ContactNumberToCompare, 'g');   //'g' for all occurrences
                BlockedContactsDBStr = BlockedContactsDBStr.replace(re, "");
                //alert("After Replacing="+BlockedContactsNodeValue);

                BlockedContactsDBStr = BlockedContactsDBStr.replace(/=+/g, " ");
                BlockedContactsDBStr = BlockedContactsDBStr.replace(/[}]/g,"+");
                //alert(BlockedContactsDBStr);

                newNodeVal = BlockedContactsDBStr;

            }
            else    //Number is unblokced. Now adding to Blocked List
            {
                BlockedContactsDBStr = BlockedContactsDBStr.replace(/=+/g, " ");
                //alert(BlockedContactsDBStr);
                newNodeVal = BlockedContactsDBStr+"//LAPCS//"+contactNo;
            }

        }
        else    // No Blocked Contact
        {
            newNodeVal = "//LAPCS//"+contactNo;
        }

        var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('blockedContacts');
        ref.set(newNodeVal);

        //alert("inserted in blockedContacts Node in Firebase");
        toastr.success("Block List Updated !");

    }

    function updateWatchedContactsString(contactNo, WatchedContactsDBStr) {

        var newNodeVal;
        if(WatchedContactsDBStr.length!=0) // Watched Contacts Exist
        {
            WatchedContactsDBStr = WatchedContactsDBStr.replace(/ +/g, "=");

            var ContactNumberToCompare = "//LAPCS//"+contactNo;
            ContactNumberToCompare = ContactNumberToCompare.replace(/ +/g, "=");

            if(WatchedContactsDBStr.includes(ContactNumberToCompare))   //Found in List means Number is Watched. Now UnWatching
            {
                //alert("ContactNumberToCompare==>"+ContactNumberToCompare);
                ContactNumberToCompare = ContactNumberToCompare.replace(/[+]/g,"}");
                //alert("After + Operation in ContactNumberToCompare="+ContactNumberToCompare);

                toastr.success("Watched Number. Now UnWatching!");
                //alert("Before Replacing="+WatchedContactsNodeValue);
                WatchedContactsDBStr = WatchedContactsDBStr.replace(/[+]/g,"}");
                //alert("After + Operation="+WatchedContactsDBStr);

                var re = new RegExp(ContactNumberToCompare, 'g');   //'g' for all occurances
                WatchedContactsDBStr = WatchedContactsDBStr.replace(re, "");
                //alert("After Replacing="+WatchedContactsNodeValue);

                WatchedContactsDBStr = WatchedContactsDBStr.replace(/=+/g, " ");
                WatchedContactsDBStr = WatchedContactsDBStr.replace(/[}]/g,"+");

                newNodeVal = WatchedContactsDBStr;

            }
            else    //Number is UnWatched. Now adding to Watched List
            {
                WatchedContactsDBStr = WatchedContactsDBStr.replace(/=+/g, " ");
                newNodeVal = WatchedContactsDBStr+"//LAPCS//"+contactNo;
            }

        }
        else    // No Watched Contact
        {
            newNodeVal = "//LAPCS//"+contactNo;
        }

        var watchedContactsRef = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('watchedContacts');
        watchedContactsRef.set(newNodeVal);

        //alert("inserted in watchedContacts Node in Firebase");
        toastr.success("Watch List Updated !");

    }

    function LoadWatchedAndBlockedContacts() {

        if (localStorage.getItem("SelectedDevice") == "NA")
            window.alert("No Smartphone Selected");
        else
        {
            if(BlockedContactsNodeValue.length==0)
            {
                toastr.info("Block List Contact is Empty!");
            }

            if(WatchedContactsNodeValue.length==0)
            {
                alert("Watch List Contact is Empty!");
            }


            table.clear().draw();

            for (var i = 2; i < filesFetched.length; i++) {


                //$("body").faLoading();
                //document.getElementById('overlay').style.display = 'block';

                var ContactName = filesFetched[i].split("Number:").shift().trim();
                //alert(ContactName);
                ContactName = ContactName.replace(/Name:/, "");
                //alert(ContactName);

                var ContactNumber = filesFetched[i].split("Number:").pop().trim();
                //alert(ContactNumber);

                var contactID = ContactName.replace(/ +/g, ":") +"||"+ ContactNumber.replace(/ +/g, "");

                //alert(contactID);
                var contactFromDBToCompare = filesFetched[i].replace(/ +/g, "");

                WatchedContactsNodeValue = WatchedContactsNodeValue.replace(/ +/g, "");
                BlockedContactsNodeValue = BlockedContactsNodeValue.replace(/ +/g, "");

                var btnToInsertString = "";

                if(     WatchedContactsNodeValue.includes(contactFromDBToCompare) == true   &&
                    BlockedContactsNodeValue.includes(contactFromDBToCompare) == true   )
                {
                    //alert(contactFromDBToCompare+'=> Found in '+WatchedContactsNodeValue);
                    //alert(contactFromDBToCompare+'=> Found in '+BlockedContactsNodeValue);
                    btnToInsertString = '<button type="button" class=\"btn btn-danger custom\" data-id=watch-'+contactID+' ><i class=\"fa fa-eye-slash\"></i>&nbsp;Un Watch</button>&nbsp;'+
                        '<button type="button" class=\"btn btn-danger custom\" data-id=block-'+contactID+' ><i class=\"fa fa-check-circle\"></i>&nbsp;Un Block</button>';

                }
                else if(    WatchedContactsNodeValue.includes(contactFromDBToCompare) == false   &&
                    BlockedContactsNodeValue.includes(contactFromDBToCompare) == true   )
                {
                    //alert(contactFromDBToCompare+'=> Not Found in '+WatchedContactsNodeValue);
                    //alert(contactFromDBToCompare+'=> Found in '+BlockedContactsNodeValue);
                    btnToInsertString = '<button type="button" class=\"btn btn-info custom\" data-id=watch-'+contactID+' ><i class=\"fa fa-eye\"></i>&nbsp;Watch</button>&nbsp;'+
                        '<button type="button" class=\"btn btn-danger custom\" data-id=block-'+contactID+' ><i class=\"fa fa-check-circle\"></i>&nbsp;Un Block</button>';

                }
                else if(    WatchedContactsNodeValue.includes(contactFromDBToCompare) == true   &&
                    BlockedContactsNodeValue.includes(contactFromDBToCompare) == false   )
                {
                    //alert(contactFromDBToCompare+'=> Found in '+WatchedContactsNodeValue);
                    //alert(contactFromDBToCompare+'=> Not Found in '+BlockedContactsNodeValue);
                    btnToInsertString = '<button type="button" class=\"btn btn-danger custom\" data-id=watch-'+contactID+' ><i class=\"fa fa-eye-slash\"></i>&nbsp;Un Watch</button>&nbsp;'+
                        '<button type="button" class=\"btn btn-info custom\" data-id=block-'+contactID+' ><i class=\"fa fa-ban\"></i>&nbsp;Block</button>'

                }
                else
                {
                    //alert(contactFromDBToCompare+'=> Not Found in '+WatchedContactsNodeValue);
                    //alert(contactFromDBToCompare+'=> Not Found in '+BlockedContactsNodeValue);
                    btnToInsertString = '<button type="button" class=\"btn btn-info custom\" data-id=watch-'+contactID+' ><i class=\"fa fa-eye\"></i>&nbsp;Watch</button>&nbsp;'+
                        '<button type="button" class=\"btn btn-info custom\" data-id=block-'+contactID+' ><i class=\"fa fa-ban\"></i>&nbsp;Block</button>'

                }

                $('#dataTable').dataTable().fnAddData( [
                    i-1,
                    ContactName,
                    ContactNumber,
                    btnToInsertString
                ] );



            }
        }


        document.getElementById('watchListDetails').innerHTML = "<strong class='text-center'><a class='disabled'>Watch List Detail of Selected Contact</a></strong>"+
            "<a class='list-group-item list-group-item-action bg-danger text-white'> " +
            "No Row Selected</a>";



        $(window).scrollTop(0);

    }


</script>
</body>
</html>
