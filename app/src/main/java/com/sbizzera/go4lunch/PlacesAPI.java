package com.sbizzera.go4lunch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PlacesAPI {

    @GET("json?location=46.1558,-1.1532&radius=1000&type=restaurant&key=AIzaSyCXKSSFW71Dz9gHO2AirNNY2AzU77LLvfE")
    Call<ResponseBody> getNearbyRestaurant ();
}
