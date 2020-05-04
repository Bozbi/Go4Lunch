package com.sbizzera.go4lunch.services;

import android.content.Context;

import com.sbizzera.go4lunch.R;

// TODO BOZBI STRING FORMAT C'est classe n'est pas forcément nécessaire
public class ResourcesProvider {
    private Context mContext;

    public ResourcesProvider(Context context){
        mContext = context;
    }

    public String getDialogTextNoChoice(){
        return mContext.getString(R.string.dialog_text_no_choice);
    }

    public String getDialogTextWithChoice(){
        return mContext.getString(R.string.dialog_text_with_choice);
    }

    public String getDialogTextWith() {
        return mContext.getString(R.string.dialog_text_with);
    }

    public String getDialogTextAnd() {
        return mContext.getString(R.string.dialog_text_and);
    }

    public String get_No_Schedule(){
        return mContext.getString(R.string.no_schedule_available);
    }

    public String getOpenNow() {
        return mContext.getString(R.string.open_now);
    }

    public String getClosed(){
        return  mContext.getString(R.string.closed);
    }

    public String getHasntDecided(){
        return mContext.getString(R.string.hasnt_decided);
    }

    public String getEatAT() {
        return mContext.getString(R.string.eat_at);
    }

    public String getEatsHere() {
        return mContext.getString(R.string.eats_here);
    }
}
