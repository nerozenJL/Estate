package com.example.nerozen.casloginprocess;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Pack200;

/**
 * Created by Nerozen on 2016/5/24.
 */
public class GardenInfoList implements android.os.Parcelable {

    private Map<String, String> gardenCertainInfoMap;

    public Map<String, String> getGardenCertainInfoMap() {
        return gardenCertainInfoMap;
    }

    public GardenInfoList(Map map) {
        gardenCertainInfoMap = map;
    }

    public GardenInfoList(Parcel Parcel) {
        gardenCertainInfoMap = Parcel.readHashMap(HashMap.class.getClassLoader());
    }


    public static final Parcelable.Creator<GardenInfoList> CREATOR = new Parcelable.Creator<GardenInfoList>() {
        @Override
        public GardenInfoList createFromParcel(Parcel source) {
            GardenInfoList g = new GardenInfoList(source);
            return g;
        }

        @Override
        public GardenInfoList[] newArray(int size) {
            return new GardenInfoList[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(gardenCertainInfoMap);
    }
}
