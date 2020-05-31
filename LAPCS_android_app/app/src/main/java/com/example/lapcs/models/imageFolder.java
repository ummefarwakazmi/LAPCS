package com.example.lapcs.models;

import android.text.TextUtils;
import java.util.ArrayList;

public class imageFolder {

    private  String folderPath;
    private  String FolderName;
    private int numberOfPics = 0;
    ArrayList<ImageFile> imageFileList = new ArrayList<>();


    public ArrayList<ImageFile> getImageFileList() {
        return imageFileList;
    }

    public void setImageFileList(ArrayList<ImageFile> imageFileList) {
        this.imageFileList = imageFileList;
    }


    public imageFolder(){

    }

    public imageFolder(String folderPath, String folderName) {
        this.folderPath = folderPath;
        FolderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfPics() {
        return numberOfPics;
    }

    public void setNumberOfPics(int numberOfPics) {
        this.numberOfPics = numberOfPics;
    }

    public void addpics(){
        this.numberOfPics++;
    }

    @Override
    public String toString() {

        return "imageFolder{" +
                "folderPath='" + folderPath + '\'' +
                ", FolderName='" + FolderName + '\'' +
                ", numberOfPics=" + numberOfPics +
                ", ImageList=" + TextUtils.join(", ", imageFileList) +
                '}';

    }

}