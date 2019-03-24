package com.software.pyc.aguasfinal.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.data.User_OpenHelper;
import com.software.pyc.aguasfinal.provider.AdaptadorUsuarios;
import com.software.pyc.aguasfinal.provider.Usuario;

import java.util.List;

public class UsuariosActivity extends AppCompatActivity {
    AdaptadorUsuarios usuarioAdapter;
    User_OpenHelper helper = new User_OpenHelper(this,"BD1",null,1);
    private FloatingActionButton mAddButton;
    private Usuario usu=null;
    private  ListView listViewusuarios;
    private List<Usuario> listaUsuarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

         TextView tv_name = findViewById(R.id.et_name);
         TextView tv_perfil = findViewById(R.id.et_perfil);
         TextView tv_pass = findViewById(R.id.et_pass);



/*        Button eliminar = findViewById(R.id.btnEliminarUsu);
        Button editar  = findViewById(R.id.btnEditarUsu);*/
//        mAddButton =  findViewById(R.id.fabAddUsu);

        listViewusuarios = (ListView)findViewById(R.id.users_list);
        listaUsuarios = helper.getListaUsuarios(helper.ConsultarUsuTodos());

        usuarioAdapter = new AdaptadorUsuarios(getApplicationContext(), listaUsuarios);
        listViewusuarios.setAdapter(usuarioAdapter);

        listViewusuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usu = usuarioAdapter.getItem(position);
/*                LinearLayout panelEdit = findViewById(R.id.support_layout_Edit);
                LinearLayout panelDetail = findViewById(R.id.support_layout_Detail);*/
                TextView tv_name = findViewById(R.id.et_name);
                TextView tv_perfil = findViewById(R.id.et_perfil);
                TextView tv_pass = findViewById(R.id.et_pass);

/*                panelEdit.setVisibility(View.GONE);
                panelDetail.setVisibility(View.VISIBLE);*/

                tv_name.setText(usu.getNombre());
                tv_perfil.setText(usu.getPerfil());
                tv_pass.setText(usu.getPass());
            }
        });

/*        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

/*        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.deleteUsuario(usu.get_id());
                TextView tv_name = findViewById(R.id.et_name);
                TextView tv_perfil = findViewById(R.id.et_perfil);
                TextView tv_pass = findViewById(R.id.et_pass);
                tv_name.setText("");
                tv_perfil.setText("");
                tv_pass.setText("");

                listaUsuarios = helper.getListaUsuarios(helper.ConsultarUsuTodos());
                usuarioAdapter = new AdaptadorUsuarios(getApplicationContext(), listaUsuarios);
                listViewusuarios.setAdapter(usuarioAdapter);
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarUsu();
            }
        });*/


    }

    private void editarUsu() {
    }

    private void eliminarUsu() {


    }


    private void showAddScreen() {

    }


}
