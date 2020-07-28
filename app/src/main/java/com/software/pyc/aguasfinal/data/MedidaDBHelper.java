package com.software.pyc.aguasfinal.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.software.pyc.aguasfinal.provider.ContractMedida;
import com.software.pyc.aguasfinal.provider.Medida;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MedidaDBHelper extends SQLiteOpenHelper {

    public MedidaDBHelper(Context context,
                String name,
                SQLiteDatabase.CursorFactory factory,
        int version) {
            super(context, name, factory, version);
        }

    public void onCreate(SQLiteDatabase database) {
        createTable(database); // Crear la tabla "medida"
    }

    /**
     * Crear tabla en la base de datos
     *
     * @param database Instancia de la base de datos
     */
    private void createTable(SQLiteDatabase database) {
        String cmd = "CREATE TABLE " + ContractMedida.MEDIDA + " (" +
                ContractMedida.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractMedida.Columnas.RUTA + " TEXT, " +
                ContractMedida.Columnas.ORDEN + " TEXT, " +
                ContractMedida.Columnas.CODIGO + " TEXT, " +
                ContractMedida.Columnas.NOMBRE + " TEXT," +
                ContractMedida.Columnas.MEDIDOR + " TEXT, " +
                ContractMedida.Columnas.PARTIDA + " TEXT, " +
                ContractMedida.Columnas.ESTADO_ANT + " TEXT, " +
                ContractMedida.Columnas.ESTADO_ACT + " TEXT," +
                ContractMedida.Columnas.FECHA_ACT + " TEXT, " +
                ContractMedida.Columnas.ACTUALIZADO + " TEXT, " +
                ContractMedida.Columnas.USUARIO + " TEXT, " +
                ContractMedida.Columnas.OBSERVACIONES + " TEXT, " +

                ContractMedida.Columnas.ID_REMOTA + " TEXT," +
                ContractMedida.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT "+ ContractMedida.ESTADO_OK+"," +
                ContractMedida.Columnas.FECHA_INSERCION + " TEXT," +
                ContractMedida.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }

    // Sobrescribe la base cada vez que actualiza, es decir se cargan todos los registros de nuevo.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        try { db.execSQL("drop table " + ContractMedida.MEDIDA); }
//        catch (SQLiteException e) { }
        //dropTable(db);
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try { db.execSQL("delete from " + ContractMedida.MEDIDA); }
        catch (SQLiteException e) { }
    }


    public void onDelete(){
        SQLiteDatabase db = this.getWritableDatabase();
        try { db.execSQL("drop table " + ContractMedida.MEDIDA); }
        catch (SQLiteException e) { }
        onCreate(db);
    }


    // Trae Todos los datos de la base
    public Cursor getAllMedidas(String orderBy, String busqueda) {

        Cursor c;
        c = this.getReadableDatabase()
                .query(
                        ContractMedida.MEDIDA,
                        null,
                        busqueda,
                        null,
                        null,
                        null,
                        orderBy);


        return c;
    }

    // Trae Todos los datos de la base
    public Cursor getAllMedidas(String orderBy, String busqueda, String limite) {

        Cursor c;
        c = this.getReadableDatabase()
                .query(
                        ContractMedida.MEDIDA,
                        null,
                        busqueda,
                        null,
                        null,
                        null,
                        orderBy,
                        limite);


        return c;
    }
    // Trae Todos los datos de la base
    public int getCantAllMedidas(String orderBy, String busqueda, String limite) {

        Cursor c;
        c = this.getReadableDatabase()
                .query(
                        ContractMedida.MEDIDA,
                        null,
                        busqueda,
                        null,
                        null,
                        null,
                        orderBy
                        );


        return c.getCount();
    }

    // Trae Todos los datos de la base
    public Cursor getRutaMedidas(String ruta, String orderBy,  String busqueda) {

        Cursor c;
        c = this.getReadableDatabase()
                .query(
                        ContractMedida.MEDIDA,
                        null,
                        busqueda,
                        null,
                        null,
                        null,
                        orderBy);
        return c;
    }

    //Pasa un cursor a cun List para usarlo en el adapter
    public List<Medida> getListaMedidas(Cursor c) {
        List<Medida> listaMedida = new ArrayList<>();

        // Si el cursor contiene datos los añadimos al List
        if (c.moveToFirst()) {
            do {

                listaMedida.add(new Medida(c.getString(0),c.getString(1),c.getString(2),
                        c.getString(3),c.getString(4),c.getString(5),
                        c.getString(6),c.getString(7),c.getString(8),
                        c.getString(9),c.getString(10),c.getString(11),c.getString(12),
                        c.getString(13)));
            } while (c.moveToNext());
        }

        return listaMedida;

    }
//MAster
    public boolean cargaEstado(String id,String estAct, String cargado, String usuario){

        Date currentTime = Calendar.getInstance().getTime();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContractMedida.Columnas.ESTADO_ACT,estAct);
        cv.put(ContractMedida.Columnas.ACTUALIZADO ,cargado);
        cv.put(ContractMedida.Columnas.PENDIENTE_INSERCION ,"1");
        cv.put(ContractMedida.Columnas.FECHA_INSERCION , String.valueOf(currentTime));
        cv.put(ContractMedida.Columnas.FECHA_ACT , String.valueOf(currentTime));
        cv.put(ContractMedida.Columnas.USUARIO , usuario);

        sqLiteDatabase.update(ContractMedida.MEDIDA, cv,"_id="+id,null);
        return true;
    }
    //MAster
    public boolean cargaEstado(String id,String estAct, String observaciones, String cargado, String usuario){

        Date currentTime = Calendar.getInstance().getTime();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContractMedida.Columnas.ESTADO_ACT,estAct);
        cv.put(ContractMedida.Columnas.OBSERVACIONES,observaciones);
        cv.put(ContractMedida.Columnas.ACTUALIZADO ,cargado);
        cv.put(ContractMedida.Columnas.PENDIENTE_INSERCION ,"1");
        cv.put(ContractMedida.Columnas.FECHA_INSERCION , String.valueOf(currentTime));
        cv.put(ContractMedida.Columnas.FECHA_ACT , String.valueOf(currentTime));
        cv.put(ContractMedida.Columnas.USUARIO , usuario);

        sqLiteDatabase.update(ContractMedida.MEDIDA, cv,"_id="+id,null);
        return true;
    }
}



