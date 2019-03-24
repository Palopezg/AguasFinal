package com.software.pyc.aguasfinal.provider;


        import android.content.Context;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.software.pyc.aguasfinal.R;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by pablo on 10/12/2018.
 */

public class AdaptadorUsuarios extends ArrayAdapter<Usuario> {
    private Context context;
    private ArrayList datos;

    public AdaptadorUsuarios(Context context, List<Usuario> objects) {
        super(context,0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        //      if (null == convertView) {
        View itemView = inflater.inflate(R.layout.list_item_usuario,null);
        //      }

        // Referencias UI.

        TextView nombre = (TextView) itemView.findViewById(R.id.tv_name);
        ImageView imagen = itemView.findViewById(R.id.iv_avatar);

        // Lead actual.
        Usuario usuario = getItem(position);

        // Setup.

        nombre.setText(usuario.getNombre());
        imagen.setImageResource(R.drawable.ic_account_circle);


        return itemView;
    }
}
