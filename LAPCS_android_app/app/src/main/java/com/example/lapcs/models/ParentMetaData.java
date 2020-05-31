package com.example.lapcs.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParentMetaData {

    @SerializedName("token_id")
    @Expose
    public String tokenId;

    @SerializedName("imei")
    @Expose
    public String imei;

    public ParentMetaData() {
    }

    public ParentMetaData(String tokenId, String IMEI) {
        this.tokenId = tokenId;
        this.imei = IMEI;
    }

    public String getIMEI() {
        return imei;
    }

    public void setIMEI(String IMEI) {
        this.imei = IMEI;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tokenId", tokenId).append("IMEI", imei).toString();
    }

}