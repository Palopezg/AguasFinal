package com.software.pyc.aguasfinal.provider;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.pyc.aguasfinal.R;
import com.software.pyc.aguasfinal.sync.SyncAdapter;
import com.software.pyc.aguasfinal.utils.Constantes;
import com.software.pyc.aguasfinal.utils.LogMedida;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorDeMedida extends RecyclerView.Adapter<AdaptadorDeMedida.ExpenseViewHolder> {

    private static final String TAG = SyncAdapter.class.getSimpleName();
        private Cursor cursor;
        private Context context;
        public List<Medida> lsMedida;


        public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

            // Campos respectivos de un item
            public TextView ruta;
            public TextView nombre;
            public TextView orden;
            public TextView codigo;
            public TextView medidor;
            public TextView partida;
            public TextView estAnt;
            public TextView estAct;
            public TextView actualizado;
            public TextView estado;
            public TextView diff;
            public ImageView statusIndicator;
            public View rl;
            public View cl;

            public ExpenseViewHolder(View v) {
                super(v);


                // Referencias UI.
                 ruta    = itemView.findViewById(R.id.itemRuta);
                 nombre  = itemView.findViewById(R.id.itemNombre);
                 orden   = itemView.findViewById(R.id.itemOrden);
                 codigo  = itemView.findViewById(R.id.itemCodigo);
                 medidor = itemView.findViewById(R.id.itemMedidor);
                 partida = itemView.findViewById(R.id.itemPartida);
                 estAnt  = itemView.findViewById(R.id.itemAnt);
                 estAct  = itemView.findViewById(R.id.itemAct);
                 diff    = itemView.findViewById(R.id.itemDiff);
                 statusIndicator = itemView.findViewById(R.id.imStatus);
                 rl =  itemView.findViewById(R.id.rlItem);
                 cl = itemView.findViewById(R.id.cl_item);
                //statusIndicator = itemView.findViewById(R.id.cl_card_title);

            }
        }

        public AdaptadorDeMedida(Context context) {
            this.context= context;

        }

        @Override
        public int getItemCount() {
            if (cursor!=null)
                return cursor.getCount();
            return 0;
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_lista, viewGroup, false);
            return new ExpenseViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExpenseViewHolder viewHolder, int i) {

            cursor.moveToPosition(i);

            String ruta;
            String orden;
            String codigo;
            String nombre;
            String medidor;
            String partida;
            String estAnt;
            String estAct;
            String estado;
            String actualizado;
            String diff;
            String comentario;
            int diferencia;
            //View statusIndicator = viewHolder.statusIndicator;

            ruta = cursor.getString(1);
            orden = cursor.getString(2);
            codigo = cursor.getString(3);
            nombre = cursor.getString(4);
            medidor = cursor.getString(5);
            partida = cursor.getString(6);
            estAnt = cursor.getString(7);
            estAct = cursor.getString(8);
            actualizado = cursor.getString(10);
            estado = cursor.getString(14);
            comentario = cursor.getString(12);

            String[] comentarios_spinner = {"Medidor Roto",
                                            "No se puede acceder al medidor",
                                            "Medidor no existe",
                                            "Otros"};

            Boolean medidor_roto = Boolean.FALSE;

            try {
                String[] separated = comentario.split(" -- ");
                if (separated[0] != null) {

                    for (String c : comentarios_spinner) {
                        if (c.equalsIgnoreCase(separated[0]))
                            medidor_roto = Boolean.TRUE;
                    }

                }
            }catch (Exception e){
                medidor_roto = Boolean.FALSE;
            }


            diferencia = 0;
            try
            {
          //      if ((estAct != null) && (!estAct.equals(""))){
          //          if ((estAnt != null) &&  (!estAnt.equals("")))
                        diferencia = Integer.parseInt(estAct) - Integer.parseInt(estAnt);
          //      }
            } catch (NumberFormatException nfe)
            {
                LogMedida.grabaLog(Constantes.LOG_BAJA_TABLA,"Basura en los datos de estados. Orden: "+orden,context);;
            }
            if (diferencia < 0){
                diff = String.valueOf('-');
            }else{
                diff = String.valueOf(diferencia);
            }

            viewHolder.ruta.setText(ruta);
            viewHolder.orden.setText(orden);
            viewHolder.codigo.setText(codigo);
            viewHolder.nombre.setText(nombre);
            viewHolder.medidor.setText(medidor);
            viewHolder.partida.setText(partida);
            viewHolder.estAnt.setText(estAnt);
            viewHolder.estAct.setText(estAct);
            viewHolder.diff.setText(diff);
            //viewHolder.estado.setText(actualizado);

            String p = viewHolder.ruta.getText().toString();
            Log.i(TAG, "estado: "+estado+"   actualizado: "+actualizado);

             if (actualizado != null) {
                 if (actualizado.equalsIgnoreCase(Constantes.CARGADO)) {
                     viewHolder.statusIndicator.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                     //viewHolder.statusIndicator.setBackgroundResource(R.color.bt_yellow);
                 }else {
                     if (actualizado.equalsIgnoreCase(Constantes.SYCRONIZADO)) {
                         viewHolder.statusIndicator.setImageResource(R.drawable.ic_sync_black_24dp);
//                         viewHolder.statusIndicator.setBackgroundResource(R.color.bt_green);
                     }
                 }
             }else{
                 viewHolder.statusIndicator.setImageResource(R.drawable.ic_sin_carga_on_black_24dp);
//                 viewHolder.statusIndicator.setBackgroundResource(R.color.bt_red);
             }

             if (medidor_roto){
                 viewHolder.rl.setBackgroundResource(R.color.color_med_roto);
//                 viewHolder.statusIndicator.setBackgroundResource(R.color.color_med_roto);
             }else{
                 viewHolder.rl.setBackgroundResource(R.color.color_med_ok);
             }



           // viewHolder.estado.setImageDrawable();

            getMedida(cursor);

        }

        public void getMedida(Cursor c){
            List<Medida> listaMedida = new ArrayList<>();

            // Si el cursor contiene datos los añadimos al List
            if (c.moveToFirst()) {
                do {

                    listaMedida.add(new Medida(c.getString(0),c.getString(1),c.getString(2),
                            c.getString(3),c.getString(4),c.getString(5),
                            c.getString(6),c.getString(7),c.getString(8),
                            c.getString(9),c.getString(10),c.getString(11),c.getString(12),
                            c.getString(13)));
                } while (c.moveToNext());
            }

            setLsMedida(listaMedida);
        }

        public void swapCursor(Cursor newCursor) {
            cursor = newCursor;
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return cursor;
        }

    public List<Medida> getLsMedida() {
        return lsMedida;
    }

    public void setLsMedida(List<Medida> lsMedida) {
        this.lsMedida = lsMedida;
    }
}
