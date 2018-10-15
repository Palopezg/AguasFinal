package com.software.pyc.aguasfinal.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.provider.ContractMedida;
import com.software.pyc.aguasfinal.utils.Constantes;
import com.software.pyc.aguasfinal.utils.LogMedida;
import com.software.pyc.aguasfinal.utils.Utilidades;
import com.software.pyc.aguasfinal.web.Medida;
import com.software.pyc.aguasfinal.web.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();
    Context context;

    LogMedida logMedida = new LogMedida();



    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContractMedida.Columnas._ID,
            ContractMedida.Columnas.RUTA,
            ContractMedida.Columnas.ORDEN,
            ContractMedida.Columnas.CODIGO,
            ContractMedida.Columnas.NOMBRE,
            ContractMedida.Columnas.MEDIDOR,
            ContractMedida.Columnas.PARTIDA,
            ContractMedida.Columnas.ESTADO_ANT,
            ContractMedida.Columnas.ESTADO_ACT,
            ContractMedida.Columnas.FECHA_ACT,
            ContractMedida.Columnas.USUARIO,
            ContractMedida.Columnas.ID_REMOTA,
            ContractMedida.Columnas.OBSERVACIONES,
            ContractMedida.Columnas.ACTUALIZADO


    };

/*    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MONTO = 2;
    public static final int COLUMNA_ETIQUETA = 3;
    public static final int COLUMNA_FECHA = 4;
    public static final int COLUMNA_DESCRIPCION = 5;*/

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
        this.context = context;
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static void inicializarSyncAdapter(Context context) {
        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "onPerformSync()...");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {
            realizarSincronizacionLocal(syncResult);
        } else {
            realizarSincronizacionRemota();
        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGet(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.d(TAG, error.getMessage());
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                                //Log.d(TAG, "networkResponse Volley: " +error.networkResponse.toString());
                            }
                        }
                )
        );
    }

    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos las medidas.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);

            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocales(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,"Actualizando el servidor...",context);

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");
        LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,"Se encontraron " + c.getCount() + " registros sucios.",context);

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(Utilidades.COLUMNA_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.UPDATE_URL,
                                Utilidades.deCursorAJSONObject(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,"Error Volley: " + error.getMessage(),context);
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        } else {
            Log.i(TAG, "No se requiere sincronización");
            LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,"No se requiere sincronización",context);
            //Toast.makeText(this.context,"No se requiere sincronizacion",Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSucios() {
        Uri uri = ContractMedida.CONTENT_URI;
        String selection = ContractMedida.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractMedida.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractMedida.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION, selection, selectionArgs, null);
    }

    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion() {
        Uri uri = ContractMedida.CONTENT_URI;
        String selection = ContractMedida.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractMedida.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractMedida.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractMedida.Columnas.ESTADO, ContractMedida.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción:" + results);
        LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,"Registros puestos en cola de inserción:" + results,context);
    }

    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param idRemota id remota
     */
    private void finalizarActualizacion(String idRemota, int idLocal) {
        Uri uri = ContractMedida.CONTENT_URI;
        String selection = ContractMedida.Columnas._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractMedida.Columnas.PENDIENTE_INSERCION, "0");
        v.put(ContractMedida.Columnas.ESTADO, ContractMedida.ESTADO_OK);
        v.put(ContractMedida.Columnas.ID_REMOTA, idRemota);
        v.put(ContractMedida.Columnas.ACTUALIZADO,Constantes.SYCRONIZADO);

        resolver.update(uri, v, selection, selectionArgs);




    }

    /**
     * Procesa los diferentes tipos de respuesta obtenidos del servidor
     *
     * @param response Respuesta en formato Json
     */
    public void procesarRespuestaInsert(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);
            // Obtener identificador del nuevo registro creado en el servidor
            String idRemota = response.getString(Constantes.ID_MEDIDA);

            switch (estado) {
                case Constantes.SUCCESS:
                    Log.i(TAG, mensaje);
                    LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,mensaje+" idRemota:"+idRemota,context);
                    finalizarActualizacion(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG, mensaje);
                    LogMedida.grabaLog(Constantes.LOG_SUBIR_TABLA,mensaje+" idRemota:"+idRemota,context);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {

        JSONArray medida = null;

        try {
            // Obtener array "medida"
            medida = response.getJSONArray(Constantes.MEDIDA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Medida[] res = gson.fromJson(medida != null ? medida.toString() : null, Medida[].class);
        List<Medida> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, Medida> expenseMap = new HashMap<>();
        for (Medida e : data) {
            expenseMap.put(e.idMedida, e);

        }

        // Consultar registros remotos actuales
        Uri uri = ContractMedida.CONTENT_URI;
        String select = ContractMedida.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");
        LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Se encontraron " + c.getCount() + " registros locales.",context);



        // Encontrar datos obsoletos
        String id, idRemota, actualizado;
        String ruta, orden, codigo, nombre, medidor, partida, estado_act, estado_ant, fecha_act, usuario, observaciones;

        // Datos de la base local
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(Utilidades.COLUMNA_ID);
            ruta = c.getString(Utilidades.COLUMNA_RUTA);
            orden = c.getString(Utilidades.COLUMNA_ORDEN);
            codigo = c.getString(Utilidades.COLUMNA_CODIGO);
            nombre = c.getString(Utilidades.COLUMNA_NOMBRE);
            medidor = c.getString(Utilidades.COLUMNA_MEDIDOR);
            partida = c.getString(Utilidades.COLUMNA_PARTIDA);
            estado_ant = c.getString(Utilidades.COLUMNA_ESTADO_ANT);
            estado_act = c.getString(Utilidades.COLUMNA_ESTADO_ACT);
            fecha_act = c.getString(Utilidades.COLUMNA_FECHA_ACT);
            usuario = c.getString(Utilidades.COLUMNA_USUARIO);
            observaciones = c.getString(Utilidades.COLUMNA_OBSERVACIONES);
            idRemota = c.getString(Utilidades.COLUMNA_ID_REMOTA);
            actualizado = c.getString(Utilidades.COLUMNA_ACTUALIZADO);



            Medida match = expenseMap.get(idRemota);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(idRemota);

                Uri existingUri = ContractMedida.CONTENT_URI.buildUpon()
                        .appendPath(idRemota).build();


                // Comprobar si la medida necesita ser actualizado

                boolean b =  match.ruta != null && !match.ruta.equals(ruta);
                boolean b1 = match.orden != null && !match.orden.equals(orden);
                boolean b2 = match.codigo != null && !match.codigo.equals(codigo);
                boolean b3 = match.nombre != null && !match.nombre.equals(nombre);
                boolean b4 =  match.medidor != null && !match.medidor.equals(medidor);
                boolean b5 = match.partida != null && !match.partida.equals(partida);
                boolean b6 = match.estadoAnterior != null && !match.estadoAnterior.equals(estado_ant);
                boolean b7 = match.estadoActual != null && !match.estadoActual.equals(estado_act);
                boolean b8 =  match.fechaActualizacion != null && !match.fechaActualizacion.equals(fecha_act);
                boolean b9 = match.usuario != null && !match.usuario.equals(usuario);
                boolean b10 = match.observaciones != null && !match.observaciones.equals(observaciones);


                if (match.estadoActual != null && !match.estadoActual.equalsIgnoreCase("0")){
                    match.actualizado = Constantes.SYCRONIZADO;
                }


//                if (b || b1 || b2 || b3|| b4 || b5 || b6|| b7 || b8 || b9 || b10) {
              //  if (estado_act.equalsIgnoreCase("0") || estado_act == null) {
                if (actualizado == null || actualizado.equalsIgnoreCase("")) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);
                    LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Programando actualización de: " + existingUri,context);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractMedida.Columnas.ID_REMOTA, match.idMedida)
                            .withValue(ContractMedida.Columnas.RUTA, match.ruta)
                            .withValue(ContractMedida.Columnas.ORDEN, match.orden)
                            .withValue(ContractMedida.Columnas.CODIGO, match.codigo)
                            .withValue(ContractMedida.Columnas.NOMBRE, match.nombre)
                            .withValue(ContractMedida.Columnas.MEDIDOR, match.medidor)
                            .withValue(ContractMedida.Columnas.PARTIDA, match.partida)
                            .withValue(ContractMedida.Columnas.ESTADO_ANT, match.estadoAnterior)
                            .withValue(ContractMedida.Columnas.ESTADO_ACT, match.estadoActual)
                            .withValue(ContractMedida.Columnas.FECHA_ACT, match.fechaActualizacion)
                            .withValue(ContractMedida.Columnas.USUARIO, match.usuario)
                            .withValue(ContractMedida.Columnas.OBSERVACIONES, match.observaciones)
                            .withValue(ContractMedida.Columnas.ACTUALIZADO, match.actualizado)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No se actualizan registros que fueron cargados por el usuario: " + existingUri);
                    LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"No se actualizan registros que fueron cargados por el usuario: " + existingUri,context);
                }
            } else {
                if (estado_act.equalsIgnoreCase("0") || estado_act == null) {
                    // Debido a que la entrada no existe, es removida de la base de datos
                    Uri deleteUri = ContractMedida.CONTENT_URI.buildUpon()
                            .appendPath(idRemota).build();
                    Log.i(TAG, "Programando eliminación de: " + deleteUri);
                    LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Programando eliminación de: " + deleteUri,context);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }
            }
        }
        c.close();

        // Insertar items resultantes
        for (Medida e : expenseMap.values()) {

            if (e.estadoActual != null && !e.estadoActual.equalsIgnoreCase("0")){
                e.actualizado = Constantes.SYCRONIZADO;
            }
            Log.i(TAG, "Programando inserción de: " + e.idMedida);
            LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Programando inserción de: " + e.idMedida,context);
            ops.add(ContentProviderOperation.newInsert(ContractMedida.CONTENT_URI)
                    .withValue(ContractMedida.Columnas.ID_REMOTA, e.idMedida)
                    .withValue(ContractMedida.Columnas.RUTA, e.ruta)
                    .withValue(ContractMedida.Columnas.ORDEN, e.orden)
                    .withValue(ContractMedida.Columnas.CODIGO, e.codigo)
                    .withValue(ContractMedida.Columnas.NOMBRE, e.nombre)
                    .withValue(ContractMedida.Columnas.MEDIDOR, e.medidor)
                    .withValue(ContractMedida.Columnas.PARTIDA, e.partida)
                    .withValue(ContractMedida.Columnas.ESTADO_ANT, e.estadoAnterior)
                    .withValue(ContractMedida.Columnas.ESTADO_ACT, e.estadoActual)
                    .withValue(ContractMedida.Columnas.FECHA_ACT, e.fechaActualizacion)
                    .withValue(ContractMedida.Columnas.USUARIO, e.usuario)
                    .withValue(ContractMedida.Columnas.OBSERVACIONES, e.observaciones)
                    .withValue(ContractMedida.Columnas.ACTUALIZADO, e.actualizado)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Aplicando operaciones...",context);
            try {
                resolver.applyBatch(ContractMedida.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractMedida.CONTENT_URI,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");
            LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,
                    "Sincronización finalizada. Cantidad Inserts: "+String.valueOf(syncResult.stats.numInserts)
                    +" Cantidad Updates: "+String.valueOf(syncResult.stats.numUpdates)
                    +" Cantidad Deltes: "+String.valueOf(syncResult.stats.numDeletes),context);

        } else {
            Log.i(TAG, "No se requiere sincronización");
            LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"No se requiere sincronización",context);
        }

    }

    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload) {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }

    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    public static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Crear cuenta por defecto
        Account newAccount = new Account(
                context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;

        }
        Log.i(TAG, "Cuenta de usuario obtenida.");
        return newAccount;
    }



}
