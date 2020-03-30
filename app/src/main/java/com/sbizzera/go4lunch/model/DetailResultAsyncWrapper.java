package com.sbizzera.go4lunch.model;

import com.sbizzera.go4lunch.model.places_place_details_models.DetailsResponse;

public class DetailResultAsyncWrapper {
    private Boolean alreadyEnqueued;
    private DetailsResponse.DetailResult placeDetail;

    public DetailResultAsyncWrapper(Boolean alreadyEnqueued, DetailsResponse.DetailResult placeDetail) {
        this.alreadyEnqueued = alreadyEnqueued;
        this.placeDetail = placeDetail;
    }

    public Boolean getAlreadyEnqueued() {
        return alreadyEnqueued;
    }

    public void setAlreadyEnqueued(Boolean alreadyEnqueued) {
        this.alreadyEnqueued = alreadyEnqueued;
    }

    public DetailsResponse.DetailResult getPlaceDetail() {
        return placeDetail;
    }

    public void setPlaceDetail(DetailsResponse.DetailResult placeDetail) {
        this.placeDetail = placeDetail;
    }
}
