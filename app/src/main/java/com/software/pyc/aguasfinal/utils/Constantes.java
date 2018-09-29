package com.software.pyc.aguasfinal.utils;

/**
 * Constantes
 */
public class Constantes {

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta característica.
     */
    private static final String PUERTO_HOST = ":80";

    /**
     * Dirección IP de genymotion o AVD
     */
    private static final String IP = "http://aguassync.000webhostapp.com";

    /**
     * URLs del Web Service
     */
    public static final String GET_URL = IP + PUERTO_HOST + "/web/obtener_medida.php";
    public static final String UPDATE_URL = IP + PUERTO_HOST + "/web/update_medida.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_MEDIDA = "idMedida";
    public static final String RUTA = "ruta";
    public static final String ORDEN = "orden";
    public static final String CODIGO = "codigo";
    public static final String NOMBRE = "nombre";
    public static final String MEDIDOR = "medidor";
    public static final String PARTIDA = "partida";
    public static final String ESTADO_ANT = "estadoAnterior";
    public static final String ESTADO_ACT = "estadoActual";
    public static final String FECHA_ACT = "fechaActualizacion";
    public static final String USUARIO = "usuario";

    public static final String ESTADO = "estado";
    public static final String MENSAJE = "mensaje";
    public static final String MEDIDA = "medida";

    /**
     * Códigos del campo { @link ESTADO}
     */
    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.software.pyc.aguasfinal.account";


}

