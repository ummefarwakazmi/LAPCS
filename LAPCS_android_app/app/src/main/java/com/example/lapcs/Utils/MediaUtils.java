package com.example.lapcs.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.models.ImageFile;
import com.example.lapcs.models.imageFolder;

import java.util.ArrayList;

import static com.example.lapcs.AppConsts.TAG;


public class MediaUtils {

    public static  ArrayList<imageFolder> getPicturePaths(Context context)
    {
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = context.getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();

                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";

                if (!picPaths.contains(folderpaths))
                {
                    picPaths.add(folderpaths);
                    folds.setFolderPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.addpics();
                    folds.getImageFileList().add(new ImageFile(name,datapath));
                    picFolders.add(folds);

                }
                else
                {

                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getFolderPath().equals(folderpaths)){
                            picFolders.get(i).addpics();
                            picFolders.get(i).getImageFileList().add(new ImageFile(name,datapath));
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++)
        {
            Log.d(TAG,"Folder Name: "+picFolders.get(i).getFolderName());
            Log.d(TAG,"Folder Path = "+picFolders.get(i).getFolderPath());
            Log.d(TAG,"Number of Pics = "+picFolders.get(i).getNumberOfPics());
            Log.d(TAG,"\n==================================================");
            for(int j = 0; j < picFolders.get(i).getImageFileList().size(); j++){
                Log.d(TAG,"Image Name: "+picFolders.get(i).getImageFileList().get(j).getImageName());
                Log.d(TAG,"Image Path = "+picFolders.get(i).getImageFileList().get(j).getImagePath());
            }
        }

        return picFolders;
    }
    
}
