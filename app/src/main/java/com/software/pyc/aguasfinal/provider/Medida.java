package com.software.pyc.aguasfinal.provider;

import android.content.ContentValues;


/**
 * Created by pablo on 15/7/2018.
 * Clase Medida se instancia con los datos de la BD
 * -----No esta completamente alineada a la BD
 */

public class Medida {
    private final String observaciones;
    private String id, ruta, orden, codigo, medidor, estadoAnterior, estadoActual, idRemota;
    private String usuario;
    private String nombre, partida;
    private String fechaActualizacion;
    private String actualizado;

    public String getIdRemota() {
        return idRemota;
    }

    public void setIdRemota(String idRemota) {
        this.idRemota = idRemota;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMedidor() {
        return medidor;
    }

    public void setMedidor(String medidor) {
        this.medidor = medidor;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getActualizado() {
        return actualizado;
    }

    public void setActualizado(String actualizado) {
        this.actualizado = actualizado;
    }

    public Medida(String id, String ruta, String orden, String codigo, String nombre, String medidor,  String partida,  String estadoAnterior, String estadoActual, String fechaActualizacion, String actualizado, String usuario, String observaciones, String idRemota) {
        this.id = id;
        this.ruta = ruta;
        this.orden = orden;
        this.codigo = codigo;
        this.medidor = medidor;
        this.estadoAnterior = estadoAnterior;
        this.estadoActual = estadoActual;
        this.usuario = usuario;
        this.nombre = nombre;
        this.partida = partida;
        this.observaciones = observaciones;
        this.fechaActualizacion = fechaActualizacion;
        this.actualizado = actualizado;
        this.idRemota = idRemota;
    }



    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(ContractMedida.Columnas._ID,id );
        values.put(ContractMedida.Columnas.RUTA,ruta );
        values.put(ContractMedida.Columnas.ORDEN,orden );
        values.put(ContractMedida.Columnas.CODIGO,codigo );
        values.put(ContractMedida.Columnas.NOMBRE,nombre );
        values.put(ContractMedida.Columnas.MEDIDOR,medidor );
        values.put(ContractMedida.Columnas.PARTIDA,partida );
        values.put(ContractMedida.Columnas.ESTADO_ANT,estadoAnterior );
        values.put(ContractMedida.Columnas.ESTADO_ACT,estadoActual );
        values.put(ContractMedida.Columnas.FECHA_ACT,fechaActualizacion );
        values.put(ContractMedida.Columnas.ACTUALIZADO,actualizado );
        values.put(ContractMedida.Columnas.OBSERVACIONES,observaciones );
        values.put(ContractMedida.Columnas.USUARIO,usuario );

        return values;
    }
}
