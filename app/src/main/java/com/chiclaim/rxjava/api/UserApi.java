package com.chiclaim.rxjava.api;


import com.chiclaim.rxjava.model.AuthToken;

import retrofit.client.Response;
import retrofit.http.GET;
import rx.Observable;

public interface UserApi {

    @GET("/token")
    AuthToken refreshToken();

    @GET("/userinfo")
    Observable<Response> getUserInfo();

    //wrong path
    @GET("/userinfo1")
    Observable<Response> getUserInfo1();
}