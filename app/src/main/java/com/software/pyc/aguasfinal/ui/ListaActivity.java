package com.software.pyc.aguasfinal.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.software.pyc.aguasfinal.R;

import com.software.pyc.aguasfinal.provider.*;
import com.software.pyc.aguasfinal.data.MedidaDBHelper;
import com.software.pyc.aguasfinal.sync.SyncAdapter;

import java.util.List;

public class ListaActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ListaActivity.class.getSimpleName();


    static MedidaAdapter medidaAdapter;
    static Cursor c;
    static String rutaSeleccionada;
    static String opcionBusqueda;
    static String opcionBusquedaAux;
    static int posAnterior=1;
    static View viewAnterior = null;
    static Medida currentMedida = null;
    Toolbar toolbar;

    static Boolean firstTime = false;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AdaptadorDeMedida adapter;
    ProgressDialog progressDoalog;


    ConsultaMedida consulta = ConsultaMedida.getInstance();

    MedidaDBHelper medidaOpenHelper = new MedidaDBHelper(this,ContractMedida.DATABASE_NAME,null,1);
    static int posicionList =1;
    static int paddinList=0;
    static int posElegido=-1;
    Boolean flag=true;



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

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String usuarioSesion = sessionManager.getUser();

//        TextView usu = findViewById(R.id.tvUser);
//        TextView usur = findViewById(R.id.menu_usu);
//        usur.setText(sessionManager.getUser());


