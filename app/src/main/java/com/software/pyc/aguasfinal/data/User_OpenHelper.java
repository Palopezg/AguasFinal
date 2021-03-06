package com.software.pyc.aguasfinal.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.software.pyc.aguasfinal.provider.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pablo on 29/4/2018.
 * Open Helper de la tabla user.
 */

public class User_OpenHelper extends SQLiteOpenHelper {

    public User_OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="create table usuarios(_ID integer primary key autoincrement, Nombre text, Password text, Perfil text)";
        db.execSQL(query);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void mockData(){

        this.insertarRegistro(1,"mmasetti","mmasetti","oper");
        this.insertarRegistro(2,"mcontreras","mcontreras","oper");
        this.insertarRegistro(3,"bbalbiano","bbalbiano","oper");
        this.insertarRegistro(4,"admin","admin","admin");
    }

    // Metodo que permite insertar registros en la tabla usuarios
    public void insertarRegistro(Integer id, String nombre, String pass, String perfil){
        ContentValues valores = new ContentValues();
        valores.put("_ID", id);
        valores.put("Nombre", nombre);
        valores.put("Password", pass);
        valores.put("Perfil", perfil);
        this.getWritableDatabase().insert("usuarios",null,valores);
    }

    // Metodo que permite  validar si el usuario existe
    public Cursor ConsultarUsuPass(String usu, String pass) throws SQLException{
      Cursor mcursor;
      mcursor =  this.getReadableDatabase().query(
              "usuarios",new String[]{"_ID, Nombre, Password, Perfil"},
              "Nombre like '"+usu+"' and Password like '"+pass+"'",
              null,
              null,
              null,
              null);



      return mcursor;
    }

    // Metodo que permite mostrar todos los usuaios
    public Cursor ConsultarUsuTodos() throws SQLException{
        Cursor mcursor;
        mcursor =  this.getReadableDatabase().query(
                "usuarios",new String[]{"_ID, Nombre, Password, Perfil"},
                null,
                null,
                null,
                null,
                null);



        return mcursor;
    }

    public  List<Usuario> getListaUsuarios(Cursor c) {
        List<Usuario> listaUsuarios = new ArrayList<>();

        // Si el cursor contiene datos los añadimos al List

        if (c.moveToFirst()) {
            do {
                listaUsuarios.add(new Usuario(c.getInt(0),c.getString(1),c.getString(2),c.getString(3)));
            } while (c.moveToNext());
        }

        return listaUsuarios;
    }

    public long saveUsuario(Usuario usuario) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                "usuarios",
                null,
                usuario.toContentValues());

    }

    public Cursor getUsuarioById(Integer usuarioId) {
        Cursor c = getReadableDatabase().query(
                "usuarios",
                null,
                "_ID" + " LIKE ?",
                new String[]{String.valueOf(usuarioId)},
                null,
                null,
                null);
        return c;
    }

    public int deleteUsuario(Integer usuarioId) {
        return getWritableDatabase().delete(
                "usuarios",
                "_ID" + " LIKE ?",
                new String[]{String.valueOf(usuarioId)});
    }

    public int updateLawyer(Usuario usuario, Integer usuarioId) {
        return getWritableDatabase().update(
                "usuarios",
                usuario.toContentValues(),
                "_ID" + " LIKE ?",
                new String[]{String.valueOf(usuarioId)}
        );
    }

    // Metodo que permite abrir la BD
    public void abrir(){
        this.getWritableDatabase();
    }

    // Metodo que permite cerrar la BD
    public void cerrar(){
        this.close();
    }




}
