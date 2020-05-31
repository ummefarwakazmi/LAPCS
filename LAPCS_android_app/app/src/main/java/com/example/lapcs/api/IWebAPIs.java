package com.example.lapcs.api;

import com.example.lapcs.models.RequestNotificaton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IWebAPIs {

    @POST("fcm/send")
    Call<ResponseBody> sendPushNotification(
            @Body RequestNotificaton requestNotificaton,
            @Header("Authorization") String Authorization_key,
            @Header("Content-Type") String content_type
    );



}
