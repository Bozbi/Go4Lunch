package com.sbizzera.go4lunch.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestrofitService {

    public static Retrofit retrofit =  new Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build();

    public static <T> T createRetrofitService(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    };
}
