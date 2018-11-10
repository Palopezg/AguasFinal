package com.software.pyc.aguasfinal.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.provider.*;
import com.software.pyc.aguasfinal.data.MedidaDBHelper;
import com.software.pyc.aguasfinal.sync.SyncAdapter;
import com.software.pyc.aguasfinal.utils.Constantes;
import com.software.pyc.aguasfinal.utils.LogMedida;
import java.util.List;

public class ListaActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CargaMedida.OnSimpleDialogListener {

    private static final String TAG = ListaActivity.class.getSimpleName();

    Cursor c;
    String rutaSeleccionada;
    String opcionBusqueda;
    View viewAnterior = null;
    Medida currentMedida = null;
    Toolbar toolbar;
    Boolean firstTime = false;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AdaptadorDeMedida adapter;
    ProgressDialog progressDoalog;

    ConsultaMedida consulta = ConsultaMedida.getInstance();

    MedidaDBHelper medidaOpenHelper = new MedidaDBHelper(this,ContractMedida.DATABASE_NAME,null,1);
    int posicionList =1;
    int posElegido=-1;
    Boolean flag=true;


    public void onPossitiveButtonClick() {
        //medidaAdapter = new MedidaAdapter(getApplicationContext(), medidaOpenHelper.getListaMedidas(medidaOpenHelper.getAllMedidas(orderBy)));
        Toast.makeText(getApplicationContext(),"OnClickListener" ,Toast.LENGTH_SHORT).show();
        //medidaAdapter.notifyDataSetChanged();
        //listMedida = medidaOpenHelper.getListaMedidas(medidaOpenHelper.getAllMedidas());
        actualizarDatos();

    }

