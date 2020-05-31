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
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
        }

        .header {
            text-align: center;
            padding: 32px;
        }

        .row {
            display: -ms-flexbox; /* IE 10 */
            display: flex;
            -ms-flex-wrap: wrap; /* IE 10 */
            flex-wrap: wrap;
            padding: 0 4px;
        }

        /* Create two equal columns that sits next to each other */
        .column {
            -ms-flex: 50%; /* IE 10 */
            flex: 50%;
            padding: 0 4px;
        }

        .column img {
            margin-top: 8px;
            vertical-align: middle;
        }

        /* Style the buttons */
        .btn {
            border: none;
            outline: none;
            padding: 10px 16px;
            background-color: #f1f1f1;
            cursor: pointer;
            font-size: 18px;
        }

        .btn:hover {
            background-color: #ddd;
        }

        .btn.active {
            background-color: #007bff;
            color: white;
        }
    </style>
    <title>Parent Dashboard</title>
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>
    <link href="css/styles.css" rel="stylesheet" />
    <link href="https://cdn.datatables.net/1.10.20/css/dataTables.bootstrap4.min.css" rel="stylesheet" crossorigin="anonymous" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js" crossorigin="anonymous"></script>
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.9.1/firebase-database.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.12.0/firebase-storage.js"></script>
    <script src="js/loadFirebase.js"></script>
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
                <h1 class="mt-4">Photos</h1>
                <ol class="breadcrumb mb-4">
                    <li class="breadcrumb-item"><a href="index.php">Dashboard</a></li>
                    <li class="breadcrumb-item active">Photos</li>
                </ol>
                <div class="row">
                    <div class='col-md-12'>
                        <div class='list-group'>
                            <strong class='text-center'><a class='disabled'>IMAGE FOLDERS SUMMARY</a></strong>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-bordered" id="folderDataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Folder Name</th>
                                    <th>Image Count</th>
                                </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                        <br/>
                        <div class='list-group'>
                            <strong class='text-center'><a class='disabled'>CHILD DEVICE IMAGE SUMMARY</a></strong>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Image Name</th>
                                    <th>Folder Name</th>
                                </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                        <div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
                            <div class="carousel-inner" id="childFolderImages">
                            </div>
                            <a class="carousel-control-prev" href="#carouselExampleControls" role="button" data-slide="prev">
                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                <span class="sr-only">Previous</span>
                            </a>
                            <a class="carousel-control-next" href="#carouselExampleControls" role="button" data-slide="next">
                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                <span class="sr-only">Next</span>
                            </a>
                        </div>

                        <br>
                        <button class='btn btn-primary btn-block' type='button' onclick='getPhotos()'>Get Photos</button>
                        <br>
                        <button class='btn btn-primary btn-block' type='button' onclick='LoadPhotos()'>Load Photos</button>
                        <br>
                        <div class='list-group' id='downloadedImagesCount'>
                            <strong class='text-center'><a class='list-group-item list-group-item-action bg-info text-white'> Downloaded Images Count : <font color=\"red\"><span id="downloadedImagesCountSpan">0</span></font></a></strong>
                        </div>
                        <div class='list-group' id='errorList'>
                            <strong class='text-center'><a class='disabled'>Errors while downloading from Firebase Storage will show below</a></strong>
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js" crossorigin="anonymous"></script>
<script src="assets/demo/chart-area-demo.js"></script>
<script src="assets/demo/chart-bar-demo.js"></script>
<script src="assets/demo/chart-pie-demo.js"></script>
<script src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js" crossorigin="anonymous"></script>
<script src="https://cdn.datatables.net/1.10.20/js/dataTables.bootstrap4.min.js" crossorigin="anonymous"></script>
<script src="assets/demo/datatables-demo.js"></script>
<script>


