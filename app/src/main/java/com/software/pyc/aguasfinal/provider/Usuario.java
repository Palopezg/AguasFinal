package com.software.pyc.aguasfinal.provider;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by pablo on 30/4/2018.
 * Clase de usuario, esta alineado con la BD
 */

public class Usuario {
    Integer _id;
    String nombre;
    String pass;
    String perfil;

    public Usuario(Integer id,String nombre, String pass, String perfil) {
        this._id = id;
        this.nombre = nombre;
        this.pass = pass;
        this.perfil = perfil;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "Nombre='" + nombre + '\'' +
                ", Password='" + pass + '\'' +
                '}';
    }

    public Usuario(Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex("_ID"));
        nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
        pass = cursor.getString(cursor.getColumnIndex("Password"));
        perfil = cursor.getString(cursor.getColumnIndex("Perfil"));
;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("_ID", _id);
        values.put("Nombre", nombre);
        values.put("Password", pass);
        values.put("Perfil", perfil);

        return values;
    }
}
