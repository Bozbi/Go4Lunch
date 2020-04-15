package com.sbizzera.go4lunch.services;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.VisibleRegion;

public class CameraPositionRepo {
    private static CameraPositionRepo sCameraPositionRepo;
    private CameraPosition mLastCameraPosition;
    private CameraPosition mInitialCameraPosition;
    private VisibleRegion mLastVisibleRegion;


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

    public void setLastVisibleRegion(VisibleRegion visibleRegion){
        mLastVisibleRegion = visibleRegion;
    }

    public VisibleRegion getLastVisibleRegion() {
        return mLastVisibleRegion;
    }
}
