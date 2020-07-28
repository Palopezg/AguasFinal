package com.software.pyc.aguasfinal.utils;


import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Utilidades {
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_RUTA = 1;
    public static final int COLUMNA_ORDEN = 2;
    public static final int COLUMNA_CODIGO = 3;
    public static final int COLUMNA_NOMBRE = 4;
    public static final int COLUMNA_MEDIDOR = 5;
    public static final int COLUMNA_PARTIDA = 6;
    public static final int COLUMNA_ESTADO_ANT = 7;
    public static final int COLUMNA_ESTADO_ACT = 8;
    public static final int COLUMNA_FECHA_ACT= 9;
    public static final int COLUMNA_USUARIO = 10;
    public static final int COLUMNA_ID_REMOTA = 11;
    public static final int COLUMNA_OBSERVACIONES = 12;
    public static final int COLUMNA_ACTUALIZADO = 13;


    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Copia los datos de una medida almacenados en un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObject(Cursor c) {
        JSONObject jObject = new JSONObject();

        String estado_act;
        String fecha_act;
        String usuario;
        String idRemota;
        String observaciones;

        estado_act = c.getString(COLUMNA_ESTADO_ACT);
        fecha_act = c.getString(COLUMNA_FECHA_ACT);
        usuario = c.getString(COLUMNA_USUARIO);
        idRemota = c.getString(COLUMNA_ID_REMOTA);
        observaciones = c.getString(COLUMNA_OBSERVACIONES);

        try {

            jObject.put(Constantes.ESTADO_ACT, estado_act);
            jObject.put(Constantes.FECHA_ACT, fecha_act);
            jObject.put(Constantes.USUARIO, usuario);
            jObject.put(Constantes.ID_MEDIDA, idRemota);
            jObject.put(Constantes.OBSERVACIONES, observaciones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Cursor a JSONObject", String.valueOf(jObject));

        return jObject;
    }
}

