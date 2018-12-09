package com.software.pyc.aguasfinal.provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.software.pyc.aguasfinal.data.MedidaDBHelper;
import com.software.pyc.aguasfinal.ui.ListaActivity;
import com.software.pyc.aguasfinal.R;

import java.net.Inet4Address;


/**
 * Created by pablo on 5/6/2018.
 */

public class CargaMedida extends DialogFragment  {


    String estAnt, estAct, id, med, comentario;
    String carga="TRUE";

    // Interfaz de comunicación
    OnSimpleDialogListener listener;


    public void Carga (Medida m){
        estAnt = m.getEstadoAnterior();
        estAct = m.getEstadoActual();
        med = m.getMedidor();
        comentario = m.getObservaciones();
        id = m.getId();

    }

    public interface OnSimpleDialogListener {
        void onPossitiveButtonClick();// Eventos Botón Positivo

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


            try {
                System.out.println("on Attach");

                listener = (OnSimpleDialogListener) activity;

            } catch (ClassCastException e) {
                throw new ClassCastException(
                        activity.toString() +
                                " no implementó OnSimpleDialogListener");

            }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();


        final View v = inflater.inflate(R.layout.dialog_carga, null);




        builder.setView(v);

        //Implementacion del spinner ruta
        Spinner spinner = v.findViewById(R.id.sp_comentarios);
        String[] comentarios_spinner = {"",
                                        "Medidor Roto",
                                        "No se puede acceder al medidor",
                                        "Medidor no existe"};

        spinner.setAdapter(new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_comentarios, comentarios_spinner));
        spinner.setSelection(0);

        for (int i = 0; i < comentarios_spinner.length; i++) {
            if (comentarios_spinner[i].equalsIgnoreCase(comentario)){
                spinner.setSelection(i);
            }

        }

        final String comentario = spinner.getSelectedItem().toString();
        final Button actualizar = (Button)v.findViewById(R.id.btnDialogCarga);
        final EditText estadoActual = (EditText)v.findViewById(R.id.etDialogEstAct);
        final TextView estadoAnterior = (TextView)v.findViewById(R.id.tvDialogEstAnt);
        TextView medidor = (TextView)v.findViewById(R.id.tvDialogMedidor);


        // Cargo los valores existentes
        TextView ea = (TextView)v.findViewById(R.id.itemAct);
        TextView en = (TextView)v.findViewById(R.id.itemAnt);

        estadoActual.setText(estAct);
        estadoAnterior.setText(estAnt);
        medidor.setText(med);




        actualizar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public  void onClick(View v) {


                        final SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
                        final String usuarioSesion = sessionManager.getUser();

                        View viewItemMedida = inflater.inflate(R.layout.item_lista,null);
                        final String valorEstadoActual = String.valueOf(estadoActual.getText());


                        // ACTUALIZAR EL DATO EN LA BASE POR EL ID
                        final MedidaDBHelper medidaOpenHelper = new MedidaDBHelper(getActivity().getApplicationContext(),ContractMedida.DATABASE_NAME,null,1);

                        if (estadoActual.getText().toString().equalsIgnoreCase("0")){
                            carga="FALSE";
                        }

                        Integer estAct;
                        try {
                             estAct = Integer.parseInt(estadoActual.getText().toString());
                        }catch (Exception ex){
                             estAct = 0;
                             // carga="FALSE";
                        }

                        Integer estAnt = Integer.parseInt(estadoAnterior.getText().toString());

                        if (estAct < estAnt){
                            AlertDialog dialog =  createSimpleDialog("El valor actual, no puede ser menor al anterior.");
                            dialog.show();
                            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                            textView.setTextSize(28);

                        }else {

                            if (estAct > estAnt + 100 ){
/*                                AlertDialog dialog =  createSimpleDialog("La medida actual supera por 100 a la anterior");
                                dialog.show();*/



                                AlertDialog dialog2 = new AlertDialog.Builder(getContext())
                                            .setTitle("La Medida nueva supera por 100 a la anteriod")
                                            .setMessage("Confirma la nueva medida?")
                                            .setPositiveButton("Confirmar",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int idInt) {
                                                            boolean controlCarga = medidaOpenHelper.cargaEstado(id, estadoActual.getText().toString(), comentario, carga, usuarioSesion);

                                                            Toast.makeText(getActivity(), "Valor nuevo: " + valorEstadoActual, Toast.LENGTH_SHORT).show();

                                                            listener.onPossitiveButtonClick();
                                                            dismiss();
                                                        }
                                                    })
                                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            }).show();
                                TextView tvMessage = (TextView) dialog2.findViewById(android.R.id.message);
                                tvMessage.setTextSize(25);
                                TextView tvTitle = (TextView) dialog2.findViewById(android.R.id.title);
                                if (tvTitle != null) {
                                    tvTitle.setTextSize(28);
                                }




 /*                               AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                                dialogo1.setTitle("La Medida nueva supera por 100 a la anteriod");
                                dialogo1.setMessage("Confirma la nueva medida?");
                                dialogo1.setCancelable(false);
                                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        aceptar();
                                    }
                                    private void aceptar() {
                                        boolean controlCarga = medidaOpenHelper.cargaEstado(id, estadoActual.getText().toString(), comentario, carga, usuarioSesion);

                                        Toast.makeText(getActivity(), "Valor nuevo: " + valorEstadoActual, Toast.LENGTH_SHORT).show();

                                        listener.onPossitiveButtonClick();
                                        dismiss();
                                    }




                                });
                                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        cancelar();
                                    }
                                    public void cancelar() {
                                        dismiss();
                                    }
                                });
                                dialogo1.show();*/



                            }
/*                            boolean controlCarga = medidaOpenHelper.cargaEstado(id, estadoActual.getText().toString(), comentario, carga, usuarioSesion);

                            Toast.makeText(getActivity(), "Valor nuevo: " + valorEstadoActual, Toast.LENGTH_SHORT).show();

                            listener.onPossitiveButtonClick();
                            dismiss();*/
                        }


                    }
                }
        );

        return builder.create();

    }




    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Advertencia")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });


        return builder.create();
    }


}
