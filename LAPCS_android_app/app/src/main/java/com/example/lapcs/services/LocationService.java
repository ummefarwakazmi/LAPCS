package com.example.lapcs.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.R;
import com.example.lapcs.Receivers.ProximityIntentReceiver;
import com.example.lapcs.Utils.FireBaseUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.LAPCS_LOCATION_CHANNEL_ID;
import static com.example.lapcs.AppConsts.PROX_ALERT_INTENT;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class LocationService extends Service {

    DatabaseReference mDatabaseRef;
    DatabaseReference mGeoFireRef;
    GeoFire mGeoFire;
    SharedPreferences sharedPreferences;
    String Imei = "";
    String userID = "";
    Location location = null;

    public LocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            if (!("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString())) &&
                    !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
            ) {

            Log.d(TAG, "Imei: " + Imei + "userID: " + userID);
            //getting DB values at start without any event occur
            mDatabaseRef.child("Users").child(userID).child(Imei).child("proxmityalert").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        String Latitude = "";
                        String Longitude = "";
                        String Radius = "";

                        double latitude = 0.0;
                        double longitude = 0.0;
                        double radius = 0.0;
                        if (dataSnapshot.hasChild("lat")) {

                            Latitude = dataSnapshot.child("lat").getValue().toString();
                            latitude = Double.parseDouble(Latitude);
                        }
                        if (dataSnapshot.hasChild("lng")) {
                            Longitude = dataSnapshot.child("lng").getValue().toString();
                            longitude = Double.parseDouble(Longitude);
                        }
                        if (dataSnapshot.hasChild("lat")) {
                            Radius = dataSnapshot.child("radius").getValue().toString();
                            radius = Double.parseDouble(Radius);

                        }

                        if (!("".equals(Latitude.toString())) && !(TextUtils.isEmpty(Latitude.toString())) &&
                                !("".equals(Longitude.toString())) && !(TextUtils.isEmpty(Longitude.toString())) &&
                                !("".equals(Radius.toString())) && !(TextUtils.isEmpty(Radius.toString()))
                        ) {

//                            LatLng location = new LatLng(latitude, longitude);
//                            Location proximityAlertLocation = new Location("");
//                            proximityAlertLocation.setLatitude( latitude );
//                            proximityAlertLocation.setLongitude( longitude );

                            addProximityAlert(latitude,longitude, (float) radius);

                            Log.d(TAG, "Latitude: " + Latitude + " Longitude: " + Longitude + " Radius: " + Radius);
                            radius = radius / 1000.0;     //--> converting into KM
                            GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);

                            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(String key, GeoLocation location) {
                                    String msg = "Your Child  " + Build.MODEL + " is Entering " + location;
                                    String parentDeviceTokenID = "";
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                                    if (!parentDeviceTokenID.equals("")) {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                                        Log.d(TAG, "Proximity Alert Message sent to parent");
                                    } else {
                                        Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                                        //Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onKeyExited(String key) {
                                    String msg = "Your Child  " + Build.MODEL + " is Leaving";
                                    String parentDeviceTokenID = "";
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                                    if (!parentDeviceTokenID.equals("")) {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                                        Log.d(TAG, "Proximity Alert Message sent to parent");
                                    } else {
                                        Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                                        //Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onKeyMoved(String key, GeoLocation location) {

                                }

                                @Override
                                public void onGeoQueryReady() {

                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });


                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabaseRef.child("Users").child(userID).child(Imei).child("proxmityalert").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.hasChildren()) {
                        String Latitude = "";
                        String Longitude = "";
                        String Radius = "";

                        double latitude = 0.0;
                        double longitude = 0.0;
                        double radius = 0.0;
                        if (dataSnapshot.hasChild("lat")) {

                            Latitude = dataSnapshot.child("lat").getValue().toString();
                            latitude = Double.parseDouble(Latitude);
                        }
                        if (dataSnapshot.hasChild("lng")) {
                            Longitude = dataSnapshot.child("lng").getValue().toString();
                            longitude = Double.parseDouble(Longitude);
                        }
                        if (dataSnapshot.hasChild("lat")) {
                            Radius = dataSnapshot.child("radius").getValue().toString();
                            radius = Double.parseDouble(Radius);

                        }

                        if (!("".equals(Latitude.toString())) && !(TextUtils.isEmpty(Latitude.toString())) &&
                                !("".equals(Longitude.toString())) && !(TextUtils.isEmpty(Longitude.toString())) &&
                                !("".equals(Radius.toString())) && !(TextUtils.isEmpty(Radius.toString()))
                        ) {

//                            LatLng location = new LatLng(latitude, longitude);
//                            Location proximityAlertLocation = new Location("");
//                            proximityAlertLocation.setLatitude( latitude );
//                            proximityAlertLocation.setLongitude( longitude );
                            addProximityAlert(latitude,longitude, (float) radius);
                            Log.d(TAG, "Latitude: " + Latitude + " Longitude: " + Longitude + " Radius: " + Radius);

                            radius = radius / 1000.0;     //--> converting into KM
                            GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);

                            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(String key, GeoLocation location) {
                                    String msg = "Your Child  " + Build.MODEL + " is Entering " + location;
                                    String parentDeviceTokenID = "";
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                                    if (!parentDeviceTokenID.equals("")) {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                                        Log.d(TAG, "Proximity Alert Message sent to parent");
                                    } else {
                                        Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                                        //Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onKeyExited(String key) {
                                    String msg = "Your Child  " + Build.MODEL + " is Leaving";
                                    String parentDeviceTokenID = "";
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                                    if (!parentDeviceTokenID.equals("")) {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                                        Log.d(TAG, "Proximity Alert Message sent to parent");
                                    } else {
                                        Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                                        //Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onKeyMoved(String key, GeoLocation location) {

                                }

                                @Override
                                public void onGeoQueryReady() {

                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });


                        }

                    }

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            Log.d(TAG, this.getClass().getName() + ": Imei and userID is empty !!");
        }


        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mDatabaseRef = FireBaseUtils.getDatabaseRef();      //<-- should be followed in whole app

        mGeoFireRef = mDatabaseRef.child("geofire");
        mGeoFire = new GeoFire(mGeoFireRef);

        populateDataFromSharedPreferneces();

        buildNotification();
        requestLocationUpdates();
    }

    //Create the persistent notification//
    private void buildNotification() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("stop");

        registerReceiver(stopReceiver, filter);

        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, 0, new Intent("stop"), PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.siren);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //For API 26+ you need to put some additional code like below:
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mChannel = new NotificationChannel(LAPCS_LOCATION_CHANNEL_ID, "LAPCS LOCATION CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription("This is Loci Mobi Location Notification Channel");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        // Create the persistent notification
        Notification notification = new NotificationCompat.Builder(this, LAPCS_LOCATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.lapcs_logo_2b)
                .setContentText(getString(R.string.Tracking_Enabled_Notification_Str))
                //Make this notification ongoing so it can’t be dismissed by the user//
                .setOngoing(true)
                .setColor(Color.BLUE)
                .setContentIntent(broadcastIntent)
                .setSound(soundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //to show content in lock screen
                .setSmallIcon(R.drawable.ic_my_location_black_24dp)
                .build();
        startForeground(AppConsts.Notification_ID, notification);
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            //Stop the Service//
            stopSelf();
        }
    };


    //Initiate the request to track the device's location//
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device’s location//
        request.setInterval(15 * 1000);
        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    location = locationResult.getLastLocation();
                    if (location != null) {
                        //Save the location data to the database//
                        saveLocationToDB(location);
                        saveGeoFireDataToDB(location);
                    }
                }
            }, null);
        }
    }

    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + "," + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }

    public void saveLocationToDB(Location location) {
        String UserLocationStr = "";
        UserLocationStr = UserLocationStr + locationStringFromLocation(location) + "//LAPCS//";

        mDatabaseRef.child("Users").child(userID).child(Imei).child("getLocations").setValue("NA");
        mDatabaseRef.child("Users").child(userID).child(Imei).child("getLocations").setValue("//LAPCS//requestLocation//LAPCS//" + UserLocationStr);
    }

    public void saveGeoFireDataToDB(Location location) {
        mGeoFire.setLocation(Imei, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.d(TAG, "There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.d(TAG, "Location Saved in GeoFire Successfully!");
                }
            }
        });
    }

    private void addProximityAlert(double latitude, double longitude,float radius) {

        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                radius, // the radius of the central point of the alert region, in meters
                -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);

    }


    public  void populateDataFromSharedPreferneces()
    {
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        Imei = sharedPreferences.getString("Imei", "");
        userID = sharedPreferences.getString("UserID", "");
    }

    public  Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return  (lm != null) && (lm.isLocationEnabled());
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }



}
