package com.software.pyc.aguasfinal.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract Class entre el provider y las aplicaciones
 */
public class ContractMedida {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY
            = "com.software.pyc.aguasfinal";
    /**
     * Representación de la tabla a consultar
     */
    public static final String MEDIDA = "medida";
    /**
     * Representación de la BASE de DATOS
     */
    public static final String DATABASE_NAME = "aguas.db";
    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + AUTHORITY + MEDIDA;
    /**
     * Tipo MIME que retorna la consulta de { @link CONTENT_URI}
     */
    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + MEDIDA;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + MEDIDA);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS = 1;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW = 2;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MEDIDA, ALLROWS);
        uriMatcher.addURI(AUTHORITY, MEDIDA + "/#", SINGLE_ROW);
    }

    // Valores para la columna ESTADO
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;


    /**
     * Estructura de la tabla
     */
    public static class Columnas implements BaseColumns {

        private Columnas() {
            // Sin instancias
        }

        // Columnas del servidor
        public final static String RUTA = "ruta";
        public final static String ORDEN = "orden";
        public final static String CODIGO = "codigo";
        public final static String NOMBRE = "nombre";
        public final static String MEDIDOR = "medidor";
        public final static String PARTIDA = "partida";
        public final static String ESTADO_ANT = "estado_ant";
        public final static String ESTADO_ACT = "estado_act";
        public final static String FECHA_ACT = "fecha_act";
        public final static String ACTUALIZADO = "actualizado";
        public final static String USUARIO = "usuario";

        // Columnas para la sincronizacion

        // Marca de un registro que indica si está siendo sincronizado o está intacto.
        // Para referirnos a estas dos condiciones usamos las banderas ESTADO_OK y ESTADO_SYNC.
        public static final String ESTADO = "estado";

        // Es necesario tener una copia de la llave primaria que tiene el registro local en la base de datos del servidor.
        // Esto permite mantener una actualización basada en una sola referencia.
        public static final String ID_REMOTA = "idRemota";

        // Fecha en que se bajo el dato del servidor
        public final static String FECHA_INSERCION = "fecha_insercion";

        // Marca para saber que hay que insertarlo en el servidor
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";



    }
}
