package com.example.lapcs.models;

public class ImageFile {

    private  String imageName;
    private  String imagePath;

    public ImageFile()
    {

    }

    public ImageFile(String imageName, String imagePath) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageName='" + imageName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }


}