    public void actualizarDatos(){

        c = medidaOpenHelper.getAllMedidas(consulta.getOrderBy(),consulta.getWhere());
        consulta.setFin(medidaOpenHelper.getCantAllMedidas(consulta.getOrderBy(),consulta.getWhere(),consulta.getLimite()));
        adapter.swapCursor(c);

        if (flag) {
            recyclerView.setAdapter(adapter);
            flag=false;
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        progressDoalog = new ProgressDialog(ListaActivity.this);

//  Asignacion del toolbar
        toolbar = findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(toolbar);


        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String usuarioSesion = sessionManager.getUser();

        TextView usu = findViewById(R.id.tvUser);
        usu.setText(sessionManager.getUser());



        recyclerView = findViewById(R.id.reciclador);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptadorDeMedida(this);
        adapter.hasStableIds();


        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {


                        List<Medida> listaMedida = adapter.getLsMedida();
                        currentMedida = listaMedida.get(position);

                        posElegido = position;

                        CargaMedida d = new CargaMedida();
                        d.Carga(currentMedida);
                        d.show(getFragmentManager(),"");


/*                        if (viewAnterior != null) {
                            viewAnterior.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.borde_item_black));
                            viewAnterior.setElevation(0);
                        }
                        viewAnterior = view;

                        view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.borde_item));
                        view.setElevation(8);*/


                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        //Ordenamientos
        TextView orderByOrden = findViewById(R.id.lvOrden);
        TextView orderByCodigo = findViewById(R.id.lvCodigo);
        TextView orderByNombre = findViewById(R.id.lvNombre);
        TextView orderByMedidor = findViewById(R.id.lvMedidor);
        TextView orderByPartida = findViewById(R.id.lvPartida);
        TextView orderByAnt = findViewById(R.id.lvAnt);
        TextView orderByAct = findViewById(R.id.lvAct);

        orderByOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.ORDEN);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.CODIGO);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.NOMBRE);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByMedidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.MEDIDOR);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.PARTIDA);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.ESTADO_ANT);
                posicionList=0;
                actualizarDatos();
            }
        });
        orderByAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                    consulta.setOrderByMetodo("asc");
                }else{
                    consulta.setOrderByMetodo("desc");
                };

                consulta.setOrderBy(ContractMedida.Columnas.ESTADO_ACT);
                posicionList=0;
                actualizarDatos();
            }
        });



        //Implementacion del spinner ruta
        Spinner spinner = findViewById(R.id.spRuta);
        String[] ruta_spinner = {"Todas","Ruta 1","Ruta 2","Ruta 3","Ruta 4"};

        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, ruta_spinner));
        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)

            {
                String ordenSel = String.valueOf(adapterView.getItemAtPosition(pos));
                switch (ordenSel){
                    case "Todas":
                        ordenSel="*";
                        break;
                    case "Ruta 1":
                        ordenSel="1";
                        break;
                    case "Ruta 2":
                        ordenSel="2";
                        break;
                    case "Ruta 3":
                        ordenSel="3";
                        break;
                    case "Ruta 4":
                        ordenSel="4";
                        break;
                }

                if (ordenSel.equalsIgnoreCase("*")){
                    rutaSeleccionada = "1=1";
                }else {
                    rutaSeleccionada = ContractMedida.Columnas.RUTA + "=" + String.valueOf(ordenSel);
                }

                consulta.setRuta(rutaSeleccionada);
                actualizarDatos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });


        //Implementacion del spinner busqueda
        ImageButton btnBusqueda = findViewById(R.id.btnBusqueda);
        final EditText etbusqueda = findViewById(R.id.etBusqueda2);

        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busquedaValor = etbusqueda.getText().toString();


                opcionBusqueda= ContractMedida.Columnas.NOMBRE+" like '%"+busquedaValor+"%' or "
                              +ContractMedida.Columnas.PARTIDA+" like '%"+busquedaValor+"%' or "
                              +ContractMedida.Columnas.CODIGO+" like '%"+busquedaValor+"%' or "
                              +ContractMedida.Columnas.MEDIDOR+" like '%"+busquedaValor+"%'";

                Log.i(TAG, opcionBusqueda);
                consulta.addWhereAnd(opcionBusqueda);
                consulta.setOffset(1);

                actualizarDatos();

            }
        });


    }


    private void logOut(){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutUser();
    }

    //  Instanciacion del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String perfilUsuario = sessionManager.getPerfil();
        if (perfilUsuario.equalsIgnoreCase("admin")){
            getMenuInflater().inflate(R.menu.menu_admin, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String perfilUsuario = sessionManager.getPerfil();


        if (id == R.id.dropTable) {
            if (perfilUsuario.equalsIgnoreCase("admin")) {
                if (LogMedida.copiaBD()) {
                    dropTable();
                    actualizarDatos();
                }else{
                    Toast.makeText(getApplicationContext(),"Error al realizar el respaldo de la base...",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Solo el administrador puede borrar la tabla.",Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.logout) {
            logOut();
        }

        if (id == R.id.itemUsuarios) {
            // agregar la llamada al activity para modificar/crear usuarios
        }


        if (id == R.id.action_sync) {
            if (!compruebaConexion(this)) {
                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            } else {
//                medidaOpenHelper.onDelete();
//                Log.i(TAG, "medida borrada.");

                SyncAdapter.sincronizarAhora(this, false);

                Log.i(TAG, "sincronizacion llamada");
                firstTime = true;

                progressDoalog = new ProgressDialog(ListaActivity.this);
                progressDoalog.setMax(100);
                progressDoalog.setMessage("Descargando tabla....");
                progressDoalog.setTitle("Recuperando datos del servidor");
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDoalog.setCancelable(false);
                progressDoalog.show();

            }

            return true;
        }
        if (id == R.id.action_sync_local) {
            if (!compruebaConexion(this)) {
                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getApplicationContext(),"Subir cambios al servidor",Toast.LENGTH_SHORT).show();
                SyncAdapter.sincronizarAhora(this, true);


                firstTime = true;


                progressDoalog.setMax(100);
                progressDoalog.setMessage("Subiendo modificaciones....");
                progressDoalog.setTitle("Actualizando el Servidor");
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDoalog.setCancelable(false);
                progressDoalog.show();



                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void dropTable() {

        medidaOpenHelper.dropTable();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i(TAG, "onCreateLoader...");
        // Consultar todos los registros
        return new CursorLoader(
                this,
                ContractMedida.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        actualizarDatos();
        Log.i(TAG, "onLoadFinichsed...");
        if (progressDoalog.isShowing()) {
            progressDoalog.setProgress(progressDoalog.getMax());
            progressDoalog.dismiss();
        }else {
            consulta.setFin(data.getCount());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        actualizarDatos();
        Log.i(TAG, "onLoadReset...");
    }

    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        if (redes != null) {

            for (NetworkInfo rede : redes) {
                // Si alguna red tiene conexión, se devuelve true
                if (rede.getState() == NetworkInfo.State.CONNECTED) {
                    connected = true;
                }
            }
        }
        return connected;
    }

    @Override
    public void onBackPressed() {

    }




}


