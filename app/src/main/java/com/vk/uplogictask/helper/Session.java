package com.vk.uplogictask.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.vk.uplogictask.activity.LoginActivity;
import com.vk.uplogictask.activity.MainActivity;

public class Session {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREFER_NAME = "wallet";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "user_email";


    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }
    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public void logoutUser(Activity activity) {

        editor.clear();
        editor.commit();

        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();

    }

}
