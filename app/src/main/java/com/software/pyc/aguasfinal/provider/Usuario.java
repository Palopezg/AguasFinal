package com.software.pyc.aguasfinal.provider;

/**
 * Created by pablo on 30/4/2018.
 */

public class Usuario {
    String nombre;
    String pass;

    public Usuario(String nombre, String pass) {
        this.nombre = nombre;
        this.pass = pass;
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
}
