package com.software.pyc.aguasfinal.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.software.pyc.aguasfinal.data.MedidaDBHelper;


/**
 * Content Provider personalizado para las medidas
 */
public class ProviderMedida extends ContentProvider{
    /**
     * Nombre de la base de datos
     */
    private static final String DATABASE_NAME = "aguas.db";
    /**
     * Versión actual de la base de datos
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Instancia global del Content Resolver
     */
    private ContentResolver resolver;
    /**
     * Instancia del administrador de BD
     */
    private MedidaDBHelper medidaDBHelper;

    @Override
    public boolean onCreate() {
        // Inicializando gestor BD
        medidaDBHelper = new MedidaDBHelper(
                getContext(),
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );

        resolver = getContext().getContentResolver();

        return true;
    }



    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {


        // Obtener base de datos
        SQLiteDatabase db = medidaDBHelper.getWritableDatabase();
        // Comparar Uri
        int match = ContractMedida.uriMatcher.match(uri);

        Cursor c;

        switch (match) {
            case ContractMedida.ALLROWS:
                // Consultando todos los registros
                c = db.query(ContractMedida.MEDIDA, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractMedida.CONTENT_URI);
                break;
            case ContractMedida.SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long idMedida = ContentUris.parseId(uri);
                c = db.query(ContractMedida.MEDIDA, projection,
                        ContractMedida.Columnas._ID + " = " + idMedida,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractMedida.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (ContractMedida.uriMatcher.match(uri)) {
            case ContractMedida.ALLROWS:
                return ContractMedida.MULTIPLE_MIME;
            case ContractMedida.SINGLE_ROW:
                return ContractMedida.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de gasto desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        if (ContractMedida.uriMatcher.match(uri) != ContractMedida.ALLROWS) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = medidaDBHelper.getWritableDatabase();
        long rowId = db.insert(ContractMedida.MEDIDA, null, contentValues);
        if (rowId > 0) {
            Uri uri_medida = ContentUris.withAppendedId(
                    ContractMedida.CONTENT_URI, rowId);
            resolver.notifyChange(uri_medida, null, false);
            return uri_medida;
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = medidaDBHelper.getWritableDatabase();

        int match = ContractMedida.uriMatcher.match(uri);
        int affected;

        switch (match) {
            case ContractMedida.ALLROWS:
                affected = db.delete(ContractMedida.MEDIDA,
                        selection,
                        selectionArgs);
                break;
            case ContractMedida.SINGLE_ROW:
                long idMedida = ContentUris.parseId(uri);
                affected = db.delete(ContractMedida.MEDIDA,
                        ContractMedida.Columnas.ID_REMOTA + "=" + idMedida
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.
                        notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Elemento gasto desconocido: " +
                        uri);
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = medidaDBHelper.getWritableDatabase();
        int affected;
        switch (ContractMedida.uriMatcher.match(uri)) {
            case ContractMedida.ALLROWS:
                affected = db.update(ContractMedida.MEDIDA, values,
                        selection, selectionArgs);
                break;
            case ContractMedida.SINGLE_ROW:
                String idMedida = uri.getPathSegments().get(1);
                affected = db.update(ContractMedida.MEDIDA, values,
                        ContractMedida.Columnas.ID_REMOTA + "=" + idMedida
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        resolver.notifyChange(uri, null, false);
        return affected;
    }

}
