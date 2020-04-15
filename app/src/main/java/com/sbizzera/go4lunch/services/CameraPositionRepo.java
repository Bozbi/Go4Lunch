package com.sbizzera.go4lunch.services;

import com.google.android.gms.maps.model.CameraPosition;

public class CameraPositionRepo {
    private static CameraPositionRepo sCameraPositionRepo;
    private CameraPosition mLastCameraPosition;
    private CameraPosition mInitialCameraPosition;


    public static CameraPositionRepo getInstance(){
        if (sCameraPositionRepo==null){
            sCameraPositionRepo = new CameraPositionRepo();
        }
        return sCameraPositionRepo;
    }

    public void setLastCameraPosition(CameraPosition lastCameraPosition){
        mLastCameraPosition=lastCameraPosition;
        if (mInitialCameraPosition==null){
            mInitialCameraPosition = lastCameraPosition;
        }
    }

    public CameraPosition getLastCameraPosition(){
        return mLastCameraPosition;
    }

}
