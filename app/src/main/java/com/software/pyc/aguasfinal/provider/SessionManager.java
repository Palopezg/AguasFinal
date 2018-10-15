package com.software.pyc.aguasfinal.provider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.software.pyc.aguasfinal.ui.ListaActivity;
import com.software.pyc.aguasfinal.ui.LoginActivity;


import java.util.HashMap;

/**
 * Created by pablo on 29/7/2018.
 * Clase que guarda los datos de session, usuario y perfil
 *
 */

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "reg";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "Name";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PERFIL = "perfil";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String Name, String perfil){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, Name);
        editor.putString(KEY_PERFIL, perfil);


        editor.commit();
    }


    public void checkLogin(){
        if(this.isLoggedIn()){
            Intent i = new Intent(_context, ListaActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }


   public String getUser(){
        return pref.getString(KEY_NAME, null);
   }


    public String getPerfil(){
        return pref.getString(KEY_PERFIL, null);
    }
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PERFIL, pref.getString(KEY_PERFIL, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}

