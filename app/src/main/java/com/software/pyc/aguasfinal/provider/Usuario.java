package com.software.pyc.aguasfinal.provider;

/**
 * Created by pablo on 30/4/2018.
 * Clase de usuario, esta alineado con la BD
 */

public class Usuario {
    String nombre;
    String pass;
    String perfil;

    public Usuario(String nombre, String pass, String perfil) {
        this.nombre = nombre;
        this.pass = pass;
        this.perfil = perfil;
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
}
