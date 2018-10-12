package com.software.pyc.aguasfinal.provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.data.MedidaDBHelper;


/**
 * Created by pablo on 5/6/2018.
 */

public class CargaMedida extends DialogFragment  {


    String estAnt, estAct, id, medidor;

    // Interfaz de comunicación
    OnSimpleDialogListener listener;

    public void Carga (Medida m){
        estAnt = m.getEstadoAnterior();
        estAct = m.getEstadoActual();
        medidor = m.getMedidor();
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

        Button actualizar = (Button)v.findViewById(R.id.btnDialogCarga);
        final EditText estadoActual = (EditText)v.findViewById(R.id.etDialogEstAct);
        TextView estadoAnterior = (TextView)v.findViewById(R.id.tvDialogEstAnt);
        TextView tvmedidor = (TextView)v.findViewById(R.id.tvDialogMedidor);

        // Cargo los valores existentes
/*        TextView ea = (TextView)v.findViewById(R.id.itemAct);
        TextView en = (TextView)v.findViewById(R.id.itemAnt);
        TextView med = (TextView)v.findViewById(R.id.itemMedidor);*/

        estadoActual.setText(estAct);
        estadoAnterior.setText(estAnt);
        tvmedidor.setText(medidor);




        actualizar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public  void onClick(View v) {

                        String carga="TRUE";


                        View viewItemMedida = inflater.inflate(R.layout.card_item,null);
                        String valorEstadoActual = String.valueOf(estadoActual.getText());


                        // ACTUALIZAR EL DATO EN LA BASE POR EL ID
                        MedidaDBHelper medidaOpenHelper = new MedidaDBHelper(getActivity().getApplicationContext(), ContractMedida.DATABASE_NAME,null,1);

                        if (estadoActual.getText().toString().equalsIgnoreCase("0")){
                            carga="FALSE";
                        }
                        boolean controlCarga = medidaOpenHelper.cargaEstado(id, estadoActual.getText().toString(),null,carga,null);


                        Toast.makeText(getActivity(),"Valor nuevo: "+ valorEstadoActual,Toast.LENGTH_SHORT ).show();


                        listener.onPossitiveButtonClick();

                        dismiss();
                    }
                }
        );

        return builder.create();

    }


}

