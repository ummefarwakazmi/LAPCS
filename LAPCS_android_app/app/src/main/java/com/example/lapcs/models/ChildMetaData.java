package com.example.lapcs.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ChildMetaData {

    @SerializedName("token_id")
    @Expose
    private String tokenId;


    public ChildMetaData() {
    }

    public ChildMetaData( String tokenId) {
        super();
        this.tokenId = tokenId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tokenId", tokenId).toString();
    }

}