</script>
<script>
    var table = $('#dataTable').DataTable( {
        "autoWidth": false,
        "columnDefs": [
            { "width": "5%", "targets": 0 }
        ]
    } );
    var foldertable = $('#folderDataTable').DataTable( {
        "autoWidth": false,
        "columnDefs": [
            { "width": "5%", "targets": 0 }
        ]
    } );


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

    var storage = firebase.storage();
    var storageRef = storage.ref();

    var imageStorageURLsList = [];
    var imageStorageFailedURLsList = [];

    //var ref = database.ref('/Users/' + userID);
    var ref = database.ref('/ChildMetaData/' + userID);
    ref.on('value', function (data) {
        var details = data.val();
        var a = 0;
        var keys = Object.keys(details);
        //alert(keys);
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

                    if (listOfMobilesNames.length != 0 && listOfMobilesNames.length >= selectedDeviceIs + 1) {

                        setCurrentMobile(selectedDeviceIs);
                    }
                });
            }
        }
    });

    function setCurrentMobile(data) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        table.clear().draw();
        foldertable.clear().draw();
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

        // document.getElementById("SelectedDevice").innerHTML = listOfMobilesNames[data];
        // document.cookie = data;
        // //storing IMEI in selected device
        // localStorage.setItem("SelectedDevice", listOfMobiles[data]);



            ///////////////////////////////////////////////////////////////////////////////////////////

        var database = firebase.database();
        //Check for Photos updates
        var ref = database.ref('/Users/' + userID + '/' + listOfMobiles[data]+'/getPhotos');
        var filesFetched;
        ref.on('value', function (data) {
            //document.getElementById('photoList').innerHTML = "<a>No Data</a>";
            var details = data.val();
            var valData = Object.values(details);
            //alert(valData);
            if (details.startsWith("//LAPCS//requestPhotos//LAPCS//")) {
                filesFetched = details.split("//LAPCS//");
                //alert(filesFetched);
                //var oldData = "";

                table.clear().draw();
                foldertable.clear().draw();

                var imgCount=1;
                var FolderCount=1;
                for (var i = 2; i < filesFetched.length - 1; i++) {

                    if(filesFetched[i].startsWith('Folders'))
                    {
                        var foldersString = filesFetched[i].replace(/Folders:/, "");
                        var FoldersList = foldersString.split(",");
                        for (var j = 0; j < FoldersList.length; j++) {
                            var FoldersDetails = FoldersList[j].split("-");
                            for (var k = 0; k < FoldersDetails.length; k++) {
                                FoldersDetails[k] = FoldersDetails[k].replace(/\[/g, "").replace(/\]/g, "");
                            }
                            for (var k = 0; k < FoldersDetails.length; k+=2) {
                                //oldData = oldData + "<a class='list-group-item list-group-item-action bg-success text-white'> Folder Name: <font color=\"red\">" + FoldersDetails[k] + "</font><br/> Number of Images:  <font color=\"red\">" + FoldersDetails[k+1] + "</font></a>"

                                $('#folderDataTable').dataTable().fnAddData( [
                                    FolderCount,
                                    FoldersDetails[k],
                                    FoldersDetails[k+1]
                                ] );

                            }

                        }
                        FolderCount++;

                    }
                    else
                    {
                        //pop for picking last element of array
                        //shift for picking first element of array

                        //[Camera]-[ImageName]: IMG20191110233113.jpg
                        var splittedFolderName = filesFetched[i].split(":").shift().trim();   //[Camera]-[ImageName]
                        splittedFolderName = splittedFolderName.split("-").shift().replace(/\[/g, "").replace(/\]/g, ""); //Camera
                        //alert(splittedFolderName);
                        var splittedImageName = filesFetched[i].split(":").pop().trim();  //IMG20191110233113.jpg
                        //alert(splittedImageName);
                        var imgRef = storageRef.child('images/' + userID + '/' + localStorage.getItem("SelectedDevice")+'/'+splittedFolderName+'/'+splittedImageName);
                        //alert(imgRef);

                        imgRef.getDownloadURL()
                            .then(function(url){  //success

                                //alert("imageName in getDownloadURL:"+splittedImageName);
                                imageStorageURLsList.push(url);
                                document.getElementById('downloadedImagesCountSpan').innerHTML = imageStorageURLsList.length;

                                //alert(imageStorageURLsList.length);

                                //document.querySelector('img').src = url;
                                //imgData = imgData + " <img src=${url}  name=${splittedImageName} id=${splittedImageName} style=\"width:100%\">" + filesFetched[i] + "</a>"
                                //alert(splittedImageName+"<=>"+url);
                                console.debug(url);
                            })
                            .catch(function(error) {          //Failure

                                imageStorageFailedURLsList.push(error);
                                switch (error.code) {
                                    case 'storage/object_not_found':
                                        // Object not found <font color=\"red\">
                                        document.getElementById('errorList').innerHTML = document.getElementById('errorList').innerHTML+ "<a class='list-group-item list-group-item-action bg-danger text-white'> Image Not Found <br/><font color='black'> Error Details:  "+error.message+"</font></a>";
                                        console.error(error);
                                        break;
                                    case 'storage/unauthorized':
                                        // User doesn't have permission to access the object
                                        document.getElementById('errorList').innerHTML = document.getElementById('errorList').innerHTML+ "<a class='list-group-item list-group-item-action bg-danger text-white'> User doesn't have permission to access the object <br/><font color='black'> Error Details:  "+error.message+"</font></a>";
                                        console.error(error);
                                        break;
                                    case 'storage/canceled':
                                        // User canceled the upload
                                        document.getElementById('errorList').innerHTML = document.getElementById('errorList').innerHTML+ "<a class='list-group-item list-group-item-action bg-danger text-white'> User canceled the upload <br/><font color='black'> Error Details: "+error.message+"</font></a>";
                                        console.error(error);
                                        break;
                                    case 'storage/unknown':
                                        // Unknown error occurred, inspect error.serverResponse
                                        document.getElementById('errorList').innerHTML = document.getElementById('errorList').innerHTML+ "<a class='list-group-item list-group-item-action bg-danger text-white'>  Unknown error occurred, inspect error.serverResponse <br/><font color='black'> Error Details:  "+error.message+"</font></a>";
                                        console.error(error);
                                        break;
                                    default:
                                        document.getElementById('errorList').innerHTML = document.getElementById('errorList').innerHTML+ "<a class='list-group-item list-group-item-action bg-danger text-white'>  Image Loading Error Occured <br/><font color='black'> Error Details:  "+error.message+"</font></a>";
                                        console.error(error);
                                        break;
                                }


                            });

                        $('#dataTable').dataTable().fnAddData( [
                            imgCount,
                            splittedImageName,
                            splittedFolderName
                        ] );

                        //oldData = oldData + "<a class='list-group-item list-group-item-action bg-primary text-white'>" + imgCount+": "+splittedImageName + "</a>"
                        imgCount++;
                    }

                    //document.getElementById('photoList').innerHTML = oldData;
                }
            }
        });



        //Live Scenario

        var livePhotoCount = 0;
        var refCurrentPhoto = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('getCurrentPhoto');
        refCurrentPhoto.on('value', function (data) {
            var imageURL = data.val();
            //alert(imageURL);

            var splitURL  = imageURL.split("?").shift().trim();
            //alert(splitURL);
            var splitImageName  = splitURL.split("%2F").pop().trim();
            //alert(splitImageName);
            var imgData = document.getElementById('childFolderImages').innerHTML;
            //imgData = imgData + " <br/><img src="+imageURL+" name="+splitImageName+"  id="+splitImageName+"  style=\"width:25%\"> <br/>" + splitImageName

            if(livePhotoCount==0)
            {
                imgData = imgData +
                    " <div class=\"carousel-item active\">"+
                    "<img class=\"d-block w-100\" src="+imageURL+" name="+splitImageName+"  id="+splitImageName+" alt="+splitImageName+">\n" +
                    "<div class=\"carousel-caption d-none d-md-block\">\n"	+
                    "   <span class=\"d-block p-2 bg-primary text-white\"><h5>"+splitImageName+"</h5></span>\n"	+
                    "</div>"+
                    "</div>";
            }
            else
            {
                imgData = imgData +
                    " <div class=\"carousel-item\">"+
                    "<img class=\"d-block w-100\" src="+imageURL+" name="+splitImageName+"  id="+splitImageName+" alt="+splitImageName+">\n" +
                    "<div class=\"carousel-caption d-none d-md-block\">\n"	+
                    "   <span class=\"d-block p-2 bg-primary text-white\"><h5>"+splitImageName+"</h5></span>\n"	+
                    "</div>"+
                    "</div>";
            }

            document.getElementById('childFolderImages').innerHTML = imgData;
            livePhotoCount++;
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

    function LoadPhotos() {

        if (localStorage.getItem("SelectedDevice") == "NA")
            window.alert("No Smartphone Selected");
        else
        {

            var imgData = "";
            alert("Images To be Shown: "+imageStorageURLsList.length);
            for (var i = 0; i < imageStorageURLsList.length; i++)
            {
                var splitURL  = imageStorageURLsList[i].split("?").shift().trim();
                //alert(splitURL);
                var splitImageName  = splitURL.split("%2F").pop().trim();

                //alert(splitImageName);
                //alert(imageStorageURLsList[i]);

                //imgData = imgData + " <br/><img src="+imageStorageURLsList[i]+" name="+splitImageName+"  id="+splitImageName+"  style=\"width:25%\"> <br/>" + splitImageName
                //imgData = imgData + " <br/><img src="+imageURL+" name="+splitImageName+"  id="+splitImageName+"  style=\"width:25%\"> <br/>" + splitImageName

                if(i==0)
                {
                    imgData = imgData +
                        " <div class=\"carousel-item active\">"+
                        "<img class=\"d-block w-100\" src="+imageStorageURLsList[i]+" name="+splitImageName+"  id="+splitImageName+" alt="+splitImageName+">\n" +
                        "<div class=\"carousel-caption d-none d-md-block\">\n"	+
                        "   <span class=\"d-block p-2 bg-primary text-white\"><h5>"+splitImageName+"</h5></span>\n"	+
                        "</div>"+
                        "</div>";
                }
                else
                {
                    imgData = imgData +
                        " <div class=\"carousel-item\">"+
                        "<img class=\"d-block w-100\" src="+imageStorageURLsList[i]+" name="+splitImageName+"  id="+splitImageName+" alt="+splitImageName+">\n" +
                        "<div class=\"carousel-caption d-none d-md-block\">\n"	+
                        "   <span class=\"d-block p-2 bg-primary text-white\"><h5>"+splitImageName+"</h5></span>\n"	+
                        "</div>"+
                        "</div>";
                }


            }
            document.getElementById('childFolderImages').innerHTML = imgData;

        }

    }

    function getPhotos() {

        //document.getElementById('photoList').innerHTML = "<a>No Data</a>";
        document.getElementById('childFolderImages').innerHTML = "";
        if (localStorage.getItem("SelectedDevice") == "NA")
            window.alert("No Smartphone Selected");
        else {
            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set("NA");
            var ref = database.ref().child('Users').child(userID).child(localStorage.getItem("SelectedDevice")).child('trigger').set(localStorage.getItem("SelectedDevice") + "/photo");
        }
    }



</script>
</body>
</html>