//      Inicializa los valores del panel lateral

        ConstraintLayout rl_ini =  findViewById(R.id.cl_card_title_pl);
        TextView ruta_ini = findViewById(R.id.itemRuta_pl);
        TextView medidor_ini = findViewById(R.id.itemMedidor_pl);
        TextView nombre_ini = findViewById(R.id.itemNombre_pl);
        TextView codigo_ini = findViewById(R.id.itemCodigo_pl);
        TextView partida_ini = findViewById(R.id.itemPartida_pl);
        TextView orden_ini = findViewById(R.id.itemOrden_pl);
        TextView estAnt_ini = findViewById(R.id.itemAnt_pl);

        rl_ini.setBackgroundColor(getResources().getColor(R.color.bt_bg_gris));
        ruta_ini.setText(null);
        medidor_ini.setText(null);
        nombre_ini.setText(null);
        codigo_ini.setText(null);
        partida_ini.setText(null);
        orden_ini.setText(null);
        estAnt_ini.setText(null);



        recyclerView = findViewById(R.id.reciclador);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdaptadorDeMedida(this);

        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);

        Button btnCargaMedida = findViewById(R.id.btnPLAceptar);



        btnCargaMedida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etCargaMedida = findViewById(R.id.etIngresarMedida_pl);
                EditText etObservaciones = findViewById(R.id.etObservaciones);

                if (currentMedida == null) {
                    Toast.makeText(getApplicationContext(), "Seleccione un item...", Toast.LENGTH_SHORT).show();
                }else {
                    if (etCargaMedida.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "Ingrese un valor nuevo...", Toast.LENGTH_SHORT).show();

                    } else {
                        boolean controlCarga = medidaOpenHelper.cargaEstado(currentMedida.getId(), etCargaMedida.getText().toString(), etObservaciones.getText().toString(), "TRUE", usuarioSesion);
                        etCargaMedida.setText(null);
                        posicionList = layoutManager.findFirstVisibleItemPosition();

                        actualizarDatos();
                    }
                }
            }
        });



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {


                        List<Medida> listaMedida = adapter.getLsMedida();

                        currentMedida = listaMedida.get(position);
                        posElegido = position;


                        if (viewAnterior != null) {
                            viewAnterior.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.borde_item_black));
                            viewAnterior.setElevation(0);
                        }
                        viewAnterior = view;

                        view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.borde_item));
                        view.setElevation(8);

                        ConstraintLayout rl_1 = findViewById(R.id.cl_card_title_pl);
                        TextView ruta_1 = findViewById(R.id.itemRuta_pl);
                        TextView medidor_1 = findViewById(R.id.itemMedidor_pl);
                        TextView nombre_1 = findViewById(R.id.itemNombre_pl);
                        TextView codigo_l = findViewById(R.id.itemCodigo_pl);
                        TextView partida_l = findViewById(R.id.itemPartida_pl);
                        TextView orden_l = findViewById(R.id.itemOrden_pl);
                        TextView estAnt_l = findViewById(R.id.itemAnt_pl);
                        EditText panelEstAct = findViewById(R.id.etIngresarMedida_pl);
                        EditText panelObsrevaciones = findViewById(R.id.etObservaciones);


                        panelEstAct.setText(currentMedida.getEstadoActual());
                        panelObsrevaciones.setText(currentMedida.getObservaciones());
                        ruta_1.setText(currentMedida.getRuta());
                        medidor_1.setText(currentMedida.getMedidor());
                        nombre_1.setText(currentMedida.getNombre());
                        codigo_l.setText(currentMedida.getCodigo());
                        partida_l.setText(currentMedida.getPartida());
                        orden_l.setText(currentMedida.getOrden());
                        estAnt_l.setText(currentMedida.getEstadoAnterior());
                        rl_1.setBackgroundColor(getResources().getColor(R.color.bt_blue));
                        if (currentMedida.getActualizado() != null) {
                            if (currentMedida.getActualizado().equalsIgnoreCase("TRUE")) {
                                rl_1.setBackgroundColor(getResources().getColor(R.color.bt_yellow));
                            } else {
                                if (currentMedida.getActualizado().equalsIgnoreCase("SYNC")) {
                                    rl_1.setBackgroundColor(getResources().getColor(R.color.bt_green));
                                }
                            }
                        } else {
                            rl_1.setBackgroundColor(getResources().getColor(R.color.bt_red));
                        }
                    }




                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        //Implementacion del spinner buscar
        Spinner spinnerOrderBy =  findViewById(R.id.spOrderBy);

        String[] order_spinner = {"Ordenar por Nombre","Ordenar por Partida","Ordenar por Medidor"};

        spinnerOrderBy.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, order_spinner));

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)

            {

                String ordenSeleccionada = String.valueOf(adapterView.getItemAtPosition(pos));

                switch (ordenSeleccionada){
                    case "Ordenar por Nombre":
                        if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                            consulta.setOrderByMetodo("asc");
                        }else{
                            consulta.setOrderByMetodo("desc");
                        }

                        consulta.setOrderBy(ContractMedida.Columnas.NOMBRE);
                        posicionList=1;
                        actualizarDatos();
                        break;
                    case "Ordenar por Partida":
                        if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                            consulta.setOrderByMetodo("asc");
                        }else{
                            consulta.setOrderByMetodo("desc");
                        }

                        consulta.setOrderBy(ContractMedida.Columnas.ORDEN);
                        posicionList=1;
                        actualizarDatos();
                        break;
                    case "Ordenar por Medidor":
                        if (consulta.getOrderByMetodo().equalsIgnoreCase("desc")){
                            consulta.setOrderByMetodo("asc");
                        }else{
                            consulta.setOrderByMetodo("desc");
                        }

                        consulta.setOrderBy(ContractMedida.Columnas.MEDIDOR);
                        posicionList=1;
                        actualizarDatos();
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        //Implementacion del spinner ruta
        Spinner spinner = findViewById(R.id.spRuta);
        String[] ruta_spinner = {"Ruta 1","Ruta 2","Ruta 3","Ruta 4"};

        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, ruta_spinner));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)

            {
                String ordenSel = String.valueOf(adapterView.getItemAtPosition(pos));
                switch (ordenSel){
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

                rutaSeleccionada = ContractMedida.Columnas.RUTA+"="+String.valueOf(ordenSel);

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

        String[] busqueda_spinner = {"Nombre","Partida","Medidor"};


        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busquedaValor = etbusqueda.getText().toString();


                opcionBusqueda= ContractMedida.Columnas.NOMBRE+" like '%"+busquedaValor+"%' or "
                              +ContractMedida.Columnas.PARTIDA+" like '%"+busquedaValor+"%' or "
                              +ContractMedida.Columnas.MEDIDOR+" like '%"+busquedaValor+"%'";

//                opcionBusqueda=opcionBusquedaAux+" like '%"+busquedaValor+"%'";
                Log.i(TAG, opcionBusqueda);
                consulta.addWhereAnd(opcionBusqueda);
                consulta.setOffset(1);

                actualizarDatos();

            }
        });



        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildLayoutPosition(v);
                Medida currentMedida = medidaAdapter.getItem(position);

                CargaMedida d = new CargaMedida();
                d.Carga(currentMedida);
                d.show(getFragmentManager(),"");

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logOut();
        }

        if (id == R.id.action_sync) {
            if (!compruebaConexion(this)) {
                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            } else {
                medidaOpenHelper.onDelete();
                Log.i(TAG, "medida borrada.");

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



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

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
    }

    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

}


