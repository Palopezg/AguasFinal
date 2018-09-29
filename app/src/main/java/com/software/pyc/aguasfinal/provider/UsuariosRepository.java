package com.software.pyc.aguasfinal.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by pablo on 30/4/2018.
 */

public class UsuariosRepository {

    private static UsuariosRepository repository = new UsuariosRepository();
    private HashMap<String, Usuario> usuarioHashMap = new HashMap<>();


    public static UsuariosRepository getInstance() {
        return repository;
    }

    private UsuariosRepository() {


        saveUsuario(new Usuario("pablo","1234"));
        saveUsuario(new Usuario("vero","1234"));
        saveUsuario(new Usuario("crii","1234"));
        saveUsuario(new Usuario("caro","1234"));
    }

    private void saveUsuario(Usuario usuario) {

        usuarioHashMap.put(usuario.getNombre(),usuario);
    }

    public List<Usuario> getUsuarios() {

        return new ArrayList<>(usuarioHashMap.values());
    }



}
