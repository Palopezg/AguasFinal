package com.software.pyc.aguasfinal.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.provider.SessionManager;
import com.software.pyc.aguasfinal.data.User_OpenHelper;

public class LoginActivity extends AppCompatActivity {

    EditText nombre, password;
    Button ingresar;


    User_OpenHelper helper = new User_OpenHelper(this,"BD1",null,1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      Crea los registro en la base de Usuarios.
//        helper.mockData();

//   La clase SessionManager mantiene el registro del ususario una vez apagada la app
        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

//   Se cargan las variables de los recursos de la pantalla
        nombre   = findViewById(R.id.etUser);
        password = findViewById(R.id.etPass);
        ingresar = findViewById(R.id.btnLogin);

//   Button Login on click
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Cursor cursor = helper.ConsultarUsuPass(String.valueOf(nombre.getText()),String.valueOf(password.getText()));

//                  User y Pass OK
                    if (cursor.getCount() > 0){
                        sessionManager.createLoginSession(String.valueOf(nombre.getText()));
                        Intent i = new Intent(getApplicationContext(),ListaActivity.class);
                        startActivity(i);

//                  User y Pass Incorrectos
                    }else {
                        Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectas",Toast.LENGTH_SHORT ).show();
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }


            }
        });


    }
}
