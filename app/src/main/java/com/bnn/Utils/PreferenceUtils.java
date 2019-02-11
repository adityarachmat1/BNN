package com.bnn.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;

import com.bnn.Modal.UserMenu;

import java.util.Set;

/**
 * Created by acilryandi on 10/25/17.
 */

public class PreferenceUtils {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private PreferenceUtils(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context context){
        PreferenceUtils prefUtils = new PreferenceUtils(context);
        return prefUtils;
    }

    public boolean isLogin() {
        return this.sharedPreferences.getBoolean("is_login", false);
    }

    public void setIsLogin(boolean isLogin) {
        this.sharedPreferencesEditor.putBoolean("is_login", isLogin);
        this.sharedPreferencesEditor.commit();
    }

    public String getTokenKey() {
        return this.sharedPreferences.getString("token", null);
    }

    public void setTokenKey(String tokenKey) {
        this.sharedPreferencesEditor.putString("token", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getId() {
        return this.sharedPreferences.getString("id", null);
    }

    public void setId(String tokenKey) {
        this.sharedPreferencesEditor.putString("id", tokenKey);
        this.sharedPreferencesEditor.commit();
    }
    public String getName() {
        return this.sharedPreferences.getString("name", null);
    }

    public void setName(String tokenKey) {
        this.sharedPreferencesEditor.putString("name", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getInstansi() {
        return this.sharedPreferences.getString("instansi", null);
    }

    public void setInstansi(String tokenKey) {
        this.sharedPreferencesEditor.putString("instansi", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getRank() {
        return this.sharedPreferences.getString("rank", null);
    }

    public void setRank(String tokenKey) {
        this.sharedPreferencesEditor.putString("rank", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getImage() {
        return this.sharedPreferences.getString("image", null);
    }

    public void setImage(String tokenKey) {
        this.sharedPreferencesEditor.putString("image", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getWilayah() {
        return this.sharedPreferences.getString("wilayah", null);
    }

    public void setWilayah(String tokenKey) {
        this.sharedPreferencesEditor.putString("wilayah", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getURL() {
        return this.sharedPreferences.getString("url", null);
    }

    public void setURL(String tokenKey) {
        this.sharedPreferencesEditor.putString("url", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getMenu() {
        return this.sharedPreferences.getString("menu", null);
    }

    public void setMenu(String tokenKey) {
        this.sharedPreferencesEditor.putString("menu", tokenKey);
        this.sharedPreferencesEditor.commit();
    }

    public String getRazia() {
        return this.sharedPreferences.getString("razia_data", null);
    }

    public void setRazia(String raziaData) {
        this.sharedPreferencesEditor.putString("razia_data", raziaData);
        this.sharedPreferencesEditor.commit();
    }

    public String getUserMenu() {
        return this.sharedPreferences.getString("user_menu", null);
    }

    public void setUserMenu(String userMenu) {
        this.sharedPreferencesEditor.putString("user_menu", userMenu);
        this.sharedPreferencesEditor.commit();
    }

//    public Set<String> getUserMenu() {
//        return this.sharedPreferences.getStringSet("user_menu", new ArraySet<String>());
//    }
//
//    public void setUserMenu(Set<String> userMenu) {
//        this.sharedPreferencesEditor.putStringSet("user_menu", userMenu);
//        this.sharedPreferencesEditor.commit();
//    }

    public void clearPref() {
        this.sharedPreferencesEditor.clear().commit();
    }

}
