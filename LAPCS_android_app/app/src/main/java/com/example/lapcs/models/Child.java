package com.example.lapcs.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


public class Child implements Serializable {

    @SerializedName("child_name")
    @Expose
    public String childName;

    @SerializedName("child_device")
    @Expose
    public String childDevice;

    /**
     * No args constructor for use in serialization
     *
     */
    public Child() {
        this.childName = "";
        this.childDevice = "";
    }

    /**
     *
     * @param childDevice
     * @param childName
     */
    public Child(String childName, String childDevice) {
        super();
        this.childName = childName;
        this.childDevice = childDevice;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildDevice() {
        return childDevice;
    }

    public void setChildDevice(String childDevice) {
        this.childDevice = childDevice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("childName", childName).append("childDevice", childDevice).toString();
    }

    // Overriding equals() to compare two Child objects
    @Override
    public boolean equals(@Nullable Object o) {

        /* Check if o is an instance of Child or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Child)) {
            return false;
        }

        // typecast o to Child so that we can compare data members
        Child c = (Child) o;

        // Compare the data members and return accordingly
        return this.getChildDevice().equals(c.childDevice);

    }

}