package com.minsung.examples.trafficlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by minsung on 2018-09-30.
 */

public class DataManager {
    // for 로그 태그
    private static String TAG = DataManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;



    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Data";

    private static final String KEY_BLE_ID = "id";


    public DataManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setBleID(String id) {

        editor.putString(KEY_BLE_ID, id);
        // commit changes
        editor.commit();

    }


    public String getBleID(){
        return pref.getString(KEY_BLE_ID,"");
    }


}
