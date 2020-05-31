package com.example.lapcs.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.developers.imagezipper.ImageZipper;
import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.Utils.MediaUtils;
import com.example.lapcs.models.imageFolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.lapcs.AppConsts.TAG;


public class getPhotosIntentService extends IntentService {

    DatabaseReference mDatabase;
    FirebaseStorage mStorage;
    StorageReference mStorageReference;
    SharedPreferences mSharedPreferences;

    String Imei = "";
    String userID = "";
    ArrayList<imageFolder> userPhoneImagesList;

    public getPhotosIntentService() {
        super("getPhotosIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        initializeData();
        uploadImageNameListToFirebaseDatabase();    //populate -> getPhotos Node
        uploadImagesToFireBaseCloudStorage();   //upload to storage and store downloadable link to getCurrentPhoto Node
    }

    public void initializeData() {

        userPhoneImagesList = MediaUtils.getPicturePaths(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        getDataFromSharedPrefernece();

    }

    public void uploadImageNameListToFirebaseDatabase()
    {
        String data = formatImageListForUploading();

        mDatabase.child("Users").child(userID).child(Imei).child("getPhotos").setValue("NA");
        mDatabase.child("Users").child(userID).child(Imei).child("getPhotos").setValue("//LAPCS//requestPhotos//LAPCS//"+data);
        Log.d(TAG,"Photos Metadata Stored in getPhotos Successfully!! -->" + "//LAPCS//requestPhotos//LAPCS//"+data);

    }

    public String formatImageListForUploading()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("");
        sb.append("Folders:");
        for(int i = 0;i < userPhoneImagesList.size();i++)
        {
            sb.append( "["+userPhoneImagesList.get(i).getFolderName()+"]"+"-"+"["+userPhoneImagesList.get(i).getNumberOfPics()+"]");
            if(i!=userPhoneImagesList.size()-1)
            {
                sb.append(",");
            }
        }

        sb.append("//LAPCS//");
        for(int i = 0;i < userPhoneImagesList.size();i++)
        {
            for(int j = 0; j < userPhoneImagesList.get(i).getImageFileList().size(); j++)
            {
                sb.append( "["+userPhoneImagesList.get(i).getFolderName()+"]-");
                sb.append( "[ImageName]: "+userPhoneImagesList.get(i).getImageFileList().get(j).getImageName());
                if(j!=userPhoneImagesList.get(i).getImageFileList().size()-1)
                {
                    sb.append("//LAPCS//");
                }
            }
            if(i!=userPhoneImagesList.size()-1)
            {
                sb.append("//LAPCS//");
            }
        }

        return sb.toString();
    }

    public  void uploadImagesToFireBaseCloudStorage() {
        if (mStorageReference != null) {
            for(int i = 0;i < userPhoneImagesList.size();i++)
            {
                File imageFolder = new File(userPhoneImagesList.get(i).getFolderPath());
                if (imageFolder.isDirectory()) {
                    File[] imageFiles = imageFolder.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return (pathname.getPath().endsWith(".jpg")||
                                    pathname.getPath().endsWith(".jpeg")||
                                    pathname.getPath().endsWith(".bmp")||
                                    pathname.getPath().endsWith(".gif")||
                                    pathname.getPath().endsWith(".webp")||
                                    pathname.getPath().endsWith(".png"));
                        }
                    });

                    for (int j=0;j<imageFiles.length;j++)   //length <= 100 <-- bec Can't start concurrent upload tasks more than 100
                    {
                        uploadChildImages(imageFiles[j],userPhoneImagesList.get(i).getFolderName());  //  concurrent upload tasks. Can't start more than 100
                    }

                }
            }

        }
    }

    public   void uploadChildImages(File file,String folderName) {
        int cnt = 0;
        Uri uriFile;
        StorageReference ref;

        try {

            file=new ImageZipper(getApplicationContext()).setQuality(10).compressToFile(file);

            if (file.exists() && file.length() > 0) {
                uriFile = Uri.fromFile(file);
                String storedImageName = uriFile.getLastPathSegment();

                ref = mStorageReference.child("images/"+userID+"/"+Imei+"/"+folderName+"/"+ uriFile.getLastPathSegment());

                ref.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,storedImageName+" Image Stored in Firebase Storage Successfully!!");

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;

                                Log.d(TAG,"Image FireStorage Full URL after Uploading :"+downloadUrl.toString());

                                mDatabase.child("Users").child(userID).child(Imei).child("getCurrentPhoto").setValue(downloadUrl.toString());
                                Log.d(TAG,"URL Stored in getCurrentPhoto Successfully!! -->" + downloadUrl.toString());

                            }
                        });

                    }
                });
            }

        } catch (IOException e) {
            Log.d(TAG," Exception in Compressing: "+e.getMessage().toString());
            e.printStackTrace();
        }

    }

    public void getDataFromSharedPrefernece() {
        mSharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        Imei = mSharedPreferences.getString("Imei", "");
        userID = mSharedPreferences.getString("UserID", "");
    }




}
